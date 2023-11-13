package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setupToolbar()
        setupCountryCodeSpinner()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back action
        }
    }

    private fun setupCountryCodeSpinner() {
        val spinner: Spinner = findViewById(R.id.countryCodeSpinner)
        val countryCodesWithNames = listOf("+60 -> Malaysia", "+65 -> Singapore", "+62 -> Indonesia", "+66 -> Thailand") // Dropdown list
        val countryCodes = listOf("+60", "+65", "+62", "+66") // Spinner view

        val countryCodeAdapter = object : ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_spinner_item, // Layout for the normal spinner view
            countryCodes // Data
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                // Provide the layout for the dropdown view
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.text = countryCodesWithNames[position]
                return view
            }
        }

        countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = countryCodeAdapter
    }
}
