package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R

class PublicTransportAdapter (private var transportList: List<PublicTransport>) : RecyclerView.Adapter<PublicTransportAdapter.ProductViewHolder>() {
    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val transport: TextView = view.findViewById(R.id.transport)
        val ETA: TextView = view.findViewById(R.id.ETA)
        val transportName: TextView = view.findViewById(R.id.transportName)
        val estimatedTime: TextView = view.findViewById(R.id.estimatedTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.public_transport_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val PublicTransport = transportList[position]
        with(holder) {
            transport.text = PublicTransport.transport
            ETA.text = PublicTransport.ETA
            transportName.text = PublicTransport.transportName
            estimatedTime.text = PublicTransport.estimatedTime


        }
    }


    override fun getItemCount() = transportList.size

    fun updateTransports(newTransports: List<PublicTransport>) {
        transportList = newTransports
        notifyDataSetChanged()
    }
}

data class PublicTransport(
    val transport: String,
    val ETA: String,
    val transportName: String, // Ideally this should be a numeric type, formatted as a string for display purposes
    val estimatedTime: String // URL to the product image
)
