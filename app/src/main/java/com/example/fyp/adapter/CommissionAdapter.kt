package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.fyp.R
import com.example.fyp.database.Payments
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.CommissionDetailsActivity

class CommissionAdapter(context: Context, private val payments: List<Payments>) :
    ArrayAdapter<Payments>(context, 0, payments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.list_commission, parent, false)

        val currentPayment = payments[position]

        val tvCardNumber = listItemView.findViewById<TextView>(R.id.tvCardNumber)
        tvCardNumber.text = currentPayment.agentCard

        val tvCommission = listItemView.findViewById<TextView>(R.id.tvCommission)
        val formattedCommission = String.format("%.2f", currentPayment.commission.toDoubleOrNull() ?: 0.0)
        tvCommission.text = "RM $formattedCommission commission"


        val btnView = listItemView.findViewById<Button>(R.id.btnView)
        btnView.setOnClickListener {
            val intent = Intent(context, CommissionDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", currentPayment.accomID)
            context.startActivity(intent)
        }

        return listItemView
    }
}
