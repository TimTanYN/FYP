package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.BookingAdapter
import com.example.fyp.adapter.BookingHistoryAdapter
import com.example.fyp.database.Bookings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingHistoryListActivity : AppCompatActivity() {

    private lateinit var bookingListView: ListView
    private lateinit var noBookingListView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history_list)

        bookingListView = findViewById(R.id.bookingListView)
        noBookingListView = findViewById(R.id.noBookingListView)

        setupToolbar()
        loadBookings()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadBookings() {
        val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        bookingsRef.orderByChild("userId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val bookingsList = mutableListOf<Bookings>()
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            // Add only if the booking status is "Paid" or "Rejected"
                            if (booking?.status == "Paid" || booking?.status == "Rejected") {
                                bookingsList.add(booking)
                            }
                        }

                        if (bookingsList.isNotEmpty()) {
                            val adapter = BookingHistoryAdapter(this@BookingHistoryListActivity, bookingsList)
                            bookingListView.adapter = adapter
                            bookingListView.visibility = View.VISIBLE
                            noBookingListView.visibility = View.GONE
                        } else {
                            // No approved or rejected bookings found
                            bookingListView.visibility = View.GONE
                            noBookingListView.visibility = View.VISIBLE
                        }
                    } else {
                        // No bookings found
                        bookingListView.visibility = View.GONE
                        noBookingListView.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }



}