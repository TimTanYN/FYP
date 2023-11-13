package com.example.fyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.fyp.R

class PrivacyPolicyAdapter(context: Context, items: List<PrivacyPolicyItem>) : ArrayAdapter<PrivacyPolicyItem>(context, -1, items) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position) ?: return View(context) // Handle null value

        return when (item.type) {
            ItemType.TITLE -> {
                val view = inflater.inflate(R.layout.list_item_title, parent, false)
                val textView = view.findViewById<TextView>(R.id.textTitle)
                textView.text = item.text
                view
            }
            ItemType.BODY -> {
                val view = inflater.inflate(R.layout.list_item_body, parent, false)
                val textView = view.findViewById<TextView>(R.id.textBody)
                textView.text = item.text
                view
            }
        }
    }
}

