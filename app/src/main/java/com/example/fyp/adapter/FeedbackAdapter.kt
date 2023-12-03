package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R

class FeedbackAdapter(private val feedbackList: List<Feedback>): RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val response: TextView = view.findViewById(R.id.response)
        val profilePicture: ImageView = view.findViewById(R.id.profilePicture)
        var profileName: TextView = view.findViewById(R.id.profileName)
        val rating: RatingBar = view.findViewById(R.id.rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_card, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.response.text = feedback.response
        holder.profileName.text = feedback.name
        holder.rating.rating = feedback.rating.toFloat()
        holder.profilePicture.setImageResource(R.drawable.address)
        println(feedbackList.size)
    }

    override fun getItemCount() = feedbackList.size


}

data class Feedback(
    val name: String,
    val rating: Double,
    val photoUrl: Int,
    val response:String,
    val id : String
)