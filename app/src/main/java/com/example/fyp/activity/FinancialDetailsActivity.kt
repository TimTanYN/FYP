package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Payments
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class FinancialDetailsActivity : AppCompatActivity() {

    private lateinit var tvAgentName: TextView
    private lateinit var tvAgentEmail: TextView
    private lateinit var tvAgentContact: TextView
    private lateinit var tvTenantName: TextView
    private lateinit var tvTenantEmail: TextView
    private lateinit var tvTenantContact: TextView
    private lateinit var tvCommission: TextView
    private lateinit var tvTotalFee: TextView
    private lateinit var tvRentFee: TextView
    private lateinit var tvRate: TextView
    private lateinit var tvDepositFee: TextView
    private lateinit var tvContract: TextView
    private lateinit var tvTotalEarn: TextView
    private lateinit var tenantBtn: Button
    private lateinit var agentBtn: Button
    private lateinit var checkBtn: Button

    private lateinit var accomID: String
    private lateinit var tenantEmail: String
    private lateinit var tenantPhoneNumber: String
    private lateinit var agentEmail: String
    private lateinit var agentPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_details)

        tvAgentName = findViewById(R.id.tvAgentName)
        tvAgentEmail = findViewById(R.id.tvAgentEmail)
        tvAgentContact = findViewById(R.id.tvAgentContact)
        tvTenantName = findViewById(R.id.tvTenantName)
        tvTenantEmail = findViewById(R.id.tvTenantEmail)
        tvTenantContact = findViewById(R.id.tvTenantContact)
        tvCommission= findViewById(R.id.tvCommission)
        tvTotalFee = findViewById(R.id.tvTotalFee)
        tvRentFee = findViewById(R.id.tvRentFee)
        tvRate = findViewById(R.id.tvRate)
        tvDepositFee = findViewById(R.id.tvDepositFee)
        tvContract = findViewById(R.id.tvContract)
        tvTotalEarn = findViewById(R.id.tvTotalEarn)
        tenantBtn = findViewById(R.id.tenantBtn)
        agentBtn = findViewById(R.id.agentBtn)
        checkBtn = findViewById(R.id.checkBtn)

        accomID = intent.getStringExtra("ACCOM_ID").toString()
        setupToolbar()
        loadPaymentDetails(accomID)

        tenantBtn.setOnClickListener {
            showContactTenantOptions()
        }
        agentBtn.setOnClickListener {
            showContactAgentOptions()
        }
        checkBtn.setOnClickListener {
            val intent = Intent(this, AccommodationDetailsOwnerActivity::class.java)
            intent.putExtra("ACCOM_ID", accomID)
            startActivity(intent)
        }

    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, FinancialListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showContactTenantOptions() {
        val options = arrayOf("Send email to tenant", "Call tenant")
        AlertDialog.Builder(this)
            .setTitle("Contact Tenant")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendEmail(tenantEmail)
                    1 -> makePhoneCall(tenantPhoneNumber)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showContactAgentOptions() {
        val options = arrayOf("Send email to agent", "Call agent")
        AlertDialog.Builder(this)
            .setTitle("Contact Agent")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendEmail(agentEmail)
                    1 -> makePhoneCall(agentPhoneNumber)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }


    private fun loadPaymentDetails(accomID: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Payments")
            .whereEqualTo("accomID", accomID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val payment = document.toObject(Payments::class.java)
                        // Now use payment details to populate the views
                        tvTotalFee.text = String.format("RM %.2f", payment.total.toDouble())

                        val totalEarn = payment.total.toDouble() - payment.commission.toDouble()
                        tvTotalEarn.text = String.format("RM %.2f", totalEarn.toDouble())

                        loadAccommodationDetails(payment.accomID)
                        // Load tenant and agent details
                        loadTenantDetails(payment.tenantId)
                        loadAgentDetails(payment.agentId)
                    }
                } else {

                }
            }
            .addOnFailureListener { e ->
            }
    }


    private fun loadTenantDetails(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                user?.let {
                    tvTenantName.text = it.fullName
                    tvTenantEmail.text = it.email
                    tvTenantContact.text = it.phoneNumber

                    tenantPhoneNumber = it.phoneNumber
                    tenantEmail = it.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadAgentDetails(agentId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(agentId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                user?.let {
                    tvAgentName.text = it.fullName
                    tvAgentEmail.text = it.email
                    tvAgentContact.text = it.phoneNumber

                    agentPhoneNumber = it.phoneNumber
                    agentEmail = it.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadAccommodationDetails(accomID: String) {
        val accommodationRef = FirebaseDatabase.getInstance().getReference("Accommodations").child(accomID)
        accommodationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accommodation = snapshot.getValue(Accommodations::class.java)
                accommodation?.let {
                    tvContract.text = it.agreement

                    tvRentFee.text = String.format("RM %.2f", it.rentFee.toDouble())

                    tvRate.text = "${it.rate.toDouble()}%"

                    // Calculate and display commission
                    val monthlyCommission = it.rentFee.toDouble() * it.rate.toDouble()
                    tvCommission.text = String.format("RM %.2f", monthlyCommission)

                    // Calculate and display deposit fee
                    val depositFee = it.rentFee.toDouble() * 2
                    tvDepositFee.text = String.format("RM %.2f", depositFee)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}