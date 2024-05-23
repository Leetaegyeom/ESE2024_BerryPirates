package com.example.practice_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class FootControlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.foot_control)

        val home_button: ImageButton = findViewById(R.id.home_button)
        home_button.setOnClickListener {
            // '발로 조절' 버튼 클릭 시 MainActivity로 이동
            val intent = Intent(this@FootControlActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val record_button: ImageButton = findViewById(R.id.record_button)
        record_button.setOnClickListener {
            // '발로 조절' 버튼 클릭 시 RecordActivity로 이동
            val intent = Intent(this@FootControlActivity, RecordActivity::class.java)
            startActivity(intent)
        }
    }
}







