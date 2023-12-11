package com.example.fyp.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import java.io.Serializable

class PublicTransportAdapter(private var transportList: List<PublicTransport>, private val clickListener: OnTransportClickListener) : RecyclerView.Adapter<PublicTransportAdapter.ProductViewHolder>() {
    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val transport: TextView = view.findViewById(R.id.transport)
        val ETA: TextView = view.findViewById(R.id.ETA)
        val transportName: TextView = view.findViewById(R.id.transportName)
        val estimatedTime: TextView = view.findViewById(R.id.estimatedTime)
        var transportImage: ImageView = view.findViewById(R.id.transportImage)

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
            estimatedTime.text = PublicTransport.estimatedTime + " hr " + PublicTransport.minute + " min"
            if(transport.text == "SUBWAY"){
                transportImage.setImageResource(R.drawable.train)
            }else{
                transportImage.setImageResource(R.drawable.bus)
            }

        }

        holder.itemView.setOnClickListener {
            clickListener.onTransportClick(PublicTransport)
        }
    }


    override fun getItemCount() = transportList.size

    fun updateTransports(newTransports: List<PublicTransport>) {
        transportList = newTransports
        notifyDataSetChanged()
    }

    interface OnTransportClickListener {
        fun onTransportClick(PublicTransport: PublicTransport)
    }
}

data class PublicTransport(
    val transport: String,
    val ETA: String,
    val transportName: String,
    val estimatedTime: String,
    val minute:String,
    val departure:String,
    val arrival:String,
    val departureTime:String,
    val numberOfStops:String,
    val polyline:String
): Serializable
