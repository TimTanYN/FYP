package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.Contract
import com.example.fyp.R



class ContractAdapter(private val productList: List<Product>, private val listener:OnItemClickedListener ) :
    RecyclerView.Adapter<ContractAdapter.ProductViewHolder>() {

    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.textProductName)
        val productDescription: TextView = view.findViewById(R.id.textProductDescription)
        val productPrice: TextView = view.findViewById(R.id.textProductPrice)
        val generateButton: Button = view.findViewById(R.id.generate) // Your button
    }
    interface OnItemClickedListener {
        fun onItemClicked(position: Int)
        fun onButtonClicked(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price


        holder.itemView.setOnClickListener {
            listener.onItemClicked(position)
        }

        holder.generateButton.setOnClickListener {
            listener.onButtonClicked(position)
        }
    }
    }






data class Product(
    val name: String,
    val description: String,
    val price: String
    // ... any other properties
)

