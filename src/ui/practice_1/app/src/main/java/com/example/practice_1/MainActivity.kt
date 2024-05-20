package com.example.practice_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val foot_control_button: Button = findViewById(R.id.foot_control_button)
        foot_control_button.setOnClickListener {
            // '발로 조절' 버튼 클릭 시 FootControlActivity로 이동
            val intent = Intent(this@MainActivity, FootControlActivity::class.java)
            startActivity(intent)
        }

        val app_control_button: Button = findViewById(R.id.app_control_button)
        app_control_button.setOnClickListener {
            // '앱으로 조절' 버튼 클릭 시 AppControlActivity로 이동
            val intent = Intent(this@MainActivity, AppControlActivity::class.java)
            startActivity(intent)
        }

        val home_button: Button = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '홈' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: Button = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '기록' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@MainActivity, RecordActivity::class.java)
            startActivity(intent)
        }

        val scan_button: Button = findViewById(R.id.scan_button)
        scan_button.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanActivity::class.java)
            startActivity(intent)
        }
    }

}