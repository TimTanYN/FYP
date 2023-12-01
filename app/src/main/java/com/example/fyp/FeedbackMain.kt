package com.example.fyp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.FeedbackAdapter
import com.example.fyp.adapter.Feedback

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
        val adapter = FeedbackAdapter(feedback)
        recyclerView.adapter = adapter
        println("Hi")
    }
}