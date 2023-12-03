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
import com.example.fyp.activity.DeleteCardActivity
import com.example.fyp.activity.DeleteCardAgentActivity
import com.example.fyp.activity.DeleteCardOwnerActivity
import com.example.fyp.activity.PrivacyActivity
import com.example.fyp.database.Cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CardAdapter(context: Context, private val cards: List<Cards>) : ArrayAdapter<Cards>(context, 0, cards) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.cards_item, parent, false)
        }

        val currentCard = getItem(position)

        val cardNumberTextView = listItemView!!.findViewById<TextView>(R.id.cardNumberTextView)
        cardNumberTextView.text = currentCard?.cardNumber

        // Optionally, handle the view button click event
        val viewButton = listItemView.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            determineUserRoleAndNavigate(currentCard)
        }

        return listItemView
    }

    private fun determineUserRoleAndNavigate(currentCard: Cards?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            databaseReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userRole = snapshot.child("userRole").getValue(String::class.java)
                        navigateBasedOnRole(userRole, currentCard)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun navigateBasedOnRole(userRole: String?, currentCard: Cards?) {
        val intent = when (userRole) {
            "User" -> Intent(context, DeleteCardActivity::class.java)
            "Agent" -> Intent(context, DeleteCardAgentActivity::class.java)
            "Owner" -> Intent(context, DeleteCardOwnerActivity::class.java)
            else -> return
        }
        intent.putExtra("CARD_NUMBER", currentCard?.cardNumber)
        context.startActivity(intent)
    }
}