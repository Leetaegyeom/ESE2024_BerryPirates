package com.example.practice_1

// FirebaseHelper.kt

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private val db = FirebaseFirestore.getInstance()
    private val recordCollection = db.collection("poses")

//    val db = Firebase.firestore
//    db.collection("poses")
//    fun getRecords(callback: (List<com.example.practice_1.model.Record>) -> Unit) {
//        recordCollection.get()
//            .addOnSuccessListener { result ->
//                val recordList = mutableListOf<com.example.practice_1.model.Record>()
//                for (document in result) {
//                    val record = document.toObject(com.example.practice_1.model.Record::class.java)
//                    recordList.add(record)
//                }
//                callback(recordList)
//            }
//            .addOnFailureListener { exception ->
//                // 오류 처리
//                callback(emptyList())
//            }
//    }
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                // Firestore에서 가져온 데이터를 사용하여 목록에 추가
                val leftHeight = document.getLong("leftHeight")
                val leftAngle = document.getLong("leftAngle")
                val rightHeight = document.getLong("rightHeight")
                val rightAngle = document.getLong("rightAngle")

                // 가져온 데이터를 기록 액티비티에서 사용하여 표시
            }
        }
        .addOnFailureListener { exception ->
            // 실패 시 처리
        }

}
