package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.Feedback
import com.example.fyp.adapter.FeedbackAdapter
import com.example.fyp.adapter.FeedbackEnd
import com.example.fyp.adapter.FeedbackEndAdapter
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackEnd:AppCompatActivity(), FeedbackEndAdapter.OnFeedbackEndClickListener{

    private val db = FirebaseFirestore.getInstance()
    private val documentReference = db.collection("Feedback").document("userId").collection("feedback")
    var feedbackItem: FeedbackEnd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.feedback_end)
        val recyclerView: RecyclerView = findViewById(R.id.feedbackEnd)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        documentReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val items = querySnapshot.documents.mapNotNull { document ->
                        val name = document.getString("name") ?: "Unknown"
                        val rating = document.getString("appValue")?.toDouble() ?: 0.0 // Default rating if null
                        val imageResId = R.drawable.address
                        val comment = document.getString("comment") ?: "Unknown"
                        val id = document.id
                        val photoUrl = document.getString("imageUrl") ?: ""
                        val videoUrl = document.getString("videoUrl") ?: ""

                        FeedbackEnd(name, rating, imageResId, comment, id, photoUrl, videoUrl)
                    }

                    // Set up the adapter with the list of items
                    val adapter = FeedbackEndAdapter(items.toMutableList(), this)
                    recyclerView.adapter = adapter
                } else {
                    Log.d("Firestore", "No documents found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error fetching documents: ", exception)
            }

    }

    override fun onFeedbackEndClick(feedbackEnd: FeedbackEnd) {
        // Handle item click
    }

    override fun onDeleteButtonClick(feedbackEnd: FeedbackEnd, position: Int) {
        val documentId = feedbackEnd.id
        val recyclerView: RecyclerView = findViewById(R.id.feedbackEnd)

        db.collection("yourCollection").document(documentId).delete()
            .addOnSuccessListener {
                // Remove the item from your data list and notify the adapter
                val items = (recyclerView.adapter as FeedbackEndAdapter).feedbackList
                items.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)
                Log.d("Firestore", "Document successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting document", e)
            }
    }

    override fun onResponseButtonClick(feedbackEnd: FeedbackEnd, position: Int) {
        val intent = Intent(this, FeedbackResponse::class.java)
        intent.putExtra("feedback", feedbackEnd)
        startActivity(intent)
    }

}