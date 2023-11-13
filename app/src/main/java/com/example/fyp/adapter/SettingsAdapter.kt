package com.example.fyp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.example.fyp.R

class SettingsAdapter(context: Context, settingsList: List<SettingsItem>) :
    ArrayAdapter<SettingsItem>(context, 0, settingsList) {

    private class ViewHolder {
        var titleTextView: TextView? = null
        var switchView: Switch? = null
        var arrowView: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false)
            holder = ViewHolder()
            holder.titleTextView = view.findViewById(R.id.titleTextView)
            holder.switchView = view.findViewById(R.id.switchView)
            holder.arrowView = view.findViewById(R.id.arrowView)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        holder.titleTextView?.text = item?.title
        if (item?.hasSwitch == true) {
            holder.switchView?.visibility = View.VISIBLE
            holder.arrowView?.visibility = View.GONE
        } else {
            holder.switchView?.visibility = View.GONE
            holder.arrowView?.visibility = View.VISIBLE
        }

        return view
    }
}