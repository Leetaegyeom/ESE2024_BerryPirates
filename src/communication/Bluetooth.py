import sys
import dbus, dbus.mainloop.glib
import threading
import time
from gi.repository import GLib
from example_advertisement import Advertisement
from example_advertisement import register_ad_cb, register_ad_error_cb
from example_gatt_server import Service, Characteristic
from example_gatt_server import register_app_cb, register_app_error_cb

import SignalField

BLUEZ_SERVICE_NAME = 'org.bluez'
DBUS_OM_IFACE = 'org.freedesktop.DBus.ObjectManager'
LE_ADVERTISING_MANAGER_IFACE = 'org.bluez.LEAdvertisingManager1'
GATT_MANAGER_IFACE = 'org.bluez.GattManager1'
GATT_CHRC_IFACE = 'org.bluez.GattCharacteristic1'

UART_SERVICE_UUID = '6e400001-b5a3-f393-e0a9-e50e24dcca9e'
LOCAL_NAME = 'Footreedom'
mainloop = None

class SignalCharacteristic(Characteristic):
    SIGNAL_CHARACTERISTIC_UUID = '6e400002-b5a3-f393-e0a9-e50e24dcca9e'

    def __init__(self, bus, index, service):
        Characteristic.__init__(self, bus, index, self.SIGNAL_CHARACTERISTIC_UUID,
                                ['read', 'write'], service)
        self.value = [False, False, False]

    def WriteValue(self, value, options):
        self.value = [bool(x) for x in value]
        self.on_value_changed()
        print(f'Signal updated: {self.value}')

    def on_value_changed(self):
        self.power_off = self.value[0]
        self.foot_control_on = self.value[1]
        self.app_control_on = self.value[2]

    def get_signals(self):
        return self.value

class FootControlCharacteristic(Characteristic):
    FOOT_CONTROL_CHARACTERISTIC_UUID = '6e400003-b5a3-f393-e0a9-e50e24dcca9e'

    def __init__(self, bus, index, service):
        Characteristic.__init__(self, bus, index, self.FOOT_CONTROL_CHARACTERISTIC_UUID,
                                ['read', 'write'], service)
        self.value = [False, False, False]

    def WriteValue(self, value, options):
        self.value = [bool(x) for x in value]
        self.on_value_changed()
        print(f'FootControl updated: {self.value}')

    def on_value_changed(self):
        self.fix_angular = self.value[0]
        self.fix_height = self.value[1]
        self.save_pose = self.value[2]

    def get_foot_controls(self):
        return self.value

class AppControlCharacteristic(Characteristic):
    APP_CONTROL_CHARACTERISTIC_UUID = '6e400004-b5a3-f393-e0a9-e50e24dcca9e'

    def __init__(self, bus, index, service):
        Characteristic.__init__(self, bus, index, self.APP_CONTROL_CHARACTERISTIC_UUID,
                                ['read', 'write'], service)
        self.value = [0.0, 0.0, 0.0, 0.0]

    def WriteValue(self, value, options):
        self.value = [float(x) for x in value]
        self.on_value_changed()
        print(f'AppControl updated: {self.value}')

    def on_value_changed(self):
        self.left_angle = self.value[0]
        self.left_height = self.value[1]
        self.right_angle = self.value[2]
        self.right_height = self.value[3]

    def get_app_controls(self):
        return self.value

class RecordCharacteristic(Characteristic):
    RECORD_CHARACTERISTIC_UUID = '6e400005-b5a3-f393-e0a9-e50e24dcca9e'

    def __init__(self, bus, index, service):
        Characteristic.__init__(self, bus, index, self.RECORD_CHARACTERISTIC_UUID,
                                ['read'], service)
        self.value = [0.0, 0.0, 0.0, 0.0]

    # def send_record(self, left_angle, left_height, right_angle, right_height):
    #     self.value = [left_angle, left_height, right_angle, right_height]
    #     self.PropertiesChanged(GATT_CHRC_IFACE, {'Value': self.value}, [])
    #     print(f'Record sent: {self.value}')

    def send_record(self, pose):
        left_angle, left_height, right_angle, right_height = pose[0], pose[1], pose[2], pose[3]
        self.value = [left_angle, left_height, right_angle, right_height]
        self.PropertiesChanged(GATT_CHRC_IFACE, {'Value': self.value}, [])
        print(f'Record sent: {self.value}')

    def ReadValue(self, options):
        return self.value

