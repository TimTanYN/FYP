package com.example.fyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.fyp.R
import com.example.fyp.database.Cards

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

        }

        return listItemView
    }
}
