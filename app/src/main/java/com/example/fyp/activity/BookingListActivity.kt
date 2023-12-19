package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.BookingAdapter
import com.example.fyp.adapter.BookingAgentAdapter
import com.example.fyp.adapter.BottomNavigationHandler
import com.example.fyp.adapter.BottomNavigationHandlerAgent
import com.example.fyp.database.Bookings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingListActivity : AppCompatActivity() {

    private lateinit var bookingListView: ListView
    private lateinit var noBookingListView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_list)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_book
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        bookingListView = findViewById(R.id.bookingListView)
        noBookingListView = findViewById(R.id.noBookingListView)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        loadBookings()
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
                            // Add only if the booking status is "Pending"
                            if (booking?.status == "Pending" || booking?.status == "Approved") {
                                bookingsList.add(booking)
                            }
                        }

                        if (bookingsList.isNotEmpty()) {
                            val adapter = BookingAdapter(this@BookingListActivity, bookingsList)
                            bookingListView.adapter = adapter
                            bookingListView.visibility = View.VISIBLE
                            noBookingListView.visibility = View.GONE
                        } else {
                            // No pending bookings found
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