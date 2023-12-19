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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Cards
import com.example.fyp.database.Payments
import com.example.fyp.database.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class BookingDetailsActivity : AppCompatActivity() {
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
    private lateinit var payBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var statusLayout: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var tvReason: TextView

    private lateinit var accomID: String
    private lateinit var agentEmail: String
    private lateinit var agentPhoneNumber: String
    private lateinit var agentId: String
    private lateinit var ownerId: String
    private lateinit var commission: String
    private lateinit var rent: String
    private lateinit var deposit: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

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
        payBtn = findViewById(R.id.payBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        statusLayout = findViewById(R.id.combinedLayout4)
        tvStatus = findViewById(R.id.tvStatus)
        tvReason = findViewById(R.id.tvReason)

        setupToolbar()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        accomID = intent.getStringExtra("ACCOM_ID").toString()

        loadBookingDetails(accomID)
        contactBtn.setOnClickListener {
            showContactOptions()
        }

        contactBtn2.setOnClickListener {
            showContactOptions()
        }

        payBtn.setOnClickListener {
            checkUserCardAndPay()
        }

        cancelBtn.setOnClickListener {
            showCancelConfirmationDialog()
        }


    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes") { _, _ ->
                cancelBooking()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelBooking() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.userId == currentUserId) {
                                // Delete the booking record
                                bookingSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        showToast("Booking cancelled successfully")
                                        val intent = Intent(this@BookingDetailsActivity, BookingListActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        showToast("Failed to cancel booking")
                                    }
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun checkUserCardAndPay() {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("Cards")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val card = documents.documents.first().toObject(Cards::class.java)
                    card?.let {
                        showPaymentConfirmationDialog(it.cardNumber)
                    }
                } else {
                    // No card found, navigate to Add Card Activity
                    showToast("Please add a card")
                    val intent = Intent(this, AddCardActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun showPaymentConfirmationDialog(cardNumber: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment")
            .setMessage("Do you want to use card XXXX-XXXX-XXXX- ${cardNumber.takeLast(4)} to pay?")
            .setPositiveButton("Yes") { _, _ ->
                processPayment(cardNumber)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun processPayment(cardNumber: String) {
        // Fetch agent's and owner's card details
        fetchCardDetails(agentId) { agentCard ->
            fetchCardDetails(ownerId) { ownerCard ->
                // Create and store payment record
                val payment = Payments(
                    accomID = accomID,
                    tenantId = auth.currentUser?.uid ?: "",
                    agentId = agentId,
                    deposit = deposit,
                    commission = commission,
                    rentMonth = rent,
                    total = tvTotalFee.text.toString().replace("RM", "").trim(),
                    tenantCard = cardNumber,
                    agentCard = agentCard,
                    ownerCard = ownerCard
                )

                firestore.collection("Payments")
                    .add(payment)
                    .addOnSuccessListener {
                        // Handle successful payment
                        showToast("Payment Success")
                        updateOtherBookingsStatus()
                        updateCurrentUserBookingStatus()
                        val intent = Intent(this, SettingActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        // Handle payment failure
                        showToast("Payment Failed")
                    }
            }
        }
    }

    private fun updateCurrentUserBookingStatus() {
        val currentUserId = auth.currentUser?.uid ?: return
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.userId == currentUserId) {
                                // Update status to "Paid" for the current user's booking
                                bookingSnapshot.ref.child("status").setValue("Paid")
                                break // Assuming only one active booking per user per accommodation
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun updateOtherBookingsStatus() {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.userId != auth.currentUser?.uid) {
                                // Update status to "Rejected" for other bookings
                                bookingSnapshot.ref.child("status").setValue("Rejected")
                                bookingSnapshot.ref.child("reason").setValue("It has been sold")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun fetchCardDetails(userId: String, callback: (String) -> Unit) {
        firestore.collection("Cards")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val card = documents.documents.first().toObject(Cards::class.java)
                    callback(card?.cardNumber ?: "")
                } else {
                    callback("")
                }
            }
            .addOnFailureListener {
                callback("")
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
            val intent = Intent(this, BookingListActivity::class.java)
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
                                    payBtn.visibility = View.GONE
                                    contactBtn.visibility = View.GONE

                                }else if(it.status.isNotEmpty() && it.status == "Approved"){
                                    statusLayout.visibility = View.VISIBLE

                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    contactBtn.visibility = View.GONE
                                    cancelBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE
                                    contactBtn2.visibility = View.VISIBLE
                                    payBtn.visibility = View.VISIBLE

                                }else if (it.status.isNotEmpty() && it.status == "Paid"){
                                    statusLayout.visibility = View.VISIBLE
                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    contactBtn.visibility = View.GONE
                                    cancelBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE
                                    contactBtn2.visibility = View.VISIBLE
                                }else{
                                    statusLayout.visibility = View.VISIBLE
                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    contactBtn.visibility = View.GONE
                                    contactBtn2.visibility = View.VISIBLE
                                    view.visibility = View.VISIBLE
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

                    val monthlyCommission = it.rentFee.toDouble() * it.rate.toDouble()
                    commission = String.format("%.2f", monthlyCommission)
                    rent = String.format("%.2f", it.rentFee.toDouble())
                    deposit = String.format("%.2f", depositFee)

                    // Store agentId and ownerId
                    agentId = it.agentId
                    ownerId = it.ownerId
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
}