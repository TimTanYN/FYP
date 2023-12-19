package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingDetailsAgentActivity : AppCompatActivity() {
    private lateinit var tvCheckInDate:TextView
    private lateinit var tvCheckOutDate:TextView
    private lateinit var tvTenantName: TextView
    private lateinit var tvTenantEmail: TextView
    private lateinit var tvTenantContact: TextView
    private lateinit var tvCommission:TextView
    private lateinit var tvTotalFee:TextView
    private lateinit var tvRentFee:TextView
    private lateinit var tvRate:TextView
    private lateinit var tvDepositFee:TextView
    private lateinit var tvContract:TextView
    private lateinit var contactBtn:Button
    private lateinit var contactBtn2:Button
    private lateinit var approveBtn:Button
    private lateinit var rejectBtn:Button
    private lateinit var etRejectReason:EditText
    private lateinit var statusLayout: LinearLayout
    private lateinit var tvStatus:TextView
    private lateinit var tvReason:TextView

    private lateinit var accomID: String
    private lateinit var tenantEmail: String
    private lateinit var tenantPhoneNumber: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details_agent)

        tvCheckInDate = findViewById(R.id.tvCheckInDate)
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate)
        tvTenantName = findViewById(R.id.tvTenantName)
        tvTenantEmail = findViewById(R.id.tvTenantEmail)
        tvTenantContact = findViewById(R.id.tvTenantContact)
        tvCommission= findViewById(R.id.tvCommission)
        tvTotalFee = findViewById(R.id.tvTotalFee)
        tvRentFee = findViewById(R.id.tvRentFee)
        tvRate = findViewById(R.id.tvRate)
        tvDepositFee = findViewById(R.id.tvDepositFee)
        tvContract = findViewById(R.id.tvContract)
        contactBtn = findViewById(R.id.contactBtn)
        contactBtn2 = findViewById(R.id.contactBtn2)
        approveBtn = findViewById(R.id.approveBtn)
        rejectBtn = findViewById(R.id.rejectBtn)
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

        approveBtn.setOnClickListener {
            showApproveDialog()
        }

        rejectBtn.setOnClickListener {
            showRejectDialog()
        }
    }

    private fun showContactOptions() {
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
            val intent = Intent(this, BookingListAgentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showApproveDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Approval")
            .setMessage("Are you sure you want to approve this booking?")
            .setPositiveButton("Approve") { _, _ ->
                updateBookingStatus("Approved")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateBookingStatus(newStatus: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.accomID == accomID) {
                                bookingSnapshot.ref.child("status").setValue(newStatus)
                                    .addOnSuccessListener {
                                        showToast("Booking Approved")
                                        loadBookingDetails(accomID) // Refresh the data
                                    }
                                    .addOnFailureListener {
                                        showToast("Failed to approve booking")
                                    }
                                break
                            }
                        }
                    } else {
                        showToast("Booking not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showRejectDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject_reason, null)
        etRejectReason = dialogView.findViewById(R.id.etRejectReason)

        // Regular expression to allow letters, digits, spaces, commas, and periods
        val allowedCharactersRegex = "[a-zA-Z0-9,.\\s]+"

        // Input filter that uses the regular expression
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex(allowedCharactersRegex))) {
                null // Accept the original input
            } else {
                "" // Reject the input
            }
        }

        // Apply the input filter to the EditText
        etRejectReason.filters = arrayOf(inputFilter)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Submit", null) // We set the listener later to prevent auto-dismissal
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (isInputEmpty(etRejectReason)) {
                    etRejectReason.error = "Reason cannot be empty"
                } else {
                    val reason = etRejectReason.text.toString()
                    // Handle the rejection reason submission here
                    updateBookingWithReason(reason)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
    private fun updateBookingWithReason(reason: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.accomID == accomID) {
                                // Update the reason and status of the found booking
                                bookingSnapshot.ref.child("reason").setValue(reason)
                                bookingSnapshot.ref.child("status").setValue("Rejected")
                                    .addOnSuccessListener {
                                        showToast("Booking Rejected")
                                        loadBookingDetails(accomID) // Refresh the data

                                    }
                                    .addOnFailureListener {
                                        showToast("Failed to reject booking")
                                    }
                                break // Break after updating the first matching record
                            }
                        }
                    } else {
                        showToast("Booking not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun isInputEmpty(input: EditText): Boolean {
        return input.text.toString().trim().isEmpty()
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
                                    approveBtn.visibility = View.GONE
                                    rejectBtn.visibility = View.GONE
                                    contactBtn.visibility = View.GONE

                                }else if(it.status.isNotEmpty() && it.status == "Approved"){
                                    statusLayout.visibility = View.VISIBLE

                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    approveBtn.visibility = View.GONE
                                    rejectBtn.visibility = View.GONE
                                    contactBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE
                                    contactBtn2.visibility = View.VISIBLE

                                }else if (it.status.isNotEmpty() && it.status == "Paid"){
                                    statusLayout.visibility = View.VISIBLE

                                    val reason = findViewById<TextView>(R.id.tvReason)
                                    val tvreason = findViewById<TextView>(R.id.tvBlank13)
                                    reason.visibility = View.GONE
                                    tvreason.visibility = View.GONE
                                    approveBtn.visibility = View.GONE
                                    rejectBtn.visibility = View.GONE
                                    contactBtn.visibility = View.GONE
                                    val view = findViewById<View>(R.id.view4)
                                    view.visibility = View.VISIBLE
                                    contactBtn2.visibility = View.VISIBLE

                                }

                                loadTenantDetails(it.userId)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}