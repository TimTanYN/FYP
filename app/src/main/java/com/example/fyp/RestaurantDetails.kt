package com.example.fyp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.ContractCard
import com.example.fyp.adapter.Restaurant
import com.example.fyp.adapter.restaurant_review_adapter

class RestaurantDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_details)

        val restaurant = intent.getSerializableExtra("RESTAURANT_DETAILS") as? Restaurant
       val restaurantAddress = findViewById<TextView>(R.id.restaurantAddress)
        if (restaurant != null) {
            restaurantAddress.text = restaurant.address
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