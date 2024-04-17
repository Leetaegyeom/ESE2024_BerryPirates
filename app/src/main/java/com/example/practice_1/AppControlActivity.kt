package com.example.practice_1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practice_1.ui.theme.Practice_1Theme

class AppControlActivity : AppCompatActivity() {
    // 왼발 높이와 각도, 오른발 높이와 각도의 초기값 설정
    private var leftHeight = 0
    private var leftAngle = 0
    private var rightHeight = 0
    private var rightAngle = 0
    var isTwoFeetAtTheSameTimeEnabled = false
    val db = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_control)


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


        leftHeightIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                rightHeight = (rightHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            }else {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
            }
        }

        leftHeightDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            }else {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
            }
        }

        // 왼발 각도 조절 버튼


        leftAngleIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle + 1).coerceIn(-5,5)
                rightAngle = (rightAngle + 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            }else {
                leftAngle = (leftAngle + 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
            }
        }

        leftAngleDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle - 1).coerceIn(-5,5)
                rightAngle = (rightAngle - 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            }else {
                leftAngle = (leftAngle - 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
            }
        }

        // 오른발 높이 조절 버튼


        rightHeightIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight + 1).coerceIn(0, 5)
                rightHeight = (rightHeight + 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            }else {
                rightHeight = (rightHeight + 1).coerceIn(0,5)
                rightHeightTextView.text = rightHeight.toString()
            }
        }

        rightHeightDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftHeight = (leftHeight - 1).coerceIn(0, 5)
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                leftHeightTextView.text = leftHeight.toString()
                rightHeightTextView.text = rightHeight.toString()
            }else {
                rightHeight = (rightHeight - 1).coerceIn(0, 5)
                rightHeightTextView.text = rightHeight.toString()
            }
        }



        rightAngleIncreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle + 1).coerceIn(-5,5)
                rightAngle = (rightAngle + 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            }else {
                rightAngle = (rightAngle + 1).coerceIn(-5,5)
                rightAngleTextView.text = rightAngle.toString()
            }
        }

        rightAngleDecreaseButton.setOnClickListener {
            if (isTwoFeetAtTheSameTimeEnabled) {
                leftAngle = (leftAngle - 1).coerceIn(-5,5)
                rightAngle = (rightAngle - 1).coerceIn(-5,5)
                leftAngleTextView.text = leftAngle.toString()
                rightAngleTextView.text = rightAngle.toString()
            }else {
                rightAngle = (rightAngle - 1).coerceIn(-5,5)
                rightAngleTextView.text = rightAngle.toString()
            }
        }

        val twoFeetAtTheSameTimeButton: Button = findViewById(R.id.twoFeetAtTheSameTimeButton)

        twoFeetAtTheSameTimeButton.setOnClickListener {
            isTwoFeetAtTheSameTimeEnabled = !isTwoFeetAtTheSameTimeEnabled
            if (isTwoFeetAtTheSameTimeEnabled) {
                Toast.makeText(this, "두 발의 높이와 각도를 동시에 조절합니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "두 발의 높이와 각도를 동시에 조절하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // '자세 저장하기' 버튼 클릭 이벤트
        val savePoseButton: Button = findViewById(R.id.savePoseButton)
        savePoseButton.setOnClickListener {
            // 현재의 두 발의 높이와 각도 값을 가져와서 Firestore에 저장
            val poseData = hashMapOf(
                "leftHeight" to leftHeight,
                "leftAngle" to leftAngle,
                "rightHeight" to rightHeight,
                "rightAngle" to rightAngle
            )

            // "poses" 컬렉션에 poseData 추가
            db.collection("poses")
                .add(poseData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "자세가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "자세 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        }


    }









//        setContent {
//            Practice_1Theme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting3("Android")
//                }
//            }
//        }
//    }
}

//@Composable
//fun Greeting3(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview3() {
//    Practice_1Theme {
//        Greeting3("Android")
//    }
//}