class UartService(Service):
    def __init__(self, bus, index):
        Service.__init__(self, bus, index, UART_SERVICE_UUID, True)
        self.signal_characteristic = SignalCharacteristic(bus, 0, self)
        self.foot_control_characteristic = FootControlCharacteristic(bus, 1, self)
        self.app_control_characteristic = AppControlCharacteristic(bus, 2, self)
        self.record_characteristic = RecordCharacteristic(bus, 3, self)

        self.add_characteristic(self.signal_characteristic)
        self.add_characteristic(self.foot_control_characteristic)
        self.add_characteristic(self.app_control_characteristic)
        self.add_characteristic(self.record_characteristic)

class Application(dbus.service.Object):
    def __init__(self, bus):
        self.path = '/'
        self.services = []
        dbus.service.Object.__init__(self, bus, self.path)

    def get_path(self):
        return dbus.ObjectPath(self.path)

    def add_service(self, service):
        self.services.append(service)

    @dbus.service.method(DBUS_OM_IFACE, out_signature='a{oa{sa{sv}}}')
    def GetManagedObjects(self):
        response = {}
        for service in self.services:
            response[service.get_path()] = service.get_properties()
            chrcs = service.get_characteristics()
            for chrc in chrcs:
                response[chrc.get_path()] = chrc.get_properties()
        return response

class UartApplication(Application):
    def __init__(self, bus):
        Application.__init__(self, bus)
        self.uart_service = UartService(bus, 0)
        self.add_service(self.uart_service)
    
class UartAdvertisement(Advertisement):
    def __init__(self, bus, index):
        Advertisement.__init__(self, bus, index, 'peripheral')
        self.add_service_uuid(UART_SERVICE_UUID)
        self.add_local_name(LOCAL_NAME)
        self.include_tx_power = True

class Bluetooth:
    def __init__(self):
        global mainloop
        dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
        bus = dbus.SystemBus()
        adapter = self.find_adapter(bus)
        if not adapter:
            print('BLE adapter not found')
            return

        ad_manager = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, adapter),
                                    LE_ADVERTISING_MANAGER_IFACE)
        service_manager = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, adapter),
                                        GATT_MANAGER_IFACE)

        adv = UartAdvertisement(bus, 0)
        app = UartApplication(bus)

        # 인스턴스 꺼내오기
        self.signal_characteristic = app.uart_service.signal_characteristic
        self.foot_control_characteristic = app.uart_service.foot_control_characteristic
        self.app_control_characteristic = app.uart_service.app_control_characteristic
        self.record_characteristic = app.uart_service.record_characteristic

        mainloop = GLib.MainLoop()

        ad_manager.RegisterAdvertisement(adv.get_path(), {},
                                        reply_handler=register_ad_cb,
                                        error_handler=register_ad_error_cb)

        service_manager.RegisterApplication(app.get_path(), {},
                                            reply_handler=register_app_cb,
                                            error_handler=register_app_error_cb)

        mainloop_thread = threading.Thread(target=mainloop.run())
        mainloop_thread.start()

    def find_adapter(self, bus):
        remote_om = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, '/'),
                                DBUS_OM_IFACE)
        objects = remote_om.GetManagedObjects()
        for o, props in objects.items():
            if LE_ADVERTISING_MANAGER_IFACE in props and GATT_MANAGER_IFACE in props:
                return o
            print('Skip adapter:', o)
        return None

    def get_bluetooth_signals(self):
        return self.signal_characteristic.value, self.foot_control_characteristic.value, self.app_control_characteristic.value, self.record_characteristic.value

    def send_save_pose(self, save_pose):
        self.record_characteristic.send_record(save_pose)
