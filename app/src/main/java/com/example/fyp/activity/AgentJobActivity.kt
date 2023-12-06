package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.AccommodationJobAdapter
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Workers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AgentJobActivity : AppCompatActivity() {
    private lateinit var accommodationListView: ListView
    private lateinit var noAccommodationTextView: TextView
    private lateinit var adapter: AccommodationJobAdapter
    private val accommodations = mutableListOf<Accommodations>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_job)

        accommodationListView = findViewById(R.id.accommodationListView)
        noAccommodationTextView = findViewById(R.id.noAccommodationListView)
        setupToolbar()
        loadWorkerAccommodations()

    }

    private fun loadWorkerAccommodations() {
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")

        workersRef.orderByChild("agentId").equalTo(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    accommodations.clear()
                    for (workerSnapshot in snapshot.children) {
                        val worker = workerSnapshot.getValue(Workers::class.java)
                        worker?.let {
                            accommodationsRef.child(it.accomID).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(accommodationSnapshot: DataSnapshot) {
                                    if (accommodationSnapshot.exists()) {
                                        val accommodation = accommodationSnapshot.getValue(Accommodations::class.java)
                                        accommodation?.let { acc ->
                                            accommodations.add(acc)
                                            updateListView()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }
                    }
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

    private fun updateListView() {
        if (accommodations.isNotEmpty()) {
            adapter = AccommodationJobAdapter(this, accommodations)
            accommodationListView.adapter = adapter
            noAccommodationTextView.visibility = View.GONE
            accommodationListView.visibility = View.VISIBLE
        } else {
            noAccommodationTextView.visibility = View.VISIBLE
            accommodationListView.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccommodationJobListActivity::class.java)
            startActivity(intent)
        }
    }
}