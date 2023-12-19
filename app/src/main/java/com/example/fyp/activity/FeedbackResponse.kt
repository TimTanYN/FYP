package com.example.fyp.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.FeedbackEnd
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackResponse:AppCompatActivity() {

    private lateinit var feedback: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_response)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val feedbackEnd = intent.getSerializableExtra("feedback") as FeedbackEnd

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
            response(feedbackEnd.id)
        }



        val detail = findViewById<Button>(R.id.detailButton)
        detail.setOnClickListener(){
            val intent = Intent(this, PhotoAndVideo::class.java)
            intent.putExtra("feedback", feedbackEnd)
            startActivity(intent)
        }
    }
    val db = FirebaseFirestore.getInstance()

    private fun response(id:String){

        val responseText = findViewById<EditText>(R.id.response).text.toString()
        feedback = db.collection("Feedback").document(id)
        feedback.update("response", responseText)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(this, "Saved to Database", Toast.LENGTH_LONG).show()
                val intent = Intent(this, com.example.fyp.activity.FeedbackEnd::class.java)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    }
