package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.fyp.R
import com.example.fyp.adapter.BottomNavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        // Initialize CardViews
        val cardViewManageAccount = findViewById<CardView>(R.id.cardViewManageAccount)
        val cardViewSendContract = findViewById<CardView>(R.id.cardViewSendContract)
        val cardViewSeeReview = findViewById<CardView>(R.id.cardViewSeeReview)
        val cardViewLogout = findViewById<CardView>(R.id.cardViewLogout)

        // Set onClickListeners for each CardView
        cardViewManageAccount.setOnClickListener {
            val intent = Intent(this, ApproveAccountActivity::class.java)
            startActivity(intent)
        }

        cardViewSendContract.setOnClickListener {

        }

        cardViewSeeReview.setOnClickListener {

        }

        cardViewLogout.setOnClickListener {
            showSignOutDialog()
        }

    }

    private fun showSignOutDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Sign Out")
        alertDialogBuilder.setMessage("Are you sure you want to sign out?")

        // Set up the positive (Yes) button
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            FirebaseAuth.getInstance().signOut() // Sign out
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the negative (No) button
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss() // Dismiss the dialog and stay on the current screen
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}