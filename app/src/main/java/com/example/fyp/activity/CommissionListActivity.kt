package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.BottomNavigationHandlerAgent
import com.example.fyp.adapter.CommissionAdapter
import com.example.fyp.database.Payments
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommissionListActivity : AppCompatActivity() {
    private lateinit var accommodationListView: ListView
    private lateinit var noAccommodationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commission_list)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_salary
        val navigationHandler = BottomNavigationHandlerAgent(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        accommodationListView = findViewById(R.id.accommodationListView)
        noAccommodationTextView = findViewById(R.id.noAccommodationListView)

        loadCommissions()
    }

    private fun loadCommissions() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Payments")
            .whereEqualTo("agentId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val payments = documents.toObjects(Payments::class.java)
                    val adapter = CommissionAdapter(this, payments)
                    accommodationListView.adapter = adapter
                    accommodationListView.visibility = View.VISIBLE
                    noAccommodationTextView.visibility = View.GONE
                } else {
                    accommodationListView.visibility = View.GONE
                    noAccommodationTextView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}