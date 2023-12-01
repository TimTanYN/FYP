package com.example.fyp

import android.content.ContentValues
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.ContractCard
import com.example.fyp.adapter.ContractCardAdapter
import com.example.fyp.adapter.FeedbackAdapter
import com.example.fyp.adapter.Feedback
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackMain:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.feedback_main)

        val feedback = listOf(
            Feedback("Ilo123",2.0,R.drawable.address,"Thank You For Your Response"),

            // Add more products as needed
        )
        val recyclerView: RecyclerView = findViewById(R.id.feedback)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val db = FirebaseFirestore.getInstance()
        val yourDocumentReference = db.collection("Feedback").document("userId")
        yourDocumentReference.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Unknown" // Default value if null
                    val rating = document.getString("appValue")?.toDouble() ?: 0.0 // Default rating if null
                    val imageResId = R.drawable.address // Change as needed
                    val response = "Thank you for your response"

                    // Create a single Feedback object
                    val feedbackItem = Feedback(name, rating, imageResId, "Admin Response:\n$response")

                    // Since it's a single item, create a list containing only this item
                    val items = listOf(feedbackItem)

                    // Set up the adapter with the single-item list
                    val adapter = FeedbackAdapter(items)
                    recyclerView.adapter = adapter
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching document: ", exception)
            }
    }
}