package com.example.practice_1

import android.os.Handler


import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_1.BluetoothLeService.Companion.SIGNAL_CHARACTERISTIC_UUID
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.nio.ByteBuffer
import java.util.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.os.Looper
import java.nio.ByteOrder

class FootControlActivity : AppCompatActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var footControlCharacteristic: BluetoothGattCharacteristic? = null
    private var mainSignalCharacteristic: BluetoothGattCharacteristic? = null
    private var recordCharacteristic: BluetoothGattCharacteristic? = null
    private lateinit var bluetoothStatusImageView: ImageView

    private lateinit var angleLockTextView: TextView
    private lateinit var heightLockTextView: TextView
    private lateinit var heightLockImageButton: ImageButton
    private lateinit var angleLockImageButton: ImageButton
    private lateinit var homeImageButton: ImageButton

    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val FOOT_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val RECORD_CHARACTERISTIC_UUID = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e")

    private val db = Firebase.firestore
    private lateinit var profileName: String
    private var poseName: String? = null

    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foot_control)
        bluetoothStatusImageView = findViewById(R.id.bluetoothStatusImageView)
        profileName = sharedPrefs.getString("SELECTED_PROFILE", "default") ?: "default"

        initializeUIElements()
//        progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("업데이트 중...")
//        progressDialog.setCancelable(false)
        // 이전에 저장된 장치 주소를 가져와서 연결 시도
        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        }
//        else {
//            // 저장된 디바이스 주소가 없으면 ScanActivity로 이동
//            val intent = Intent(this@FootControlActivity, ScanActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun initializeUIElements() {
        val homeButton: ImageButton = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            updateMainSignalCharacteristic(1, false)
            updateFootControlCharacteristic(0, false)
            updateFootControlCharacteristic(1, false)
            //toggleHomeButton()
            val intent = Intent(this@FootControlActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val recordButton: ImageButton = findViewById(R.id.record_button)
        recordButton.setOnClickListener {
            updateMainSignalCharacteristic(1, false)
            updateFootControlCharacteristic(0, false)
            updateFootControlCharacteristic(1, false)
            val intent = Intent(this@FootControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }

        val angleLockButton: ImageButton = findViewById(R.id.angle_lock_button)
        angleLockButton.setOnClickListener {
            toggleFootControlCharacteristic(0)
            toggleAngleLockTextView()
            updateFootControlCharacteristic(2,false)
        }

        val heightLockButton: ImageButton = findViewById(R.id.height_lock_button)
        heightLockButton.setOnClickListener {
            toggleFootControlCharacteristic(1)
            toggleHeightLockTextView()
            updateFootControlCharacteristic(2,false)
        }

        val savePoseButton: ImageButton = findViewById(R.id.save_pose_button)
        savePoseButton.setOnClickListener {
            showSaveDialog()
        }

        angleLockTextView = findViewById(R.id.angle_lock_text)
        heightLockTextView = findViewById(R.id.height_lock_text)
        heightLockImageButton = findViewById(R.id.height_lock_button)
        angleLockImageButton = findViewById(R.id.angle_lock_button)
        homeImageButton = findViewById(R.id.home_button)
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
                // Delay the read operation to ensure the write is completed
                Handler(Looper.getMainLooper()).postDelayed({
                    readAndSaveRecordCharacteristic()
                }, 500) // 500ms delay, adjust as needed
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

    private fun toggleAngleLockTextView() {
        // 현재 배경 drawable을 가져옵니다.
        val currentBackground: Drawable? = angleLockImageButton.background
        val heightBtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.angle_btn)
        val heightBlueBtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.angle_blue_btn)

        // 배경 drawable을 비교하여 배경과 텍스트를 토글합니다.
        if (currentBackground?.constantState == heightBtnDrawable?.constantState) {
            angleLockImageButton.background = heightBlueBtnDrawable
            angleLockTextView.setTextColor(Color.parseColor("#4682B4"))
        } else {
            angleLockImageButton.background = heightBtnDrawable
            angleLockTextView.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    private fun toggleHeightLockTextView() {
        // 현재 배경 drawable을 가져옵니다.
        val currentBackground: Drawable? = heightLockImageButton.background
        val heightBtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.height_btn)
        val heightBlueBtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.height_blue_btn)

        // 배경 drawable을 비교하여 배경과 텍스트를 토글합니다.
        if (currentBackground?.constantState == heightBtnDrawable?.constantState) {
            heightLockImageButton.background = heightBlueBtnDrawable
            heightLockTextView.setTextColor(Color.parseColor("#4682B4"))
        } else {
            heightLockImageButton.background = heightBtnDrawable
            heightLockTextView.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    private fun toggleHomeButton(){
        val currentHomebtn: Drawable? =homeImageButton.background
        val homebtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.home_btn)
        val homeanimatedbtnDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.home_animated_btn)

        // 배경 drawable을 비교하여 배경과 텍스트를 토글합니다.
        if (currentHomebtn?.constantState == homebtnDrawable?.constantState) {
            heightLockImageButton.background = homeanimatedbtnDrawable
        } else {
            heightLockImageButton.background =  homebtnDrawable
        }

    }


    private fun updateFootControlCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || footControlCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

