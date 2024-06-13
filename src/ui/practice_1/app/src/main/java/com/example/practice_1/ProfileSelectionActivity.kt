package com.example.practice_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadProfiles()
        }
    }
}
