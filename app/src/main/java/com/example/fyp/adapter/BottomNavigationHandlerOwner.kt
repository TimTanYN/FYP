package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import com.example.fyp.activity.Contract

import com.example.fyp.R
import com.example.fyp.activity.AccountOwnerActivity
import com.example.fyp.activity.FeedbackEnd
import com.example.fyp.activity.ManageAccommodationActivity
import com.example.fyp.activity.SettingOwnerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationHandlerOwner(private val context: Context) {

    /*  Use in activity code to call the navigation bar buttom


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)



    */
    fun setupBottomNavigation(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_manage_room -> {
                    navigateToManageRoom()
                    true
                }
                R.id.nav_rating-> {
                    navigateToRating()
                    true
                }
                R.id.nav_profile -> {
                    navigateToProfile()
                    true
                }
                R.id.nav_contract -> {
                    navigateToContract()
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

    private fun navigateToManageRoom() {
        // Implement navigation logic
        val intent = Intent(context, ManageAccommodationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    private fun navigateToRating() {
        val intent = Intent(context, FeedbackEnd::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    private fun navigateToContract() {
        val intent = Intent(context, Contract::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    private fun navigateToProfile() {
        val intent = Intent(context, AccountOwnerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(context, SettingOwnerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Add this if you're calling from a non-Activity context
        context.startActivity(intent)
    }
}