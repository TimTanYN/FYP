package com.example.fyp.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.PublicTransport

class PublicTransportDetails:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.public_transport_details)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val transport = intent.getSerializableExtra("Transport_DETAILS") as? PublicTransport
        val departureStop = findViewById<TextView>(R.id.departureStop)
        val arrivalStop = findViewById<TextView>(R.id.arrivalStop)
        val departureTime = findViewById<TextView>(R.id.departureTime)
        val stops = findViewById<TextView>(R.id.stops)

        if (transport != null) {
            departureStop.text = transport.departure
            arrivalStop.text = transport.arrival
            departureTime.text = transport.departureTime
            stops.text = transport.numberOfStops
        }

        val polyline = findViewById<Button>(R.id.polyline)
        polyline.setOnClickListener(){
            val intent = Intent(this, Polyline::class.java).apply {
                putExtra("Transport_DETAILS", transport)

            }
            startActivity(intent)
        }

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