package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_1.BluetoothLeService.Companion.SIGNAL_CHARACTERISTIC_UUID
import java.util.*

class FootControlActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var footControlCharacteristic: BluetoothGattCharacteristic? = null
    private var mainSignalCharacteristic: BluetoothGattCharacteristic? = null
    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val FOOT_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foot_control)

        initializeUIElements()

        // 이전에 저장된 장치 주소를 가져와서 연결 시도
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            // 저장된 디바이스 주소가 없으면 ScanActivity로 이동
            val intent = Intent(this@FootControlActivity, ScanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeUIElements() {
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            updateMainSignalCharacteristic(1, false)
            updateFootControlCharacteristic(0, false)
            updateFootControlCharacteristic(1, false)
            val intent = Intent(this@FootControlActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val recordButton: ImageButton = findViewById(R.id.record_button)
        recordButton.setOnClickListener {
            val intent = Intent(this@FootControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }

        val angleLockButton: ImageButton = findViewById(R.id.angle_lock_button)
        angleLockButton.setOnClickListener {
            toggleFootControlCharacteristic(0)
        }

        val heightLockButton: ImageButton = findViewById(R.id.height_lock_button)
        heightLockButton.setOnClickListener {
            toggleFootControlCharacteristic(1)
        }

        val savePoseButton: ImageButton = findViewById(R.id.save_pose_button)
        savePoseButton.setOnClickListener {
            updateFootControlCharacteristic(2, true)
        }
    }

    private fun toggleFootControlCharacteristic(index: Int) {
        if (bluetoothGatt == null || footControlCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (footControlCharacteristic!!.value == null) {
            footControlCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val controlValues = footControlCharacteristic!!.value.copyOf()
        controlValues[index] = if (controlValues[index].toInt() == 1) 0 else 1
        footControlCharacteristic!!.value = controlValues
        bluetoothGatt!!.writeCharacteristic(footControlCharacteristic)
    }

    private fun updateFootControlCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || footControlCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (footControlCharacteristic!!.value == null) {
            footControlCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val controlValues = footControlCharacteristic!!.value.copyOf()
        controlValues[index] = if (value) 1 else 0
        footControlCharacteristic!!.value = controlValues
        bluetoothGatt!!.writeCharacteristic(footControlCharacteristic)
    }

    private fun updateMainSignalCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || mainSignalCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (mainSignalCharacteristic!!.value == null) {
            mainSignalCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val signalValues = mainSignalCharacteristic!!.value.copyOf()
        signalValues[index] = if (value) 1 else 0
        mainSignalCharacteristic!!.value = signalValues
        bluetoothGatt!!.writeCharacteristic(mainSignalCharacteristic).also {
            Log.d("AppControlActivity", "MainSignalCharacteristic update 요청 완료")
        }
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
                mainSignalCharacteristic = uartService?.getCharacteristic(SIGNAL_CHARACTERISTIC_UUID)
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

    override fun onBackPressed() {
        super.onBackPressed()
        updateMainSignalCharacteristic(1, false)
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
