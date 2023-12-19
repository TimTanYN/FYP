package com.example.fyp.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
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
import com.example.fyp.database.Workers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AccommodationJobDetailsActivity : AppCompatActivity() {

    private lateinit var edtAccName: EditText
    private lateinit var edtAccAddress1: EditText
    private lateinit var edtAccAddress2: EditText
    private lateinit var edtAccState: EditText
    private lateinit var edtAccCity: EditText
    private lateinit var rentFeeEditText: EditText
    private lateinit var regionEditText: EditText
    private lateinit var commissionEditText: EditText
    private lateinit var edtAccDesc: EditText
    private lateinit var ownerEditText: EditText
    private lateinit var imageContainer: LinearLayout
    private lateinit var accomID: String
    private lateinit var btnApply: Button
    private lateinit var btnContact: Button
    private val imageUris = mutableListOf<Uri>()
    private lateinit var ownerEmail: String
    private lateinit var ownerPhoneNumber: String
    private var ownerId:String = ""
    private var isApplied: Boolean = false
    private var workDate: String = ""

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_job_details)

        edtAccName = findViewById(R.id.edtAccName)
        edtAccAddress1 = findViewById(R.id.edtAccAddress1)
        edtAccAddress2 = findViewById(R.id.edtAccAddress2)
        edtAccState = findViewById(R.id.edtAccState)
        edtAccCity = findViewById(R.id.edtAccCity)
        rentFeeEditText = findViewById(R.id.rentFeeEditText)
        regionEditText = findViewById(R.id.regionEditText)
        commissionEditText = findViewById(R.id.commissionEditText)
        edtAccDesc = findViewById(R.id.edtAccDesc)
        ownerEditText = findViewById(R.id.ownerEditText)
        imageContainer = findViewById(R.id.imageContainer)
        btnApply = findViewById(R.id.btnApply)
        btnContact = findViewById(R.id.btnContact)

        accomID = intent.getStringExtra("ACCOM_ID").toString()
        setupSettings()
        setupToolbar()
        loadImagesForAccommodation(accomID)
        loadAccommodationData(accomID)
        checkIfApplied()

        btnApply.setOnClickListener {
//            if (isApplied) {
//                checkWithdrawalPossibility()
//                if (hasSixMonthsPassed(workDate)) {
//                    showWithdrawConfirmationDialog()
//                }
//            } else {
//                showApplyConfirmationDialog()
//            }
            checkCardExistenceAndApply()

        }
        btnContact.setOnClickListener {
            showContactOptions()
        }
    }

    private fun checkCardExistenceAndApply() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Cards")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Card exists, proceed to apply for job
                    if (isApplied) {
                        checkWithdrawalPossibility()
                        if (hasSixMonthsPassed(workDate)) {
                            showWithdrawConfirmationDialog()
                        }
                    } else {
                        showApplyConfirmationDialog()
                    }
                } else {
                    // No card found, navigate to Add Card Activity
                    val intent = Intent(this, AddCardAgentActivity::class.java)
                    showToast("Please add a card")
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                showToast("Failed to check card existence")
            }
    }


    private fun showWithdrawConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Withdraw Application")
            .setMessage("Are you sure you want to withdraw your application?")
            .setPositiveButton("Yes") { _, _ ->
                withdrawFromJob()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun checkIfApplied() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")

        workersRef.orderByChild("agentId").equalTo(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val worker = child.getValue(Workers::class.java)
                        if (worker != null && worker.accomID == accomID) {
                            btnApply.text = "Applied"
                            isApplied = true
                            workDate = worker.workDate
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to check application status")
            }
        })
    }

    private fun showApplyConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Apply for Job")
            .setMessage("Are you sure you want to apply for this job?  Please take note you can only withdraw after 6 months")
            .setPositiveButton("Yes") { _, _ ->
                applyForJob()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun applyForJob() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")

        val currentDate = getCurrentDate()
        val worker = Workers(accomID, currentUserId, currentDate, commissionEditText.text.toString())

        workersRef.push().setValue(worker).addOnSuccessListener {
            accommodationsRef.child(accomID).child("agentId").setValue(currentUserId)
            btnApply.text = "Applied"
            isApplied = true
            workDate = currentDate
            showToast("Successfully applied for job")
        }.addOnFailureListener {
            showToast("Failed to apply for job")
        }
    }

    private fun withdrawFromJob() {
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Find and delete the worker record
        workersRef.orderByChild("agentId").equalTo(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        if (child.child("accomID").getValue(String::class.java) == accomID) {
                            child.ref.removeValue().addOnSuccessListener {
                                accommodationsRef.child(accomID).child("agentId").setValue("null")
                                btnApply.text = "Apply Job"
                                isApplied = false
                                showToast("Successfully withdrawn from job")
                            }
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to withdraw from job")
            }
        })
    }

    private fun checkWithdrawalPossibility() {
        if (hasSixMonthsPassed(workDate)) {
            AlertDialog.Builder(this)
                .setTitle("Withdraw Application")
                .setMessage("Are you sure you want to withdraw your application?")
                .setPositiveButton("Yes") { _, _ ->
                    withdrawFromJob()
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            showToast("You can only withdraw after 6 months")
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun hasSixMonthsPassed(fromDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = dateFormat.parse(fromDate)
        val currentDate = Date()
        val diff = currentDate.time - (startDate?.time ?: 0)
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return days >= 180
    }

    private fun showContactOptions() {
        val options = arrayOf("Send email to owner", "Call owner")
        AlertDialog.Builder(this)
            .setTitle("Contact Owner")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendEmail(ownerEmail)
                    1 -> makePhoneCall(ownerPhoneNumber)
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
            val intent = Intent(this, AccommodationJobListActivity::class.java)
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
        ownerEditText.isEnabled = false
        commissionEditText.isEnabled = false
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
        databaseReference.child(accomID).addListenerForSingleValueEvent(object :
            ValueEventListener {
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

                        val commission = calculateCommission(it.rentFee,it.rate)

                        commissionEditText.setText(commission.toString())

                        ownerId = it.ownerId
                        loadOwnerName()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load accommodation data")
            }
        })
    }

    private fun calculateCommission(rentFee: String, rate: String): String {
        val monthlyCommission = rentFee.toDouble() * rate.toDouble()
        return "RM ${String.format("%.2f", monthlyCommission)}"
    }

    private fun loadOwnerName() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.child(ownerId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        ownerEditText.setText(it.fullName)
                        ownerEmail = it.email
                        ownerPhoneNumber = it.phoneNumber
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
        if (requestCode == AccommodationJobDetailsActivity.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
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