package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import java.io.Serializable

class FeedbackEndAdapter(var feedbackList: MutableList<FeedbackEnd>, private val clickListener: FeedbackEndAdapter.OnFeedbackEndClickListener): RecyclerView.Adapter<FeedbackEndAdapter.FeedbackViewHolder>() {
    class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val comment: TextView = view.findViewById(R.id.comment)
        val profilePicture: ImageView = view.findViewById(R.id.profilePicture)
        var profileName: TextView = view.findViewById(R.id.profileName)
        val rating: RatingBar = view.findViewById(R.id.rating)
        val delete: Button = view.findViewById(R.id.delete)
        val response: Button = view.findViewById(R.id.response)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_end_card, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.profileName.text = feedback.name
        holder.rating.rating = feedback.rating.toFloat()
        holder.comment.text = feedback.comment
        holder.profilePicture.setImageResource(R.drawable.profile)

        holder.itemView.setOnClickListener {
            clickListener.onFeedbackEndClick(feedback)
        }

        holder.delete.setOnClickListener {
            clickListener.onDeleteButtonClick(feedback, position)
        }

        holder.response.setOnClickListener {
            clickListener.onResponseButtonClick(feedback, position)
        }
    }

    override fun getItemCount() = feedbackList.size

    interface OnFeedbackEndClickListener {
        fun onFeedbackEndClick(feedbackEnd: FeedbackEnd)
        fun onDeleteButtonClick(feedbackEnd: FeedbackEnd, position: Int)
        fun onResponseButtonClick(feedbackEnd: FeedbackEnd, position: Int)
    }


}

data class FeedbackEnd(
    val name: String,
    val rating: Double,
    val profilePicture: Int,
    val comment:String,
    val id : String,
    val photoUrl : String,
    val videoUrl : String
): Serializable

