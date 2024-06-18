package com.example.practice_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileSelectionActivity : AppCompatActivity() {

    private lateinit var profileListView: ListView
    private lateinit var addProfileButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_selection)

        profileListView = findViewById(R.id.profileListView)
        addProfileButton = findViewById(R.id.addProfileButton)

        loadProfiles()

        addProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileAddActivity::class.java)
            startActivityForResult(intent, 1)
        }

        // profileListView.setOnItemClickListener 제거
    }

    private fun loadProfiles() {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", setOf())!!.toList()
        val adapter = ProfileAdapter(this, profiles, { profile ->
            confirmDeleteProfile(profile)
        }, { profile ->
            selectProfile(profile)
        })
        profileListView.adapter = adapter
    }

    private fun confirmDeleteProfile(profile: String) {
        AlertDialog.Builder(this).apply {
            setTitle("프로필 삭제")
            setMessage("$profile 을(를) 삭제하시겠습니까?")
            setPositiveButton("예") { _, _ ->
                deleteProfile(profile)
            }
            setNegativeButton("아니오", null)
            show()
        }
    }

    private fun deleteProfile(profile: String) {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        val profiles = sharedPrefs.getStringSet("PROFILES", mutableSetOf())?.toMutableSet()
        profiles?.remove(profile)
        sharedPrefs.edit().putStringSet("PROFILES", profiles).apply()
        loadProfiles()
        Toast.makeText(this, "프로필이 삭제되었습니다: $profile", Toast.LENGTH_SHORT).show()
    }

    private fun selectProfile(profile: String) {
        val sharedPrefs = getSharedPreferences("BLE_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("SELECTED_PROFILE", profile).apply()

        val intent = Intent(this@ProfileSelectionActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadProfiles()
        }
    }
}
