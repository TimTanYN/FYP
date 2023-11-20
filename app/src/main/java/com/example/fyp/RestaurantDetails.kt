package com.example.fyp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.adapter.Restaurant

class RestaurantDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_details)

        val restaurant = intent.getSerializableExtra("RESTAURANT_DETAILS") as? Restaurant
       val restaurantAddress = findViewById<TextView>(R.id.restaurantAddress)
        if (restaurant != null) {
            restaurantAddress.text = restaurant.address
        }
    }
}