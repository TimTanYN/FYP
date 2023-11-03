package com.example.fyp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R

class TransitListAdapter(var transitDetails: List<String>) : RecyclerView.Adapter<TransitListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parts = transitDetails[position].split(", ")
        // Assume that each part follows "Key: Value" format
        val vehicleType = parts.find { it.startsWith("Vehicle Type:") }?.substringAfter(": ") ?: "N/A"
        val vehicleName = parts.find { it.startsWith("Vehicle Name:") }?.substringAfter(": ") ?: "N/A"
        val line = parts.find { it.startsWith("Line:") }?.substringAfter(": ") ?: "N/A"

        holder.textView.text = "Vehicle Type: $vehicleType\nVehicle Name: $vehicleName\nLine: $line"
    }

    override fun getItemCount(): Int {
        Log.d("TransitDetailLog", transitDetails.size.toString())
        return transitDetails.size
    }
}