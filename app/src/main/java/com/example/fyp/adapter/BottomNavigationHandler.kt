package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import com.example.fyp.activity.MapTabs
import com.example.fyp.R
import com.example.fyp.activity.AccommodationListActivity
import com.example.fyp.activity.AccountActivity
import com.example.fyp.activity.BookingListActivity
import com.example.fyp.activity.SettingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationHandler(private val context: Context) {

    /*  Use in activity code to call the navigation bar buttom


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)



    */
    fun setupBottomNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_room -> {
                    navigateToFindRoom()
                    true
                }
                R.id.nav_book -> {
                    navigateToBook()
                    true
                }
                R.id.nav_location -> {
                    navigateToLocation()
                    true
                }
                R.id.nav_profile -> {
                    navigateToProfile()
                    true
                }
                R.id.nav_settings -> {
                    navigateToSettings()
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToFindRoom() {
        // Implement navigation logic
        val intent = Intent(context, AccommodationListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun navigateToBook() {
        val intent = Intent(context, BookingListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun navigateToLocation() {
        val intent = Intent(context, MapTabs::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    private fun navigateToProfile() {
        val intent = Intent(context, AccountActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(context, SettingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}