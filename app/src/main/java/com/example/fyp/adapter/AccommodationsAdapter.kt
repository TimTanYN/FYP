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
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.AccommodationDetailsOwnerActivity
import com.example.fyp.activity.DeleteCardActivity
import com.example.fyp.activity.DeleteCardAgentActivity
import com.example.fyp.activity.DeleteCardOwnerActivity
import com.example.fyp.activity.EditAccommodationActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccommodationsAdapter(private val context: Context, private val accommodations: List<Accommodations>) : ArrayAdapter<Accommodations>(context, 0, accommodations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.accommodation_item, parent, false)

        val currentAccommodation = accommodations[position]

        val nameTextView = listItemView.findViewById<TextView>(R.id.accommodationName)
        nameTextView.text = "Accommodation Name: ${currentAccommodation.accomName}"

        val rentFeeTextView = listItemView.findViewById<TextView>(R.id.rentFee)
        rentFeeTextView.text = "Rent Fee: RM ${currentAccommodation.rentFee}"

        val contractTextView = listItemView.findViewById<TextView>(R.id.contractAgreement)
        contractTextView.text = "Contract Agreement: ${currentAccommodation.agreement}"

        // Fetch and set the agent's name
        val agentNameTextView = listItemView.findViewById<TextView>(R.id.agentName)
        fetchAgentName(currentAccommodation.agentId) { agentName ->
            agentNameTextView.text = "Agent Name: $agentName"
        }

        // Set up the buttons
        val viewButton = listItemView.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            val intent = Intent(context, AccommodationDetailsOwnerActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        val editButton = listItemView.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            val intent = Intent(context, EditAccommodationActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        val deleteButton = listItemView.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            // Implement delete action
        }

        return listItemView
    }

    private fun fetchAgentName(agentId: String, callback: (String) -> Unit) {
        if (agentId == "null") {
            callback("Don't have agent apply")
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.child(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    callback(user?.fullName ?: "Unknown")
                } else {
                    callback("Unknown")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("Error fetching data")
            }
        })
    }
}

