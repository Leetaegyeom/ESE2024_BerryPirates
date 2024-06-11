package com.example.practice_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileSelectionActivity : AppCompatActivity() {

    private lateinit var profileListView: ListView
    private lateinit var addProfileButton: Button
    private lateinit var profileNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_selection)

        profileListView = findViewById(R.id.profileListView)
        addProfileButton = findViewById(R.id.addProfileButton)
        profileNameEditText = findViewById(R.id.profileNameEditText)

        loadProfiles()

        addProfileButton.setOnClickListener {
            val profileName = profileNameEditText.text.toString()
            if (profileName.isNotEmpty()) {
                if (isProfileNameDuplicate(profileName)) {
                    Toast.makeText(this, "프로필 이름이 중복되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    saveProfile(profileName)
                    loadProfiles()
                    profileNameEditText.text.clear()
                }
            } else {
                Toast.makeText(this, "프로필 이름을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        profileListView.setOnItemClickListener { parent, view, position, id ->
            val selectedProfile = parent.getItemAtPosition(position).toString()
            val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("SELECTED_PROFILE", selectedProfile).apply()

            val intent = Intent(this@ProfileSelectionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun loadProfiles() {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", setOf())!!.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, profiles)
        profileListView.adapter = adapter
    }

    private fun saveProfile(profileName: String) {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", mutableSetOf())!!.toMutableSet()
        profiles.add(profileName)
        sharedPrefs.edit().putStringSet("PROFILES", profiles).apply()
    }

    // 프로필 중복 걸러내는 함수
    private fun isProfileNameDuplicate(profileName: String): Boolean {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", setOf())!!
        return profiles.contains(profileName)
    }
}
