package com.example.practice_1

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class ScanActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private val deviceList: ArrayList<BluetoothDevice> = ArrayList()
    private var bluetoothGatt: BluetoothGatt? = null

    // Define UUIDs for the services and characteristics from the Raspberry Pi code
    private val UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val SIGNAL_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val FOOT_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val APP_CONTROL_CHARACTERISTIC_UUID = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e")
    private val RECORD_CHARACTERISTIC_UUID = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth not supported or not enabled", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        deviceListAdapter = ArrayAdapter(this, R.layout.simple_list_item_1_black, ArrayList())
        val listView: ListView = findViewById(R.id.device_list_view)
        listView.adapter = deviceListAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = deviceList[position]
            connectToDevice(device)
        }

        checkPermissionsAndStartScanning()

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '홈' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@ScanActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@ScanActivity, RecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermissionsAndStartScanning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                startScanning()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                startScanning()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startScanning()
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScanning() {
        // 기존 스캔을 중지합니다.
        bluetoothLeScanner.stopScan(scanCallback)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initiateScan()
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initiateScan()
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initiateScan() {
        if (bluetoothAdapter.isEnabled) {
            val scanFilter = ScanFilter.Builder().build()
            val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            bluetoothLeScanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
        } else {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
        }
    }





    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            if (!deviceList.contains(device) && device.name != null && device.name == "Footreedom") {
                deviceList.add(device)
                val deviceName = device.name ?: "Unknown Device"
                val deviceAddress = device.address
                runOnUiThread { deviceListAdapter.add("$deviceName\n$deviceAddress") }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> Toast.makeText(this@ScanActivity, "스캔이 이미 시작되었습니다.", Toast.LENGTH_SHORT).show()
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> Toast.makeText(this@ScanActivity, "스캔 실패: 애플리케이션 등록 실패", Toast.LENGTH_SHORT).show()
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> Toast.makeText(this@ScanActivity, "스캔 실패: 내부 오류", Toast.LENGTH_SHORT).show()
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> Toast.makeText(this@ScanActivity, "스캔 실패: 기능이 지원되지 않습니다.", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this@ScanActivity, "스캔 실패: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        BleManager.bluetoothGatt = bluetoothGatt // BleManager에 저장

        // 새로운 장치 주소를 SharedPreferences에 저장
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("DEVICE_ADDRESS", device.address).apply()

        Toast.makeText(this, "${device.name} 연결 중...", Toast.LENGTH_SHORT).show()
    }


    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@ScanActivity, "연결되었습니다", Toast.LENGTH_SHORT).show() }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@ScanActivity, "연결이 끊어졌습니다", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val uartService = gatt.getService(UART_SERVICE_UUID)
                uartService?.let {
                    val mainSignalCharacteristic = it.getCharacteristic(SIGNAL_CHARACTERISTIC_UUID)
                    mainSignalCharacteristic?.let { characteristic ->
                        gatt.readCharacteristic(characteristic)
                    }

                    val footControlCharacteristic = it.getCharacteristic(FOOT_CONTROL_CHARACTERISTIC_UUID)
                    footControlCharacteristic?.let { characteristic ->
                        gatt.readCharacteristic(characteristic)
                    }

                    val appControlCharacteristic = it.getCharacteristic(APP_CONTROL_CHARACTERISTIC_UUID)
                    appControlCharacteristic?.let { characteristic ->
                        gatt.readCharacteristic(characteristic)
                    }

                    val recordCharacteristic = it.getCharacteristic(RECORD_CHARACTERISTIC_UUID)
                    recordCharacteristic?.let { characteristic ->
                        gatt.readCharacteristic(characteristic)
                    }
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (characteristic.uuid) {
                    SIGNAL_CHARACTERISTIC_UUID -> {
                        val value = characteristic.value
                        runOnUiThread { Toast.makeText(this@ScanActivity, "Signal characteristic read: ${value.contentToString()}", Toast.LENGTH_SHORT).show() }
                    }
                    FOOT_CONTROL_CHARACTERISTIC_UUID -> {
                        val value = characteristic.value
                        runOnUiThread { Toast.makeText(this@ScanActivity, "FootControl characteristic read: ${value.contentToString()}", Toast.LENGTH_SHORT).show() }
                    }
                    APP_CONTROL_CHARACTERISTIC_UUID -> {
                        val value = characteristic.value
                        runOnUiThread { Toast.makeText(this@ScanActivity, "AppControl characteristic read: ${value.contentToString()}", Toast.LENGTH_SHORT).show() }
                    }
                    RECORD_CHARACTERISTIC_UUID -> {
                        val value = characteristic.value
                        runOnUiThread { Toast.makeText(this@ScanActivity, "Record characteristic read: ${value.contentToString()}", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothLeScanner.stopScan(scanCallback)
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
