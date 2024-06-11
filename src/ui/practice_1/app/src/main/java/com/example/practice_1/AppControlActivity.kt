package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.nio.ByteBuffer
import java.util.*



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
    private var mainSignalCharacteristic: BluetoothGattCharacteristic? = null

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val APP_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e")
    private val SIGNAL_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_control)

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        // UI 요소 초기화
        initializeUIElements()

        // 이전에 저장된 장치 주소를 가져와서 연결 시도
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            // 저장된 디바이스 주소가 없으면 ScanActivity로 이동
            val intent = Intent(this@AppControlActivity, ScanActivity::class.java)
            startActivity(intent)
        }

        // 버튼 클릭 이벤트 초기화
        initializeButtonClickEvents()
    }

    private fun initializeUIElements() {
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

        // 버튼 클릭 리스너 설정
        setButtonListeners(leftHeightIncreaseButton, leftHeightDecreaseButton, leftHeightTextView, ::increaseLeftHeight, ::decreaseLeftHeight)
        setButtonListeners(leftAngleIncreaseButton, leftAngleDecreaseButton, leftAngleTextView, ::increaseLeftAngle, ::decreaseLeftAngle)
        setButtonListeners(rightHeightIncreaseButton, rightHeightDecreaseButton, rightHeightTextView, ::increaseRightHeight, ::decreaseRightHeight)
        setButtonListeners(rightAngleIncreaseButton, rightAngleDecreaseButton, rightAngleTextView, ::increaseRightAngle, ::decreaseRightAngle)
    }

    private fun initializeButtonClickEvents() {
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
            updateMainSignalCharacteristic(2, true)
            sendAppControlValues()
        }

        // '자세 저장하기' 버튼 클릭 이벤트
        val savePoseButton: ImageButton = findViewById(R.id.savePoseButton)
        savePoseButton.setOnClickListener {
            showSaveDialog()
        }

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '홈' 버튼 클릭 시 MainActivity로 이동
            updateMainSignalCharacteristic(2, false)
            val intent = Intent(this@AppControlActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@AppControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setButtonListeners(
        increaseButton: Button,
        decreaseButton: Button,
        textView: TextView,
        increaseAction: () -> Int,
        decreaseAction: () -> Int
    ) {
        increaseButton.setOnClickListener {
            textView.text = increaseAction().toString()
        }
        decreaseButton.setOnClickListener {
            textView.text = decreaseAction().toString()
        }
    }

    private fun increaseLeftHeight(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftHeight = (leftHeight + 1).coerceIn(0, 5)
            rightHeight = (rightHeight + 1).coerceIn(0, 5)
            leftHeight
        } else {
            leftHeight = (leftHeight + 1).coerceIn(0, 5)
            leftHeight
        }
    }

    private fun decreaseLeftHeight(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftHeight = (leftHeight - 1).coerceIn(0, 5)
            rightHeight = (rightHeight - 1).coerceIn(0, 5)
            leftHeight
        } else {
            leftHeight = (leftHeight - 1).coerceIn(0, 5)
            leftHeight
        }
    }

    private fun increaseLeftAngle(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftAngle = (leftAngle + 1).coerceIn(-5, 5)
            rightAngle = (rightAngle + 1).coerceIn(-5, 5)
            leftAngle
        } else {
            leftAngle = (leftAngle + 1).coerceIn(-5, 5)
            leftAngle
        }
    }

    private fun decreaseLeftAngle(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftAngle = (leftAngle - 1).coerceIn(-5, 5)
            rightAngle = (rightAngle - 1).coerceIn(-5, 5)
            leftAngle
        } else {
            leftAngle = (leftAngle - 1).coerceIn(-5, 5)
            leftAngle
        }
    }

    private fun increaseRightHeight(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftHeight = (leftHeight + 1).coerceIn(0, 5)
            rightHeight = (rightHeight + 1).coerceIn(0, 5)
            rightHeight
        } else {
            rightHeight = (rightHeight + 1).coerceIn(0, 5)
            rightHeight
        }
    }

    private fun decreaseRightHeight(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftHeight = (leftHeight - 1).coerceIn(0, 5)
            rightHeight = (rightHeight - 1).coerceIn(0, 5)
            rightHeight
        } else {
            rightHeight = (rightHeight - 1).coerceIn(0, 5)
            rightHeight
        }
    }

    private fun increaseRightAngle(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftAngle = (leftAngle + 1).coerceIn(-5, 5)
            rightAngle = (rightAngle + 1).coerceIn(-5, 5)
            rightAngle
        } else {
            rightAngle = (rightAngle + 1).coerceIn(-5, 5)
            rightAngle
        }
    }

    private fun decreaseRightAngle(): Int {
        return if (isTwoFeetAtTheSameTimeEnabled) {
            leftAngle = (leftAngle - 1).coerceIn(-5, 5)
            rightAngle = (rightAngle - 1).coerceIn(-5, 5)
            rightAngle
        } else {
            rightAngle = (rightAngle - 1).coerceIn(-5, 5)
            rightAngle
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

    private fun sendAppControlValues() {
        if (bluetoothGatt == null || appControlCharacteristic == null) {
            Toast.makeText(this, "장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }



        val values = byteArrayOf(
            (leftAngle*4).toByte(),
            (leftHeight).toByte(),
            (rightAngle*4).toByte(),
            (rightHeight).toByte()
        )
        appControlCharacteristic?.value = values

        bluetoothGatt?.writeCharacteristic(appControlCharacteristic).also {
            Log.d("AppControlActivity", "AppControlCharacteristic 값 설정 요청 완료: ${values.contentToString()}")
        }
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
                mainSignalCharacteristic = uartService?.getCharacteristic(SIGNAL_CHARACTERISTIC_UUID)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic.uuid == APP_CONTROL_CHARACTERISTIC_UUID) {
                val doneStatus = characteristic.value[0].toInt() == 1
                if (doneStatus) {
                    runOnUiThread {
                        Toast.makeText(this@AppControlActivity, "자세 조절 중입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread {
                    when (characteristic.uuid) {
                        SIGNAL_CHARACTERISTIC_UUID -> {
                            Log.d("AppControlActivity", "MainSignalCharacteristic 값이 성공적으로 설정됨")
                            sendAppControlValues()
                        }
                        APP_CONTROL_CHARACTERISTIC_UUID -> {
                            Log.d("AppControlActivity", "AppControlCharacteristic 값이 성공적으로 설정됨")
                            Toast.makeText(this@AppControlActivity, "발 받침대가 조절되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                runOnUiThread {
                    when (characteristic.uuid) {
                        SIGNAL_CHARACTERISTIC_UUID -> Log.d("AppControlActivity", "MainSignalCharacteristic 값 설정 실패")
                        APP_CONTROL_CHARACTERISTIC_UUID -> Log.d("AppControlActivity", "AppControlCharacteristic 값 설정 실패")
                    }
                    Toast.makeText(this@AppControlActivity, "발 받침대 조절에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateMainSignalCharacteristic(2, false)
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
