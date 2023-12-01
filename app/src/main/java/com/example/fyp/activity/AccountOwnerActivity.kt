package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.adapter.BottomNavigationHandler
import com.example.fyp.database.Users
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class AccountOwnerActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var cardsDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_owner)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_profile
        val navigationHandler = BottomNavigationHandler(this)
        navigationHandler.setupBottomNavigation(bottomNavigationView)

        val edit: CircleImageView = findViewById(R.id.editProfile)
        val add: Button = findViewById(R.id.btnAddNewCard)

        // Initialize Firebase Auth and Database Reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("Users")
        cardsDatabase = FirebaseDatabase.getInstance().getReference("Cards")


        userId?.let {
            getUserData(it)
            checkForCards(it)

        }

        edit.setOnClickListener{
            val intent = Intent(this, EditProfileOwnerActivity::class.java)
            startActivity(intent)
        }

        add.setOnClickListener{
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkForCards(userId: String) {
        cardsDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Cards exist for this user
                    findViewById<ListView>(R.id.cardList).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.nocardList).visibility = View.GONE
                } else {
                    // No cards for this user
                    findViewById<ListView>(R.id.cardList).visibility = View.GONE
                    findViewById<TextView>(R.id.nocardList).visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to retrieve card data")
            }
        })
    }

    private fun getUserData(userId: String) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                if (user != null) {
                    runOnUiThread {
                        try {
                            val profileImageView: ImageView = findViewById(R.id.profile_background)


                            Glide.with(this@AccountOwnerActivity)
                                .load(user.imageLink)
                                .into(profileImageView)
                            findViewById<TextView>(R.id.txtFullName)?.text = user.fullName
                            findViewById<TextView>(R.id.txtEmail)?.text = user.email
                            findViewById<TextView>(R.id.txtPhoneNumber)?.text = user.phoneNumber.drop(3)
                            findViewById<TextView>(R.id.txtCountryCode)?.text = user.phoneNumber.take(3)
                        } catch (e: Exception) {
                            Log.e("AccountActivity", "Error updating UI", e)
//                            showToast("Error setting user data")
                        }
                    }
                } else {
                    showToast("User data is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to retrieve data")
            }
        })
    }

    private fun updateUI(user: Users) {
        val fullNameTextView: TextView = findViewById(R.id.txtFullName)
        val emailTextView: TextView = findViewById(R.id.txtEmail)
        val countryCodeTextView: TextView = findViewById(R.id.txtCountryCode)
        val phoneNumberTextView: TextView = findViewById(R.id.txtPhoneNumber)
        val profileImageView: ImageView = findViewById(R.id.profile_image)

        val countryCode = user.phoneNumber.take(3)
        val phoneNumber = user.phoneNumber.drop(3)

        Glide.with(this)
            .load(user.imageLink)
            .into(profileImageView)

        fullNameTextView.text = user.fullName
        emailTextView.text = user.email
        countryCodeTextView.text = countryCode
        phoneNumberTextView.text = phoneNumber
        showToast(user.fullName)
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}