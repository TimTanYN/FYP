package com.example.fyp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class DeleteCardOwnerActivity : AppCompatActivity() {

    private lateinit var cardNumInput: EditText
    private lateinit var expiryDateInput: EditText
    private lateinit var secureCodeInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_card)

        cardNumInput = findViewById(R.id.cardNumInput)
        expiryDateInput = findViewById(R.id.expiryDateInput)
        secureCodeInput = findViewById(R.id.secureCodeInput)
        val btnDel= findViewById<Button>(R.id.delCardBtn)

        val cardNumber = intent.getStringExtra("CARD_NUMBER")
        fetchCardDetails(cardNumber)
        setupToolbar()
        cardNumInput.isEnabled = false
        expiryDateInput.isEnabled = false
        secureCodeInput.isEnabled = false

        btnDel.setOnClickListener{
            showDeleteConfirmationDialog(cardNumber)
        }

    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccountOwnerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCardDetails(cardNumber: String?) {
        cardNumber?.let {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("Cards")
                .whereEqualTo("cardNumber", cardNumber)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val card = documents.documents.first().toObject(Cards::class.java)
                        displayCardDetails(card)
                    } else {
                        showToast("Card not found")
                    }
                }
                .addOnFailureListener {
                    showToast("Error fetching card details")
                }
        }
    }

    private fun displayCardDetails(card: Cards?) {
        card?.let {
            cardNumInput.setText(it.cardNumber)
            expiryDateInput.setText(it.cardExp)
            secureCodeInput.setText(it.cardCvv)
        }
    }

    private fun showDeleteConfirmationDialog(cardNumber: String?) {
        AlertDialog.Builder(this)
            .setTitle("Delete Card")
            .setMessage("Are you sure you want to delete this card?")
            .setPositiveButton("Delete") { dialog, which ->
                cardNumber?.let { deleteCard(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCard(cardNumber: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Cards")
            .whereEqualTo("cardNumber", cardNumber)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("Cards").document(document.id).delete()
                }
                showToast("Card deleted")
                // After deleting the card, check for accommodations
                checkAccommodationsAndNavigate()
            }
            .addOnFailureListener {
                showToast("Error deleting card")
            }
    }

    private fun checkAccommodationsAndNavigate() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.orderByChild("ownerId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        showToast("Please add on new card")
                        // Navigate to AddCardOwnerActivity if there are accommodations
                        val intent = Intent(this@DeleteCardOwnerActivity, AddCardOwnerActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        val intent = Intent(this@DeleteCardOwnerActivity, AccountOwnerActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}