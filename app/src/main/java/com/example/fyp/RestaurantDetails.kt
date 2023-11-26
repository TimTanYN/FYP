package com.example.fyp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fyp.adapter.ContractCard
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
        if (restaurant != null) {
            Glide.with(restaurantImage.context).load(restaurant.photoUrl).into(restaurantImage)
            restaurantAddress.text = restaurant.address
            priceRange.text = restaurant.priceRange.toString()
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