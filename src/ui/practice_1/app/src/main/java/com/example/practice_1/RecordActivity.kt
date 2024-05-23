package com.example.practice_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // RecyclerView 초기화
        val recyclerView: RecyclerView = findViewById(R.id.recordRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화
        adapter = RecordAdapter(emptyList())
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
}
