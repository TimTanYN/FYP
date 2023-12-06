package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.AccommodationJobAdapter
import com.example.fyp.adapter.BottomNavigationHandlerAgent
import com.example.fyp.adapter.BottomNavigationHandlerOwner
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccommodationJobListActivity : AppCompatActivity() {
    private lateinit var accommodationListView: ListView
    private lateinit var noAccommodationTextView: TextView
    private lateinit var adapter: AccommodationJobAdapter
    private val accommodations = mutableListOf<Accommodations>()
    private lateinit var currentUserCity: String
    private lateinit var currentUserState: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_job_list)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_find_job
        val navigationHandler = BottomNavigationHandlerAgent(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        accommodationListView = findViewById(R.id.accommodationListView)
        noAccommodationTextView = findViewById(R.id.noAccommodationListView)

        getCurrentUserDetails()
        loadAccommodations()
    }

    private fun getCurrentUserDetails() {
        // Fetch current user's city and state from Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        currentUserCity = it.city
                        currentUserState = it.state
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadAccommodations() {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    accommodations.clear()
                    for (accommodationSnapshot in snapshot.children) {
                        val accommodation =
                            accommodationSnapshot.getValue(Accommodations::class.java)
                        if (accommodation != null && accommodation.agentId == "null") {
                            accommodations.add(accommodation)
                        }
                    }
                    filterAndSortAccommodations()
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

    private fun filterAndSortAccommodations() {
        val filteredAccommodations =
            accommodations.filter { it.city == currentUserCity || it.state == currentUserState }
        val sortedAccommodations =
            filteredAccommodations.sortedWith(compareByDescending<Accommodations> {
                calculateCommission(it.rentFee, it.agreement).removePrefix("RM ").toDouble()
            }.thenBy { it.city == currentUserCity }.thenBy { it.state == currentUserState })

        if (sortedAccommodations.isNotEmpty()) {
            adapter = AccommodationJobAdapter(this, sortedAccommodations)
            accommodationListView.adapter = adapter
        } else {
            noAccommodationTextView.visibility = View.VISIBLE
            accommodationListView.visibility = View.GONE
        }
    }

    private fun calculateCommission(rentFee: String, year: String): String {
        val rentFeeValue = rentFee.toDoubleOrNull() ?: return "RM 0.00"
        val agreementYears = when (year) {
            "1 year" -> 1
            "2 years" -> 2
            "3 years" -> 3
            "4 years" -> 4
            "5 years" -> 5
            else -> return "RM 0.00"
        }

        val totalRent = rentFeeValue * agreementYears * 12
        val commissionPercentage = when (agreementYears) {
            in 1..2 -> 0.20
            in 3..4 -> 0.25
            else -> 0.28
        }

        val commission = totalRent * commissionPercentage
        return "RM ${String.format("%.2f", commission)}"
    }
}