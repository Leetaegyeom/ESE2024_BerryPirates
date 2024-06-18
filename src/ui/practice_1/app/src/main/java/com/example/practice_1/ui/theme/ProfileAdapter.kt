package com.example.practice_1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class ProfileAdapter(
    context: Context,
    private val profiles: List<String>,
    private val deleteListener: (String) -> Unit,
    private val itemClickListener: (String) -> Unit
) : ArrayAdapter<String>(context, 0, profiles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_profile, parent, false)
        val profileName = view.findViewById<TextView>(R.id.profileName)
        val deleteProfileButton = view.findViewById<ImageButton>(R.id.deleteProfileButton)

        val profile = profiles[position]
        profileName.text = profile

        deleteProfileButton.setOnClickListener {
            deleteListener(profile)
        }

        view.setOnClickListener {
            itemClickListener(profile)
        }

        return view
    }
}
