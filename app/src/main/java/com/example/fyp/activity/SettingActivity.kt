package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.BottomNavigationHandler
import com.example.fyp.adapter.SettingsAdapter
import com.example.fyp.adapter.SettingsItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: SettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_settings
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        listView = findViewById(R.id.settings_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val settingsList = listOf(
            SettingsItem("Notifications", hasSwitch = true),
            SettingsItem("Privacy Policy", hasSwitch = false),
            SettingsItem("Terms & Conditions", hasSwitch = false),
            SettingsItem("About App", hasSwitch = false),
            SettingsItem("Contact Us", hasSwitch = false),
            SettingsItem("About App", hasSwitch = false),
            SettingsItem("Logout", hasSwitch = false),

        )

        adapter = SettingsAdapter(this, settingsList)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            // Handle click events here
            val item = adapter.getItem(position)
            when (item?.title) {
                "Notifications" -> {
                    // Handle "Notifications" click
                }
                "Privacy Policy" -> {
                    // Navigate to "Privacy Policy" screen
                    val intent = Intent(this, PrivacyActivity::class.java)
                    startActivity(intent)
                }
                "Terms & Conditions" -> {
                    // Navigate to "Terms & Conditions" screen
                }
                "About App" -> {
                    // Navigate to "About App" screen
                }
                "Logout" -> {
                    // Navigate to "Logout"
                }

            }
        }
    }
}