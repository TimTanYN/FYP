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

        val recyclerView: RecyclerView = findViewById(R.id.feedback)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Feedback").document("userId").collection("feedback")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val items = querySnapshot.documents.mapNotNull { document ->
                        val name = document.getString("name") ?: "Unknown"
                        val rating = document.getString("appValue")?.toDouble() ?: 0.0
                        val imageResId = R.drawable.address // Assuming you have a default image
                        val response = "Thank you for your response"
                        val id = document.id // Getting the document ID

                        Feedback(name, rating, imageResId, "Admin Response:\n$response", id)
                    }

                    // Set up the adapter with the list of items
                    val adapter = FeedbackAdapter(items)
                    recyclerView.adapter = adapter
                } else {
                    Log.d("Firestore", "No documents found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching documents: ", exception)
            }
    }
}