//        progressDialog.show()
        if (footControlCharacteristic!!.value == null) {
            footControlCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val controlValues = footControlCharacteristic!!.value.copyOf()
        controlValues[index] = if (value) 1 else 0
        footControlCharacteristic!!.value = controlValues
        bluetoothGatt!!.writeCharacteristic(footControlCharacteristic)

//        Thread.sleep(500)
//        progressDialog.dismiss()
    }

    private fun updateMainSignalCharacteristic(index: Int, value: Boolean) {
        if (bluetoothGatt == null || mainSignalCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }
//        progressDialog.show()
        if (mainSignalCharacteristic!!.value == null) {
            mainSignalCharacteristic!!.value = ByteArray(3) // 적절한 크기의 배열로 초기화
        }

        val signalValues = mainSignalCharacteristic!!.value.copyOf()
        signalValues[index] = if (value) 1 else 0
        mainSignalCharacteristic!!.value = signalValues
        bluetoothGatt!!.writeCharacteristic(mainSignalCharacteristic).also {
            Log.d("FootControlActivity", "MainSignalCharacteristic update 요청 완료")

//            Thread.sleep(500)
//            progressDialog.dismiss()
        }
    }

    private fun readAndSaveRecordCharacteristic() {
        if (bluetoothGatt == null || recordCharacteristic == null) {
            Toast.makeText(this, "BLE 장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("FootControlActivity", "readCharacteristic 호출")
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothGatt?.readCharacteristic(recordCharacteristic)
            bluetoothGatt?.setCharacteristicNotification(recordCharacteristic, true)
            recordCharacteristic?.let {
                it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))?.let { descriptor ->
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt?.writeDescriptor(descriptor)
                }
            }
        }, 500) // 500ms 지연 후 실행
    }



    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                runOnUiThread { Toast.makeText(this@FootControlActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                bluetoothStatusImageView.visibility = ImageView.VISIBLE
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                runOnUiThread { Toast.makeText(this@FootControlActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
                bluetoothStatusImageView.visibility = ImageView.GONE

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
            Log.d("FootControlActivity", "onCharacteristicRead 호출됨")
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.uuid == RECORD_CHARACTERISTIC_UUID) {
                    val values = characteristic.value
                    Log.d("FootControlActivity", "Characteristic 값: ${values.contentToString()}")
                    if (values.size == 4) { // 1바이트 값 4개 이므로 4바이트가 필요함
                        val leftAngle = values[0].toInt()/ 2.0f
                        val leftHeight = values[1].toInt()/ 2.0f
                        val rightAngle = values[2].toInt()/ 2.0f
                        val rightHeight = values[3].toInt()/ 2.0f

                        Log.d("FootControlActivity", "Parsed Values - Left Angle: $leftAngle, Left Height: $leftHeight, Right Angle: $rightAngle, Right Height: $rightHeight")

                        poseName?.let {
                            savePoseToFirestore(leftAngle, leftHeight, rightAngle, rightHeight, it)
                        }
                        updateFootControlCharacteristic(2, false)
                    } else {
                        Log.e("FootControlActivity", "Received data is too short: ${values.size} bytes")
                        runOnUiThread {
                            Toast.makeText(this@FootControlActivity, "자세 데이터가 손상되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.e("FootControlActivity", "Failed to read characteristic: $status")
                runOnUiThread {
                    Toast.makeText(this@FootControlActivity, "자세 데이터를 읽어오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }






        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
//            if(status == BluetoothGatt.GATT_SUCCESS){
//                runOnUiThread{
//                    when (characteristic.uuid){
//                        SIGNAL_CHARACTERISTIC_UUID -> {
//                            updateFootControlCharacteristic(0, false)
//                        }
//                        FOOT_CONTROL_CHARACTERISTIC_UUID -> {
//                            updateFootControlCharacteristic(1,false)
//                        }
//                    }
//                }
//            }

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
            "leftAngle" to leftAngle.toDouble(), // Float 값을 Double로 변환하여 저장
            "leftHeight" to leftHeight.toDouble(), // Float 값을 Double로 변환하여 저장
            "rightAngle" to rightAngle.toDouble(), // Float 값을 Double로 변환하여 저장
            "rightHeight" to rightHeight.toDouble() // Float 값을 Double로 변환하여 저장
        )

        db.collection("$profileName").document(poseName)
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
