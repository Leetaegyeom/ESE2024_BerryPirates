package com.example.practice_1

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.util.*

class Record(
    val documentName: String,
    val leftHeight: Int,
    val leftAngle: Int,
    val rightHeight: Int,
    val rightAngle: Int
)

class RecordActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var adapter: RecordAdapter

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
        setContentView(R.layout.activity_record)

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        val deviceAddress = sharedPrefs.getString("DEVICE_ADDRESS", null)
        if (deviceAddress != null) {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
        } else {
            Toast.makeText(this, "저장된 디바이스 주소가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // RecyclerView 초기화
        val recyclerView: RecyclerView = findViewById(R.id.recordRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화
        adapter = RecordAdapter(mutableListOf(), this::onRecordClick)
        recyclerView.adapter = adapter

        db.collection("poses")
            .get()
            .addOnSuccessListener { result ->
                val records = mutableListOf<Record>()
                for (document in result) {
                    val documentName = document.id
                    val leftHeight = document.getLong("leftHeight")?.toInt() ?: 0
                    val leftAngle = document.getLong("leftAngle")?.toInt() ?: 0
                    val rightHeight = document.getLong("rightHeight")?.toInt() ?: 0
                    val rightAngle = document.getLong("rightAngle")?.toInt() ?: 0

                    val record = Record(
                        documentName = documentName,
                        leftHeight = leftHeight,
                        leftAngle = leftAngle,
                        rightHeight = rightHeight,
                        rightAngle = rightAngle
                    )
                    records.add(record)
                }
                // 어댑터에 데이터 갱신
                adapter.updateRecords(records)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            updateMainSignalCharacteristic(2, false)
            // '홈' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@RecordActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@RecordActivity, RecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onRecordClick(record: Record) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_record_options, null)
        val selectedDocumentTextView: TextView = dialogView.findViewById(R.id.selectedDocumentTextView)
        selectedDocumentTextView.text = "'${record.documentName}' 자세를 선택하셨습니다."

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.adjustPostureButton).setOnClickListener {
            // 자세 조절하기 클릭 처리
            updateMainSignalCharacteristic(2, true)
            sendAppControlValues(record)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.deletePostureButton).setOnClickListener {
            // 삭제 확인 다이얼로그 표시
            showDeleteConfirmationDialog(record, dialog)
        }

        dialogView.findViewById<Button>(R.id.renamePostureButton).setOnClickListener {
            // 이름 변경 다이얼로그 표시
            showRenameDialog(record, dialog)
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(record: Record, parentDialog: AlertDialog) {
        val confirmationDialog = AlertDialog.Builder(this)
            .setMessage("'${record.documentName}' 자세를 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                // 삭제 처리
                db.collection("poses").document(record.documentName)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "'${record.documentName}' 자세가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        // 삭제 후 RecyclerView 갱신
                        adapter.removeRecord(record)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                parentDialog.dismiss()
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmationDialog.show()
    }

    private fun showRenameDialog(record: Record, parentDialog: AlertDialog) {
        val renameDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rename_record, null)
        val newNameEditText: EditText = renameDialogView.findViewById(R.id.newNameEditText)

        val renameDialog = AlertDialog.Builder(this)
            .setView(renameDialogView)
            .setPositiveButton("확인") { _, _ ->
                val newName = newNameEditText.text.toString()
                if (newName.isNotBlank()) {
                    // 이름 변경 처리
                    val newRecord = Record(newName, record.leftHeight, record.leftAngle, record.rightHeight, record.rightAngle)
                    db.collection("poses").document(record.documentName)
                        .delete()
                        .addOnSuccessListener {
                            db.collection("poses").document(newName)
                                .set(newRecord)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "'${record.documentName}'에서 '${newName}'(으)로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                    adapter.removeRecord(record)
                                    adapter.addRecord(newRecord)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "이름 변경 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "이름 변경 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "새 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                parentDialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        renameDialog.show()
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
            Log.d("RecordActivity", "MainSignalCharacteristic update 요청 완료")
        }
    }

    private fun sendAppControlValues(record: Record) {
        if (bluetoothGatt == null || appControlCharacteristic == null) {
            Toast.makeText(this, "장치에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val values = byteArrayOf(
            (record.leftAngle * 4).toByte(),
            (record.leftHeight).toByte(),
            (record.rightAngle * 4).toByte(),
            (record.rightHeight).toByte()
        )
        appControlCharacteristic?.value = values

        bluetoothGatt?.writeCharacteristic(appControlCharacteristic).also {
            Log.d("RecordActivity", "AppControlCharacteristic 값 설정 요청 완료: ${values.contentToString()}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@RecordActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@RecordActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
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

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread {
                    when (characteristic.uuid) {
                        SIGNAL_CHARACTERISTIC_UUID -> {
                            Log.d("RecordActivity", "MainSignalCharacteristic 값이 성공적으로 설정됨")
                            Toast.makeText(this@RecordActivity, "MainSignalCharacteristic이 성공적으로 설정되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        APP_CONTROL_CHARACTERISTIC_UUID -> {
                            Log.d("RecordActivity", "AppControlCharacteristic 값이 성공적으로 설정됨")
                            Toast.makeText(this@RecordActivity, "발 받침대가 조절되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                runOnUiThread {
                    when (characteristic.uuid) {
                        SIGNAL_CHARACTERISTIC_UUID -> Log.d("RecordActivity", "MainSignalCharacteristic 값 설정 실패")
                        APP_CONTROL_CHARACTERISTIC_UUID -> Log.d("RecordActivity", "AppControlCharacteristic 값 설정 실패")
                    }
                    Toast.makeText(this@RecordActivity, "발 받침대 조절에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
