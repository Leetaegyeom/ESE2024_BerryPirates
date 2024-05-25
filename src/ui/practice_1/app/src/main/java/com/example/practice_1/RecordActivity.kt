package com.example.practice_1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        selectedDocumentTextView.text = "${record.documentName}을/를 선택하셨습니다."

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.adjustPostureButton).setOnClickListener {
            // 자세 조절하기 클릭 처리
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.deletePostureButton).setOnClickListener {
            // 삭제 확인 다이얼로그 표시
            showDeleteConfirmationDialog(record, dialog)
        }

        dialogView.findViewById<Button>(R.id.renamePostureButton).setOnClickListener {
            // 이름 변경하기 클릭 처리
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(record: Record, parentDialog: AlertDialog) {
        val confirmationDialog = AlertDialog.Builder(this)
            .setMessage("${record.documentName}을/를 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                // 삭제 처리
                db.collection("poses").document(record.documentName)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "${record.documentName}이/가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
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
}
