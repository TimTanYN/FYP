package com.example.fyp.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.adapter.Restaurant
import com.example.fyp.adapter.restaurant_review_adapter

class RestaurantDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_details)

        val restaurant = intent.getSerializableExtra("RESTAURANT_DETAILS") as? Restaurant

        val restaurantImage = findViewById<ImageView>(R.id.restaurantImage)
        val restaurantAddress = findViewById<TextView>(R.id.restaurantAddress)
        val priceRange = findViewById<TextView>(R.id.restaurantPriceRange)
        val rating = findViewById<TextView>(R.id.rating)
        val restaurantOpenNow = findViewById<TextView>(R.id.restaurantOpenNow)

        when(restaurant?.priceRange){
            0 ->  priceRange.text = "Free"
            1 ->  priceRange.text = "Inexpensive"
            2 ->  priceRange.text = "Moderate"
            3 ->  priceRange.text = "Expensive"
            4 ->  priceRange.text = "Very Expensive"
            else -> priceRange.text = "Unknown"
        }

        when(restaurant?.openNow){
            true ->  restaurantOpenNow.text = "Open"
            false ->  restaurantOpenNow.text = "Close"
            else -> restaurantOpenNow.text = "Unknown"
        }
        if (restaurant != null) {
            Glide.with(restaurantImage.context).load(restaurant.photoUrl).into(restaurantImage)
            restaurantAddress.text = restaurant.address
            rating.text = restaurant.rating.toString()
        }

        var list: MutableList<Review> = mutableListOf()
        if (restaurant != null) {
            for (review in restaurant.reviews) {
                list.add(Review(review.authorName, review.rating, review.text))
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.restaurantComment)
        recyclerView.layoutManager = LinearLayoutManager(this) // Assuming 'this' is a Context
        recyclerView.adapter = restaurant_review_adapter(list)
    }
}