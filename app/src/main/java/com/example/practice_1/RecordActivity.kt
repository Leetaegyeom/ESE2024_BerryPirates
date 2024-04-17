// RecordActivity.kt
package com.example.practice_1
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice_1.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class Record(
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
                    // Firestore에서 가져온 데이터를 com.example.practice_1.model.Record 객체로 변환하여 리스트에 추가
                    val leftHeight = document.getLong("leftHeight")?.toInt() ?: 0
                    val leftAngle = document.getLong("leftAngle")?.toInt() ?: 0
                    val rightHeight = document.getLong("rightHeight")?.toInt() ?: 0
                    val rightAngle = document.getLong("rightAngle")?.toInt() ?: 0


                    val record = Record(leftHeight, leftAngle, rightHeight, rightAngle)
                    records.add(record)
                }
                // 어댑터에 데이터 갱신
                adapter.updateRecords(records)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
}
