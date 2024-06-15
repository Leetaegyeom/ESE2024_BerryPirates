import sys
sys.path.append('./communication')
sys.path.append('./control')

from Control import Control
from Bluetooth import Bluetooth
import time

RATIO = 12.5/5
OFFSET = 9.5

class FOOTREEDOM:
    def __init__(self):
        self.control = Control()
        print("CONTROL SETTING COMPLETE __main.py")
        self.bluetooth = Bluetooth()
        print("BLUETOOTH SETTING COMPLETE __main.py")

    def run(self):
        main_signal = self.bluetooth.get_main_signal()
        self.bluetooth.app_control_characteristic.done = False

        if main_signal.app_control_on and not(main_signal.foot_control_on): # app control mode
            time.sleep(1)
            print("APP CONTROL MODE ON !!")
            ref_value = self.bluetooth.get_ref_value()
            self.value_converter(ref_value, "step_to_value")
            print("\nREF VALUE :\nR_HEIGHT : %f, R_ANGLE :%f, L_HEIGHT : %f, L_ANGLE : \n%f"%(ref_value.right_height, ref_value.right_angle, ref_value.left_height, ref_value.left_angle))
            self.control.position_control(ref_value)
            self.bluetooth.send_done(True)
            self.bluetooth.main_signal_characteristic.value[2] = False

        elif not(main_signal.app_control_on) and main_signal.foot_control_on: # foot control mode
            while True:
                main_signal = self.bluetooth.get_main_signal()
                if not(main_signal.foot_control_on):
                    break
                foot_control_signal = self.bluetooth.get_foot_control_signal()
                self.control.foot_control(fix_angular = foot_control_signal.fix_angular, fix_height = foot_control_signal.fix_height)
                if foot_control_signal.save_pose:
                    save_pose = self.control.get_value()
                    save_pose = self.value_converter(save_pose, "value_to_step")
                    self.bluetooth.send_save_pose(save_pose)
                    self.bluetooth.foot_control_signal_characteristic.value[2] = False

    def value_converter(sef, value, type):
        if type == "value_to_step":
            value[1] = (value[1] - OFFSET) / RATIO
            value[3] = (value[3] - OFFSET) / RATIO
            value[0] = value[0] / 4
            value[2] = value[2] / 4
        
        elif type == "step_to_value":
            value.left_height = value.left_height * RATIO + OFFSET
            value.right_height = value.right_height * RATIO + OFFSET
        
        return value

if __name__ == "__main__":
    footreedom = FOOTREEDOM()
    try:
        while True:
            footreedom.run()
    except KeyboardInterrupt:
        footreedom.control.cleanup_all_actuator()