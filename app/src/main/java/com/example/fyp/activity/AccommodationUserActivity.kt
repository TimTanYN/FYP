package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class AccommodationUserActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtMobile: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var profileImageView: CircleImageView
    private lateinit var btnContact: Button
    private var userId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accommodation_user)

        edtEmail = findViewById(R.id.edtEmail)
        edtName = findViewById(R.id.edtName)
        edtMobile = findViewById(R.id.edtMobile)
        profileImageView = findViewById(R.id.profile_image)
        btnContact = findViewById(R.id.btnContact)

        userId = intent.getStringExtra("userId").toString()

        setupToolbar()
        setupSettings()

        btnContact.setOnClickListener {
            showContactOptions()
        }

    }

    private fun loadUserData() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        edtEmail.setText(it.email)
                        edtName.setText(it.fullName)
                        edtMobile.setText(it.phoneNumber)
                        loadProfileImage(it.imageLink)
                        setupToolbarAndContactButton(it.userRole)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load user data")
            }
        })
    }

    private fun loadProfileImage(imageLink: String) {
        Glide.with(this)
            .load(imageLink)
            .into(profileImageView)
    }

    private fun setupToolbarAndContactButton(userRole: String) {
        when (userRole) {
            "User" -> {
                toolbar.title = "Tenant Details"
                btnContact.text = "Contact Tenant"
            }
            "Agent" -> {
                toolbar.title = "Agent Details"
                btnContact.text = "Contact Agent"
            }
        }
    }

    private fun showContactOptions() {
        val contactType = if (toolbar.title == "Tenant Details") "tenant" else "agent"
        val options = arrayOf("Send email to $contactType", "Call $contactType")
        AlertDialog.Builder(this)
            .setTitle("Contact Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendEmail(edtEmail.text.toString())
                    1 -> makePhoneCall(edtMobile.text.toString())
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
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No email app found")
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("No dialer app found")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccommodationDetailsOwnerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSettings(){

        edtEmail.isEnabled = false
        edtName.isEnabled = false
        edtMobile.isEnabled = false
    }

}