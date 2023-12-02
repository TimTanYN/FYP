package com.example.fyp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.adapter.FeedbackEnd
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackResponse:AppCompatActivity() {

    private lateinit var feedback: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_response)

        val feedbackEnd = intent.getSerializableExtra("feedback") as FeedbackEnd
        println(feedbackEnd.id)
        feedback = db.collection("Feedback").document("userId").collection("feedback").document(feedbackEnd.id)
        val comment = findViewById<TextView>(R.id.comment)
        val pic = findViewById<ImageView>(R.id.profilePicture)
        val rating = findViewById<RatingBar>(R.id.rating)
        val name = findViewById<TextView>(R.id.profileName)
        name.text = feedbackEnd.name
        rating.rating = feedbackEnd.rating.toFloat()
        pic.setImageResource(feedbackEnd.profilePicture)
        comment.text = feedbackEnd.comment

        val responseButton = findViewById<Button>(R.id.responseButton)
        responseButton.setOnClickListener(){
            response()
        }

        val delete = findViewById<Button>(R.id.delete)
        delete.setOnClickListener(){
            delete()
        }

        val detail = findViewById<Button>(R.id.detailButton)
        detail.setOnClickListener(){
            val intent = Intent(this, PhotoAndVideo::class.java)
            intent.putExtra("feedback", feedbackEnd)
            startActivity(intent)
        }
    }
    val db = FirebaseFirestore.getInstance()

    private fun response(){

        val responseText = findViewById<EditText>(R.id.response).text.toString()
        feedback.update("response", responseText)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                val intent = Intent(this, FeedbackEnd::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
            }
    }

    private fun delete(){
        feedback.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document successfully deleted")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting document", e)
            }

        val intent = Intent(this, FeedbackEnd::class.java)
        startActivity(intent)
    }
    }
