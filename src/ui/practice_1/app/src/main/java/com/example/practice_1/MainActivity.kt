package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    // Bluetooth variables
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var mainSignalCharacteristic: BluetoothGattCharacteristic? = null

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val SIGNAL_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        val power_off_button: ImageButton = findViewById(R.id.power_off_button)
        power_off_button.setOnClickListener {
            updateMainSignalCharacteristic(0, true)
        }

        val foot_control_button: ImageButton = findViewById(R.id.foot_control_button)
        foot_control_button.setOnClickListener {
            updateMainSignalCharacteristic(1, true)
            val intent = Intent(this@MainActivity, FootControlActivity::class.java)
            startActivity(intent)
        }

        val app_control_button: ImageButton = findViewById(R.id.app_control_button)
        app_control_button.setOnClickListener {
            updateMainSignalCharacteristic(2, true)
            val intent = Intent(this@MainActivity, AppControlActivity::class.java)
            startActivity(intent)
        }

        val scan_button: ImageButton = findViewById(R.id.scan_button)
        scan_button.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanActivity::class.java)
            startActivity(intent)
        }

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            val intent = Intent(this@MainActivity, RecordActivity::class.java)
            startActivity(intent)
        }

        // 이전에 저장된 장치 주소를 가져와서 연결
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            Toast.makeText(this, "저장된 디바이스 주소가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMainSignalCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || mainSignalCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 특성 값이 초기화되지 않았을 경우 초기화
        if (mainSignalCharacteristic!!.value == null) {
            mainSignalCharacteristic!!.value = ByteArray(4) // 적절한 크기의 배열로 초기화
        }

        val signalValues = mainSignalCharacteristic!!.value.copyOf()
        signalValues[index] = if (value) 1 else 0
        mainSignalCharacteristic!!.value = signalValues
        bluetoothGatt!!.writeCharacteristic(mainSignalCharacteristic)
    }


    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@MainActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@MainActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val uartService = gatt.getService(UART_SERVICE_UUID)
                mainSignalCharacteristic = uartService?.getCharacteristic(SIGNAL_CHARACTERISTIC_UUID)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread { Toast.makeText(this@MainActivity, "신호가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show() }
            } else {
                runOnUiThread { Toast.makeText(this@MainActivity, "신호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
