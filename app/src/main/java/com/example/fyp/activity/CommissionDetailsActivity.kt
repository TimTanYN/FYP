package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommissionDetailsActivity : AppCompatActivity() {

    private lateinit var tvTenantName: TextView
    private lateinit var tvTenantEmail: TextView
    private lateinit var tvTenantContact: TextView
    private lateinit var tvContract:TextView
    private lateinit var tvRentFee:TextView
    private lateinit var tvRate:TextView
    private lateinit var tvCommission:TextView

    private lateinit var imageContainer: LinearLayout
    private lateinit var accomID: String

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commission_details)

        tvTenantName = findViewById(R.id.tvTenantName)
        tvTenantEmail = findViewById(R.id.tvTenantEmail)
        tvTenantContact = findViewById(R.id.tvTenantContact)
        tvCommission= findViewById(R.id.tvCommission)
        tvRentFee = findViewById(R.id.tvRentFee)
        tvRate = findViewById(R.id.tvRate)
        tvContract = findViewById(R.id.tvContract)
        imageContainer = findViewById(R.id.imageContainer)


        accomID = intent.getStringExtra("ACCOM_ID").toString()
        setupToolbar()
        loadImagesForAccommodation(accomID)
        loadBookingDetails(accomID)

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

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, CommissionListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadImagesForAccommodation(accomID: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("AccommodationImages")
        databaseReference.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (accommodationImageSnapshot in snapshot.children) {
                            val imageUrl = accommodationImageSnapshot.child("images").getValue(String::class.java)
                            imageUrl?.let { displayImage(Uri.parse(it)) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun displayImage(uri: Uri) {

        // Create a new ImageView
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(400, 400) // Set your desired size
            scaleType = ImageView.ScaleType.FIT_XY // Adjust this as needed
        }


        // Use Glide to load the image into the ImageView
        Glide.with(this)
            .load(uri)
            .into(imageView)

        // Add the ImageView to the imageContainer
        imageContainer.addView(imageView)
    }
}