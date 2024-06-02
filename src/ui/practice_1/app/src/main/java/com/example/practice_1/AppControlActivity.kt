package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.util.*
import androidx.appcompat.app.AlertDialog


class AppControlActivity : AppCompatActivity() {
    private var leftHeight = 0
    private var leftAngle = 0
    private var rightHeight = 0
    private var rightAngle = 0
    var isTwoFeetAtTheSameTimeEnabled = false
    val db = Firebase.firestore

    // Bluetooth variables
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var appControlCharacteristic: BluetoothGattCharacteristic? = null

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val APP_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e")

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_control)

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        // 왼발 높이 조절 버튼
        val leftHeightIncreaseButton: Button = findViewById(R.id.leftHeightIncreaseButton)
        val leftHeightDecreaseButton: Button = findViewById(R.id.leftHeightDecreaseButton)
        val leftHeightTextView: TextView = findViewById(R.id.leftHeightTextView)
        val leftAngleIncreaseButton: Button = findViewById(R.id.leftAngleIncreaseButton)
        val leftAngleDecreaseButton: Button = findViewById(R.id.leftAngleDecreaseButton)
        val leftAngleTextView: TextView = findViewById(R.id.leftAngleTextView)
        val rightHeightIncreaseButton: Button = findViewById(R.id.rightHeightIncreaseButton)
        val rightHeightDecreaseButton: Button = findViewById(R.id.rightHeightDecreaseButton)
        val rightHeightTextView: TextView = findViewById(R.id.rightHeightTextView)
        val rightAngleIncreaseButton: Button = findViewById(R.id.rightAngleIncreaseButton)
        val rightAngleDecreaseButton: Button = findViewById(R.id.rightAngleDecreaseButton)
        val rightAngleTextView: TextView = findViewById(R.id.rightAngleTextView)

        leftHeightIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                rightHeight = (rightHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            } else {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
            }
        }

        leftHeightDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            } else {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
            }
        }

        leftAngleIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle + 1).coerceIn(-5, 5)
                rightAngle = (rightAngle + 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            } else {
                leftAngle = (leftAngle + 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
            }
        }

        leftAngleDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle - 1).coerceIn(-5, 5)
                rightAngle = (rightAngle - 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            } else {
                leftAngle = (leftAngle - 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
            }
        }

        rightHeightIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                rightHeight = (rightHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            } else {
                rightHeight = (rightHeight + 1).coerceIn(0, 5)
                rightHeightTextView.text = rightHeight.toString()
            }
        }

        rightHeightDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            } else {
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                rightHeightTextView.text = rightHeight.toString()
            }
        }

        rightAngleIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle + 1).coerceIn(-5, 5)
                rightAngle = (rightAngle + 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            } else {
                rightAngle = (rightAngle + 1).coerceIn(-5, 5)
                rightAngleTextView.text = rightAngle.toString()
            }
        }

        rightAngleDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle - 1).coerceIn(-5, 5)
                rightAngle = (rightAngle - 1).coerceIn(-5, 5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            } else {
                rightAngle = (rightAngle - 1).coerceIn(-5, 5)
                rightAngleTextView.text = rightAngle.toString()
            }
        }

        val twoFeetAtTheSameTimeButton: ImageButton = findViewById(R.id.twoFeetAtTheSameTimeButton)
        twoFeetAtTheSameTimeButton.setOnClickListener {
            isTwoFeetAtTheSameTimeEnabled = !isTwoFeetAtTheSameTimeEnabled
            if (isTwoFeetAtTheSameTimeEnabled) {
                Toast.makeText(this, "두 발의 높이와 각도를 동시에 조절합니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "두 발의 높이와 각도를 동시에 조절하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

              // '이 자세로 발 받침대 조절하기' 버튼 클릭 이벤트
        val finalAppControlButton: ImageButton = findViewById(R.id.finalAppControlButton)
        finalAppControlButton.setOnClickListener {
            sendAppControlValues()
        }

        // '자세 저장하기' 버튼 클릭 이벤트
        val savePoseButton: ImageButton = findViewById(R.id.savePoseButton)
        savePoseButton.setOnClickListener {
            showSaveDialog()
        }



        // 이전에 저장된 장치 주소를 가져와서 연결
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            Toast.makeText(this, "저장된 디바이스 주소가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '홈' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@AppControlActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@AppControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("자세 이름 입력")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("저장") { dialog, which ->
            val poseName = input.text.toString()
            if (poseName.isNotEmpty()) {
                savePose(poseName)
            } else {
                Toast.makeText(this, "자세 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun savePose(poseName: String) {
        val poseData = hashMapOf(
            "leftHeight" to leftHeight,
            "leftAngle" to leftAngle,
            "rightHeight" to rightHeight,
            "rightAngle" to rightAngle
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
    private fun sendAppControlValues() {
        if (bluetoothGatt == null || appControlCharacteristic == null) {
            Toast.makeText(this, "장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val values = byteArrayOf(
            leftHeight.toByte(),
            leftAngle.toByte(),
            rightHeight.toByte(),
            rightAngle.toByte()
        )
        appControlCharacteristic?.value = values
        bluetoothGatt?.writeCharacteristic(appControlCharacteristic)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@AppControlActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@AppControlActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val uartService = gatt.getService(UART_SERVICE_UUID)
                appControlCharacteristic = uartService?.getCharacteristic(APP_CONTROL_CHARACTERISTIC_UUID)
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread { Toast.makeText(this@AppControlActivity, "발 받침대가 조절되었습니다.", Toast.LENGTH_SHORT).show() }
            } else {
                runOnUiThread { Toast.makeText(this@AppControlActivity, "발 받침대 조절에 실패했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}

