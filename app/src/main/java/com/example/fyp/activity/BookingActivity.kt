package com.example.fyp.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var tvCheckInDate: TextView
    private lateinit var tvCheckOutDate: TextView
    private lateinit var tvAgentName: TextView
    private lateinit var tvAgentEmail: TextView
    private lateinit var tvAgentContact: TextView
    private lateinit var tvContract: TextView
    private lateinit var tvRentFeeMonth: TextView
    private lateinit var tvDepositFee: TextView
    private lateinit var tvTotalFee: TextView
    private lateinit var btnBook: Button
    private lateinit var accomID: String
    private var agentID:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        btnBook = findViewById(R.id.bookBtn)

        accomID = intent.getStringExtra("ACCOM_ID").toString()
        agentID = intent.getStringExtra("agentId").toString()

        tvCheckInDate = findViewById(R.id.tvCheckInDate)
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate)
        tvAgentName = findViewById(R.id.tvAgentName)
        tvAgentEmail = findViewById(R.id.tvAgentEmail)
        tvAgentContact = findViewById(R.id.tvAgentContact)
        tvContract = findViewById(R.id.tvContract)
        tvRentFeeMonth = findViewById(R.id.tvRentFeeMonth)
        tvDepositFee = findViewById(R.id.tvDepositFee)
        tvTotalFee = findViewById(R.id.tvTotalFee)

        setupToolbar()
        loadAgentDetails()
        loadAccommodationDetails()
        tvCheckInDate.setOnClickListener {
            showDatePickerDialog(isCheckIn = true)
        }

        btnBook.setOnClickListener {
            makeBooking()
        }

    }

    private fun showDatePickerDialog(isCheckIn: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (isCheckIn) {
                tvCheckInDate.text = formatDate(selectedDate.time)

                // Calculate check-out date based on agreement duration
                val agreementDuration = getAgreementDurationInYears(tvContract.text.toString())
                selectedDate.add(Calendar.YEAR, agreementDuration)
                tvCheckOutDate.text = formatDate(selectedDate.time)
            } else {
                tvCheckOutDate.text = formatDate(selectedDate.time)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun getAgreementDurationInYears(agreement: String): Int {
        return when (agreement) {
            "1 year" -> 1
            "2 years" -> 2
            "3 years" -> 3
            "4 years" -> 4
            "5 years" -> 5
            else -> 0 // Default or error case
        }
    }
    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    private fun calculateTotalFee(): Double {
        val rentFee = tvRentFeeMonth.text.toString().removePrefix("RM ").toDoubleOrNull() ?: 0.0
        tvDepositFee.text = "RM ${String.format("%.2f", rentFee * 2)}"
        return rentFee + (rentFee * 2)
    }

    private fun loadAccommodationDetails() {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accommodation = snapshot.getValue(Accommodations::class.java)
                accommodation?.let {
                    tvContract.text = it.agreement
                    tvRentFeeMonth.text = "RM ${String.format("%.2f", it.rentFee.toDouble())}"
                    // Set other accommodation details if needed
                }
                tvTotalFee.text = "RM ${String.format("%.2f", calculateTotalFee())}"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadAgentDetails() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.child(agentID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                user?.let {
                    tvAgentName.text = it.fullName
                    tvAgentEmail.text = it.email
                    tvAgentContact.text = it.phoneNumber
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun makeBooking() {
        // Check if check-in and check-out dates are selected
        if (tvCheckInDate.text.equals("Click here to select date")) {
            showToast("Please select check-in date")
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")

        val booking = Bookings(
            accomID = accomID,
            userId = userId,
            agentId = agentID,
            checkIn = tvCheckInDate.text.toString(),
            checkOut = tvCheckOutDate.text.toString(),
            status = "Pending",
            total = String.format("%.2f", calculateTotalFee()),
            reason = "null"
        )

        bookingRef.push().setValue(booking).addOnSuccessListener {
            showToast("Booking made successfully")
            val intent = Intent(this, BookingListActivity::class.java)
            startActivity(intent)

        }.addOnFailureListener {
            showToast("Failed to make booking")
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
            val intent = Intent(this, AccommodationDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", accomID)
            startActivity(intent)
        }
    }
}