package com.example.practice_1

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

class BluetoothLeService : Service() {

    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    companion object {
        const val ACTION_GATT_CONNECTED = "com.example.practice_1.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.practice_1.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "com.example.practice_1.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.practice_1.ACTION_DATA_AVAILABLE"
        val UART_SERVICE_UUID: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        val SIGNAL_CHARACTERISTIC_UUID: UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    fun connect(deviceAddress: String): Boolean {
        Log.d("BluetoothLeService", "Trying to connect to $deviceAddress")
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress) ?: return false
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        if (bluetoothGatt == null) {
            Log.e("BluetoothLeService", "Failed to create BluetoothGatt object")
            return false
        }
        Log.d("BluetoothLeService", "BluetoothGatt object created successfully")
        return true
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d("BluetoothLeService", "onConnectionStateChange: status = $status, newState = $newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BluetoothLeService", "Connected to GATT server.")
                broadcastUpdate(ACTION_GATT_CONNECTED)
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothLeService", "Disconnected from GATT server.")
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            } else {
                Log.d("BluetoothLeService", "onConnectionStateChange: newState = $newState, status = $status")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d("BluetoothLeService", "onServicesDiscovered: status = $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothLeService", "Services discovered.")
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w("BluetoothLeService", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d("BluetoothLeService", "onCharacteristicRead: ${characteristic.uuid}, status = $status")
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d("BluetoothLeService", "onCharacteristicWrite: ${characteristic.uuid}, status = $status")
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    fun getBluetoothGatt(): BluetoothGatt? {
        return bluetoothGatt
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
