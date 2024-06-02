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

class FootControlActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var footControlCharacteristic: BluetoothGattCharacteristic? = null

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val FOOT_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foot_control)

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '홈' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@FootControlActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@FootControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }

        val angleLockButton: ImageButton = findViewById(R.id.angle_lock_button)
        angleLockButton.setOnClickListener {
            updateFootControlCharacteristic(0, true)
        }

        val heightLockButton: ImageButton = findViewById(R.id.height_lock_button)
        heightLockButton.setOnClickListener {
            updateFootControlCharacteristic(1, true)
        }

        val savePoseButton: ImageButton = findViewById(R.id.save_pose_button)
        savePoseButton.setOnClickListener {
            updateFootControlCharacteristic(2, true)
        }

        // 이전에 저장된 장치 주소를 가져와서 연결
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            Toast.makeText(this, "저장된 디바이스 주소가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFootControlCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || footControlCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 특성 값이 초기화되지 않았을 경우 초기화
        if (footControlCharacteristic!!.value == null) {
            footControlCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val controlValues = footControlCharacteristic!!.value.copyOf()
        controlValues[index] = if (value) 1 else 0
        footControlCharacteristic!!.value = controlValues
        bluetoothGatt!!.writeCharacteristic(footControlCharacteristic)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@FootControlActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@FootControlActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val uartService = gatt.getService(UART_SERVICE_UUID)
                footControlCharacteristic = uartService?.getCharacteristic(FOOT_CONTROL_CHARACTERISTIC_UUID)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            runOnUiThread {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Toast.makeText(this@FootControlActivity, "신호가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FootControlActivity, "신호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
