package com.example.fyp.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccommodationDetailsActivity : AppCompatActivity() {

    private lateinit var edtAccName: EditText
    private lateinit var edtAccAddress1: EditText
    private lateinit var edtAccAddress2: EditText
    private lateinit var edtAccState: EditText
    private lateinit var edtAccCity: EditText
    private lateinit var rentFeeEditText: EditText
    private lateinit var regionEditText: EditText
    private lateinit var contractEditText: EditText
    private lateinit var edtAccDesc: EditText
    private lateinit var agentEditText: EditText
    private lateinit var imageContainer: LinearLayout
    private lateinit var accomID: String
    private lateinit var btnMake: Button
    private lateinit var btnContact: Button
    private val imageUris = mutableListOf<Uri>()
    private lateinit var agentEmail: String
    private lateinit var agentPhoneNumber: String
    private var agentId:String = ""

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_details)

        edtAccName = findViewById(R.id.edtAccName)
        edtAccAddress1 = findViewById(R.id.edtAccAddress1)
        edtAccAddress2 = findViewById(R.id.edtAccAddress2)
        edtAccState = findViewById(R.id.edtAccState)
        edtAccCity = findViewById(R.id.edtAccCity)
        rentFeeEditText = findViewById(R.id.rentFeeEditText)
        regionEditText = findViewById(R.id.regionEditText)
        contractEditText = findViewById(R.id.contractEditText)
        edtAccDesc = findViewById(R.id.edtAccDesc)
        agentEditText = findViewById(R.id.agentEditText)
        imageContainer = findViewById(R.id.imageContainer)
        btnMake = findViewById(R.id.btnMake)
        btnContact = findViewById(R.id.btnContact)

        accomID = intent.getStringExtra("ACCOM_ID").toString()
        setupSettings()
        setupToolbar()
        loadImagesForAccommodation(accomID)
        loadAccommodationData(accomID)

        btnMake.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("agentId", agentId)
            intent.putExtra("ACCOM_ID", accomID)
            startActivity(intent)
        }

        btnContact.setOnClickListener {
            showContactOptions()
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccommodationListActivity::class.java)
            startActivity(intent)
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
    private fun setupSettings(){

        edtAccName.isEnabled = false
        edtAccAddress1.isEnabled = false
        edtAccAddress2.isEnabled = false
        edtAccState.isEnabled = false
        edtAccCity.isEnabled = false
        rentFeeEditText.isEnabled = false
        agentEditText.isEnabled = false
        contractEditText.isEnabled = false
        regionEditText.isEnabled = false
        edtAccDesc.isEnabled = false
        edtAccDesc.isVerticalScrollBarEnabled = true
        edtAccDesc.setScroller(Scroller(this))
        edtAccDesc.maxLines = Integer.MAX_VALUE
        edtAccDesc.movementMethod = ScrollingMovementMethod.getInstance()
        edtAccDesc.setOnTouchListener { v, event ->
            if (v.id == R.id.edtAccDesc) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

    }

    private fun loadAccommodationData(accomID: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Accommodations")
        databaseReference.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    accommodation?.let {
                        // Remove hint before setting text
                        edtAccName.setText(it.accomName)

                        edtAccAddress1.setText(it.accomAddress1)

                        edtAccAddress2.setText(it.accomAddress2)

                        val rentFee = it.rentFee.toDoubleOrNull() ?: 0.0
                        rentFeeEditText.setText("RM ${String.format("%.2f", rentFee)}")

                        regionEditText.setText("Malaysia")

                        edtAccDesc.setText(it.accomDesc)

                        edtAccState.setText(it.state)

                        edtAccCity.setText(it.city)

                        contractEditText.setText(it.agreement)

                        agentId = it.agentId
                        loadAgentName()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load accommodation data")
            }
        })
    }

    private fun loadAgentName() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.child(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        agentEditText.setText(it.fullName)
                        agentEmail = it.email
                        agentPhoneNumber = it.phoneNumber
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load data")
            }
        })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AccommodationDetailsActivity.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val processImage = { uri: Uri ->
                imageUris.add(uri) // Add URI to the list
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(400, 400) // Set your desired size
                    scaleType = ImageView.ScaleType.FIT_XY
                    setImageURI(uri)
                }
                imageContainer.addView(imageView)
            }

            data?.clipData?.let { clipData ->
                // Multiple images selected
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    processImage(imageUri)
                }
            } ?: data?.data?.let { imageUri ->
                // Single image selected
                processImage(imageUri)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}