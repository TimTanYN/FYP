package com.example.fyp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R



class ContractAdapter(private val productList: List<Contracts>, private val listener:OnItemClickedListener ) :
    RecyclerView.Adapter<ContractAdapter.ProductViewHolder>() {

    class ProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.Name)
        val productDescription: TextView = view.findViewById(R.id.Description)
        val generateButton: Button = view.findViewById(R.id.generate) // Your button
    }
    interface OnItemClickedListener {
        fun onItemClicked(position: Int)
        fun onButtonClicked(position: Int,contracts: Contracts)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contract_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val Contract = productList[position]
        holder.productName.text = Contract.name
        holder.productDescription.text = Contract.description



        holder.itemView.setOnClickListener {
            listener.onItemClicked(position)
        }

        holder.generateButton.setOnClickListener {
            listener.onButtonClicked(position,Contract)
        }
    }
    }






data class Contracts(
    val name: String,
    val description: String,
    val id : String
)

