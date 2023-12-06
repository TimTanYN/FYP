package com.example.fyp.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccommodationDetailsOwnerActivity : AppCompatActivity() {

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
    private lateinit var btnTenant: Button
    private lateinit var btnAgent: Button
    private val imageUris = mutableListOf<Uri>()
    private var tenantId:String = ""
    private var agentId:String = ""

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_details_owner)

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
        btnTenant = findViewById(R.id.btnTenant)
        btnAgent = findViewById(R.id.btnAgent)

        accomID = intent.getStringExtra("ACCOM_ID").toString()
        setupSettings()
        setupToolbar()
        loadImagesForAccommodation(accomID)
        loadAccommodationData(accomID)

        btnAgent.setOnClickListener {
            val intent = Intent(this, AccommodationUserActivity::class.java)
            intent.putExtra("userId", agentId)
            intent.putExtra("ACCOM_ID", accomID)
            startActivity(intent)
        }

        btnTenant.setOnClickListener {
            val intent = Intent(this, AccommodationUserActivity::class.java)
            intent.putExtra("userId", tenantId)
            intent.putExtra("ACCOM_ID", accomID)
            startActivity(intent)
        }
    }


    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ManageAccommodationActivity::class.java)
            startActivity(intent)
        }
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
                        // Check if agentId is null or "null" and set text accordingly
                        val agentText = if (agentId == "null") {
                            "No applied by agent"
                        }else{
                            loadAgentName()
                            btnAgent.visibility = View.VISIBLE
                        }

                        agentEditText.setText(agentText.toString())


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
                        agentEditText.setText(it.fullName) // Replace with your TextView ID for agent
                    }
                } else {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load agent data")
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
        if (requestCode == AccommodationDetailsOwnerActivity.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
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