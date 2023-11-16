package com.example.fyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fyp.R

data class Restaurant(
    val name: String,
    val address: String,
    val rating: Double?,
    val openNow: Boolean?,
    val photoUrl: String?
)

class RestaurantAdapter(private var restaurantList: List<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.restaurant_name)
        val addressTextView: TextView = view.findViewById(R.id.restaurant_address)
        val ratingTextView: TextView = view.findViewById(R.id.restaurant_rating)
        val openNowTextView: TextView = view.findViewById(R.id.restaurant_open_now)
        val imageView: ImageView = view.findViewById(R.id.restaurant_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address
        holder.ratingTextView.text = restaurant.rating?.toString() ?: "N/A"
        holder.openNowTextView.text = if (restaurant.openNow == true) "Open" else "Closed"
        Glide.with(holder.imageView.context).load(restaurant.photoUrl).into(holder.imageView)
    }

    override fun getItemCount() = restaurantList.size

    fun updateRestaurants(newRestaurants: List<Restaurant>) {
        restaurantList = newRestaurants
        notifyDataSetChanged()
    }
}