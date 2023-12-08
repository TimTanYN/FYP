package com.example.fyp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.ContractCard
import com.example.fyp.adapter.ContractCardAdapter
import com.example.fyp.adapter.FeedbackAdapter
import com.example.fyp.adapter.Feedback
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackMain:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.feedback_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView: RecyclerView = findViewById(R.id.feedback)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        println(userId)
        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Feedback")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val items = querySnapshot.documents.mapNotNull { document ->
                        val userID = document.getString("userId") ?: ""
                        if (userID == userId) {
                            val name = document.getString("name") ?: "Unknown"
                            val rating = document.getString("appValue")?.toDouble() ?: 0.0 // Use getDouble for numeric fields
                            val imageResId = R.drawable.profile // Assuming you have a default image
                            val response = document.getString("response") ?: ""
                            val id = document.id // Getting the document ID
                            Feedback(name, rating, imageResId, "Admin Response:\n$response", id)
                        } else {
                            null // Important for filtering out non-matching items
                        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_feedback -> {
                val intent = Intent(this, com.example.fyp.Feedback::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}