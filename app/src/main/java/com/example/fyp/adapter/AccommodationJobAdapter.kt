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
import com.example.fyp.activity.AccommodationJobDetailsActivity
import com.example.fyp.database.Accommodations

class AccommodationJobAdapter(private val context: Context, private val accommodations: List<Accommodations>) : ArrayAdapter<Accommodations>(context, 0, accommodations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.job_item, parent, false)

        val currentAccommodation = accommodations[position]

        val nameTextView = listItemView.findViewById<TextView>(R.id.accommodationName)
        nameTextView.text = "Accommodation Name: ${currentAccommodation.accomName}"

        val commissionTextView = listItemView.findViewById<TextView>(R.id.commission)
        val commission = calculateCommission(currentAccommodation.rentFee, currentAccommodation.agreement)
        commissionTextView.text = "Commission: $commission"

        val stateTextView = listItemView.findViewById<TextView>(R.id.accommodationState)
        stateTextView.text = "State: ${currentAccommodation.state}"

        val cityTextView = listItemView.findViewById<TextView>(R.id.accommodationCity)
        cityTextView.text = "City: ${currentAccommodation.city}"

        val viewButton = listItemView.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            val intent = Intent(context, AccommodationJobDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        return listItemView
    }

    private fun calculateCommission(rentFee: String, year: String): String {
        val rentFeeValue = rentFee.toDoubleOrNull() ?: return "RM 0.00"
        val agreementYears = when (year) {
            "1 year" -> 1
            "2 years" -> 2
            "3 years" -> 3
            "4 years" -> 4
            "5 years" -> 5
            else -> return "RM 0.00"
        }

        val totalRent = rentFeeValue * agreementYears * 12
        val commissionPercentage = when (agreementYears) {
            in 1..2 -> 0.20
            in 3..4 -> 0.25
            else -> 0.28
        }

        val commission = totalRent * commissionPercentage
        return "RM ${String.format("%.2f", commission)}"
    }
}
