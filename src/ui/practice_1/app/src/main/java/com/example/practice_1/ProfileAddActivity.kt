package com.example.practice_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileAddActivity : AppCompatActivity() {

    private lateinit var profileNameEditText: EditText
    private lateinit var addProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_add)

        profileNameEditText = findViewById(R.id.profileNameEditText)
        addProfileButton = findViewById(R.id.addProfileButton)

        addProfileButton.setOnClickListener {
            val profileName = profileNameEditText.text.toString()
            if (profileName.isNotEmpty()) {
                if (isProfileNameDuplicate(profileName)) {
                    Toast.makeText(this, "프로필 이름이 중복되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    saveProfile(profileName)
                    setResult(RESULT_OK)
                    finish()
                }
            } else {
                Toast.makeText(this, "프로필 이름을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile(profileName: String) {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", mutableSetOf())!!.toMutableSet()
        profiles.add(profileName)
        sharedPrefs.edit().putStringSet("PROFILES", profiles).apply()
    }

    private fun isProfileNameDuplicate(profileName: String): Boolean {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", setOf())!!
        return profiles.contains(profileName)
    }
}
