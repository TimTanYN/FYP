package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.AccommodationsAdapter
import com.example.fyp.adapter.BottomNavigationHandler
import com.example.fyp.adapter.BottomNavigationHandlerOwner
import com.example.fyp.database.Accommodations
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class ManageAccommodationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_accommodation)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_manage_room
        val navigationHandler = BottomNavigationHandlerOwner(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        val addBtn = findViewById<Button>(R.id.addButton)

        checkForAccommodations()

        addBtn.setOnClickListener{
            checkUserCardBeforeAdding()
        }
    }

    override fun onResume() {
        super.onResume()
        checkForAccommodations() // Refresh data when activity resumes
    }

    private fun checkUserCardBeforeAdding() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Cards")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // User has a card, proceed with adding accommodation
                    val intent = Intent(this, AddAccommodationActivity::class.java)
                    startActivity(intent)
                } else {
                    showToast("Please add a card")
                    // No card found, navigate to Add Card Owner Activity
                    val intent = Intent(this, AddCardOwnerActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun checkForAccommodations() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        val listView = findViewById<ListView>(R.id.accommodationListView)
        val noDataTextView = findViewById<TextView>(R.id.noAccommodationListView)

        databaseRef.orderByChild("ownerId").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val accommodationsList = dataSnapshot.children.mapNotNull { it.getValue(Accommodations::class.java) }
                    val adapter = AccommodationsAdapter(this@ManageAccommodationActivity, accommodationsList)
                    listView.adapter = adapter
                    listView.visibility = View.VISIBLE
                    noDataTextView.visibility = View.GONE
                } else {
                    listView.visibility = View.GONE
                    noDataTextView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}