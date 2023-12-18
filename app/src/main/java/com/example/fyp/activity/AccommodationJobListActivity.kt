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
import com.example.fyp.adapter.AccommodationAdapter
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

        val jobButton = findViewById<Button>(R.id.jobButton)

        accommodationListView = findViewById(R.id.accommodationListView)
        noAccommodationTextView = findViewById(R.id.noAccommodationListView)

        getCurrentUserDetails()
        loadAccommodations()

        jobButton.setOnClickListener{
            val intent = Intent(this, AgentJobActivity::class.java)
            startActivity(intent)
        }
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
                accommodations.clear()
                if (snapshot.exists()) {
                    for (accommodationSnapshot in snapshot.children) {
                        val accommodation = accommodationSnapshot.getValue(Accommodations::class.java)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun filterAndSortAccommodations() {
        val filteredAccommodations = accommodations.filter {
            it.city == currentUserCity || it.state == currentUserState
        }

        val accommodationsToDisplay = if (filteredAccommodations.isNotEmpty()) filteredAccommodations else accommodations

        val sortedAccommodations = accommodationsToDisplay.sortedWith(
            compareByDescending<Accommodations> {
                calculateCommission(it.rentFee, it.rate).removePrefix("RM ").toDouble()
            }.thenBy { it.city == currentUserCity }.thenBy { it.state == currentUserState  }
        )

        adapter = AccommodationJobAdapter(this, sortedAccommodations)
        accommodationListView.adapter = adapter
        noAccommodationTextView.visibility = if (sortedAccommodations.isEmpty()) View.VISIBLE else View.GONE
        accommodationListView.visibility = if (sortedAccommodations.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun calculateCommission(rentFee: String, rate: String): String {
        val monthlyCommission = rentFee.toDouble() * rate.toDouble()
        return "RM ${String.format("%.2f", monthlyCommission)}"
    }
}