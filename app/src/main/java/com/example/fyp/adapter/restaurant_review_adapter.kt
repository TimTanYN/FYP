package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.activity.Review

class restaurant_review_adapter (private val reviewList: List<Review>) : RecyclerView.Adapter<restaurant_review_adapter.ReviewViewHolder>(){

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.username)
        val profilePictureImageView: ImageView = view.findViewById(R.id.profilePicture)
        var textTextView: TextView = view.findViewById(R.id.text)
        val ratingTextView: TextView = view.findViewById(R.id.rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_review_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        holder.usernameTextView.text = review.authorName
        holder.textTextView.text = review.text
        val formattedReview = formatReviewText(review.text)
        println("Formatted review text: $formattedReview") // Check formatted text
        holder.ratingTextView.text = review.rating.toString()
        holder.textTextView.setOnClickListener {
            // Toggle between expanded and collapsed state
            holder.textTextView.maxLines = if (holder.textTextView.maxLines == 3) Integer.MAX_VALUE else 3
        }

        // Set an image for profilePictureImageView if available
        // For example, using a placeholder image:
        holder.profilePictureImageView.setImageResource(R.drawable.profile)
    }

    override fun getItemCount() = reviewList.size

    fun formatReviewText(review: String): String {
        return review.trim().replace(Regex("\\s+"), " ")
    }
}




