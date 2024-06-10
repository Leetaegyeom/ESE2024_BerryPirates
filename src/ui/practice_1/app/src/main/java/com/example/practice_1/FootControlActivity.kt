package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_1.BluetoothLeService.Companion.SIGNAL_CHARACTERISTIC_UUID
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.util.*

class FootControlActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var footControlCharacteristic: BluetoothGattCharacteristic? = null
    private var mainSignalCharacteristic: BluetoothGattCharacteristic? = null
    private var recordCharacteristic: BluetoothGattCharacteristic? = null

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val FOOT_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val RECORD_CHARACTERISTIC_UUID = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e")

    private val db = Firebase.firestore
    private var poseName: String? = null

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
            showSaveDialog()
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("자세 이름 입력")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("저장") { dialog, which ->
            poseName = input.text.toString()
            if (!poseName.isNullOrEmpty()) {
                updateFootControlCharacteristic(2, true)
                readAndSaveRecordCharacteristic()
            } else {
                Toast.makeText(this, "자세 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }

        builder.show()
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
            Log.d("FootControlActivity", "MainSignalCharacteristic update 요청 완료")
        }
    }

    private fun readAndSaveRecordCharacteristic() {
        if (bluetoothGatt == null || recordCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        bluetoothGatt?.readCharacteristic(recordCharacteristic)
        bluetoothGatt?.setCharacteristicNotification(recordCharacteristic, true)
        recordCharacteristic?.let {
            it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))?.let { descriptor ->
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor)
            }
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
                recordCharacteristic = uartService?.getCharacteristic(RECORD_CHARACTERISTIC_UUID)
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.uuid == RECORD_CHARACTERISTIC_UUID) {
                    val values = characteristic.value
                    val leftAngle = values[0].toFloat()
                    val leftHeight = values[1].toFloat()
                    val rightAngle = values[2].toFloat()
                    val rightHeight = values[3].toFloat()

                    poseName?.let {
                        savePoseToFirestore(leftAngle, leftHeight, rightAngle, rightHeight, it)
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@FootControlActivity, "자세 데이터를 읽어오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
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

    private fun savePoseToFirestore(leftAngle: Float, leftHeight: Float, rightAngle: Float, rightHeight: Float, poseName: String) {
        val poseData = hashMapOf(
            "leftAngle" to leftAngle,
            "leftHeight" to leftHeight,
            "rightAngle" to rightAngle,
            "rightHeight" to rightHeight
        )

        db.collection("poses").document(poseName)
            .set(poseData)
            .addOnSuccessListener {
                Toast.makeText(this, "자세가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "자세 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
