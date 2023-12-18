package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class BookingHistoryDetailsActivity : AppCompatActivity() {

    private lateinit var tvCheckInDate: TextView
    private lateinit var tvCheckOutDate: TextView
    private lateinit var tvAgentName: TextView
    private lateinit var tvAgentEmail: TextView
    private lateinit var tvAgentContact: TextView
    private lateinit var tvTotalFee: TextView
    private lateinit var tvRentFee: TextView
    private lateinit var tvDepositFee: TextView
    private lateinit var tvContract: TextView
    private lateinit var contactBtn: Button
    private lateinit var contactBtn2: Button
    private lateinit var statusLayout: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var tvReason: TextView

    private lateinit var accomID: String
    private lateinit var agentEmail: String
    private lateinit var agentPhoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history_details)

        tvCheckInDate = findViewById(R.id.tvCheckInDate)
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate)
        tvAgentName = findViewById(R.id.tvAgentName)
        tvAgentEmail = findViewById(R.id.tvAgentEmail)
        tvAgentContact = findViewById(R.id.tvAgentContact)
        tvTotalFee = findViewById(R.id.tvTotalFee)
        tvRentFee = findViewById(R.id.tvRentFee)
        tvDepositFee = findViewById(R.id.tvDepositFee)
        tvContract = findViewById(R.id.tvContract)
        contactBtn = findViewById(R.id.contactBtn)
        contactBtn2 = findViewById(R.id.contactBtn2)
        statusLayout = findViewById(R.id.combinedLayout4)
        tvStatus = findViewById(R.id.tvStatus)
        tvReason = findViewById(R.id.tvReason)

        setupToolbar()

        accomID = intent.getStringExtra("ACCOM_ID").toString()

        loadBookingDetails(accomID)
        contactBtn.setOnClickListener {
            showContactOptions()
        }

        contactBtn2.setOnClickListener {
            showContactOptions()
        }

    }

    private fun showContactOptions() {
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

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BookingHistoryListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadBookingDetails(accomID: String) {
        // Assuming FirebaseDatabase instance is initialized and ready to use
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            booking?.let {
                                tvCheckInDate.text = it.checkIn
                                tvCheckOutDate.text = it.checkOut
                                tvStatus.text = it.status
                                tvReason.text = it.reason
                                val formattedTotal = String.format("%.2f", it.total.toDouble())
                                tvTotalFee.text = "RM $formattedTotal"

                                if (it.status.isNotEmpty() && it.status == "Rejected" && it.reason.isNotEmpty() && it.reason != "null") {
                                    statusLayout.visibility = View.VISIBLE
                                    contactBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE

                                }else if (it.status.isNotEmpty() && it.status == "Paid"){
                                    statusLayout.visibility = View.VISIBLE
                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    contactBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE
                                    contactBtn2.visibility = View.VISIBLE
                                }

                                loadAgentDetails(it.agentId)
                                loadAccommodationDetails(it.accomID)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun loadAgentDetails(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
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