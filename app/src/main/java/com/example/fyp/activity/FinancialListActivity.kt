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
import com.example.fyp.adapter.CommissionAdapter
import com.example.fyp.adapter.FinancialAdapter
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Payments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class FinancialListActivity : AppCompatActivity() {

    private lateinit var financialListView: ListView
    private lateinit var noFinancialListView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_list)

        financialListView = findViewById(R.id.financialListView)
        noFinancialListView = findViewById(R.id.noFinancialListView)

        setupToolbar()
        loadFinancials()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, SettingOwnerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFinancials() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        val firestore = FirebaseFirestore.getInstance()

        // Step 1: Retrieve accomID(s) from Accommodations where ownerId matches currentUserId
        accommodationsRef.orderByChild("ownerId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val accomIds = snapshot.children.mapNotNull { it.getValue(Accommodations::class.java)?.accomID }

                        // Step 2: Query Payments in Firestore using retrieved accomID(s)
                        firestore.collection("Payments")
                            .whereIn("accomID", accomIds)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val payments = documents.toObjects(Payments::class.java)
                                    val adapter = FinancialAdapter(this@FinancialListActivity, payments)
                                    financialListView.adapter = adapter
                                    financialListView.visibility = View.VISIBLE
                                    noFinancialListView.visibility = View.GONE
                                } else {
                                    financialListView.visibility = View.GONE
                                    noFinancialListView.visibility = View.VISIBLE
                                }
                            }
                            .addOnFailureListener {
                                // Handle error
                            }
                    } else {
                        // No accommodations found for the owner
                        financialListView.visibility = View.GONE
                        noFinancialListView.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}