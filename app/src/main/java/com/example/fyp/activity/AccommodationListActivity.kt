package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.AccommodationAdapter
import com.example.fyp.adapter.BottomNavigationHandler
import com.example.fyp.adapter.SettingsAdapter
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Payments
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class AccommodationListActivity : AppCompatActivity() {
    private lateinit var accommodationListView: ListView
    private lateinit var noAccommodationTextView: TextView
    private val accommodations = mutableListOf<Accommodations>()
    private lateinit var adapter: AccommodationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_list)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_find_room
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        accommodationListView = findViewById(R.id.accommodationListView)
        noAccommodationTextView = findViewById(R.id.noAccommodationListView)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        loadAccommodations()

    }

//    private fun loadAccommodations() {
//        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
//        accommodationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    accommodations.clear()
//                    for (accommodationSnapshot in snapshot.children) {
//                        val accommodation =
//                            accommodationSnapshot.getValue(Accommodations::class.java)
//                        if (accommodation != null && accommodation.agentId != "null") {
//                            accommodations.add(accommodation)
//                        }
//                    }
//                    adapter = AccommodationAdapter(this@AccommodationListActivity, accommodations)
//                    accommodationListView.adapter = adapter
//
//                    noAccommodationTextView.visibility = View.GONE
//                    accommodationListView.visibility = View.VISIBLE
//                } else {
//                    noAccommodationTextView.visibility = View.VISIBLE
//                    accommodationListView.visibility = View.GONE
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
//    }

//    private fun loadAccommodations() {
//        // Fetch the Payments data from Firestore
//        val firestore = FirebaseFirestore.getInstance()
//        firestore.collection("Payments")
//            .get()
//            .addOnSuccessListener { paymentsSnapshot ->
//                val paidAccommodationIds = paymentsSnapshot.documents.mapNotNull { it.toObject(Payments::class.java)?.accomID }.toSet()
//
//                // Now fetch the Accommodations data from Realtime Database
//                val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
//                accommodationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(accommodationSnapshot: DataSnapshot) {
//                        if (accommodationSnapshot.exists()) {
//                            accommodations.clear()
//                            for (snapshot in accommodationSnapshot.children) {
//                                val accommodation = snapshot.getValue(Accommodations::class.java)
//                                if (accommodation != null && accommodation.agentId != "null" && !paidAccommodationIds.contains(accommodation.accomID)) {
//                                    accommodations.add(accommodation)
//                                }
//                            }
//                            adapter = AccommodationAdapter(this@AccommodationListActivity, accommodations)
//                            accommodationListView.adapter = adapter
//
//                            noAccommodationTextView.visibility = if (accommodations.isEmpty()) View.VISIBLE else View.GONE
//                            accommodationListView.visibility = if (accommodations.isEmpty()) View.GONE else View.VISIBLE
//                        } else {
//                            noAccommodationTextView.visibility = View.VISIBLE
//                            accommodationListView.visibility = View.GONE
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//            }
//            .addOnFailureListener {
//                // Handle error in fetching Payments data
//            }
//    }

    private fun loadAccommodations() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Fetch the Payments data from Firestore
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Payments")
            .get()
            .addOnSuccessListener { paymentsSnapshot ->
                val paidAccommodationIds = paymentsSnapshot.documents.mapNotNull { it.toObject(Payments::class.java)?.accomID }.toSet()

                // Fetch user IDs from Bookings that match the current user
                val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
                bookingsRef.orderByChild("userId").equalTo(currentUserId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(bookingsSnapshot: DataSnapshot) {
                            val bookedAccommodationIds = bookingsSnapshot.children.mapNotNull { it.getValue(Bookings::class.java)?.accomID }.toSet()

                            // Now fetch the Accommodations data from Realtime Database
                            val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
                            accommodationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(accommodationSnapshot: DataSnapshot) {
                                    if (accommodationSnapshot.exists()) {
                                        accommodations.clear()
                                        for (snapshot in accommodationSnapshot.children) {
                                            val accommodation = snapshot.getValue(Accommodations::class.java)
                                            if (accommodation != null && accommodation.agentId != "null" && !paidAccommodationIds.contains(accommodation.accomID) && !bookedAccommodationIds.contains(accommodation.accomID)) {
                                                accommodations.add(accommodation)
                                            }
                                        }
                                        adapter = AccommodationAdapter(this@AccommodationListActivity, accommodations)
                                        accommodationListView.adapter = adapter

                                        noAccommodationTextView.visibility = if (accommodations.isEmpty()) View.VISIBLE else View.GONE
                                        accommodationListView.visibility = if (accommodations.isEmpty()) View.GONE else View.VISIBLE
                                    } else {
                                        noAccommodationTextView.visibility = View.VISIBLE
                                        accommodationListView.visibility = View.GONE
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error
                        }
                    })
            }
            .addOnFailureListener {
                // Handle error in fetching Payments data
            }
    }

}