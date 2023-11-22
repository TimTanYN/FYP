package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fyp.R

class ContractCardAdapter (private val productList: List<ContractCard>) : RecyclerView.Adapter<ContractCardAdapter.ProductViewHolder>(){

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.contractName)
        val productDescription: TextView = view.findViewById(R.id.contractDescription)
        val productImage: ImageView = view.findViewById(R.id.contractImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contract_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        Glide.with(holder.productImage.context).load(product.imageUrl).into(holder.productImage)

        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = if (position % 2 == 0) 500 else 400 // Example uneven heights
        holder.itemView.layoutParams = layoutParams
        println(productList.size)
    }

    override fun getItemCount() = productList.size
}

data class ContractCard(
    val name: String,
    val description: String,
    val price: String,
    val imageUrl: Int
)