package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.fyp.R
import com.example.fyp.adapter.PrivacyPolicyAdapter
import com.example.fyp.viewmodel.PrivacyPolicyViewModel

class PrivacyActivity : AppCompatActivity() {
    private lateinit var viewModel: PrivacyPolicyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            // This will be called when the back arrow is clicked
            onBackPressed()
        }

        viewModel = ViewModelProvider(this).get(PrivacyPolicyViewModel::class.java)

        val link = "http:exampleprivacy.com" // Replace with your actual link
        val contactInfo = "example@gmail.com" // Replace with your actual contact info
        val items = viewModel.getPrivacyPolicyItems(this, link, contactInfo)
        Log.d("PrivacyPolicy", "Items count: ${items.size}")

        val adapter = PrivacyPolicyAdapter(this, items)
        findViewById<ListView>(R.id.lvPrivacyPolicy).adapter = adapter
    }
}