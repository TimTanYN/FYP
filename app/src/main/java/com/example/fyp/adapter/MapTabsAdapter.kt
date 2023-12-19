package com.example.fyp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.activity.PublicTransport
import com.example.fyp.activity.RestaurantFragment
import com.example.fyp.activity.Trip_map

class MapTabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Trip_map()
            1 -> PublicTransport()
            2 -> RestaurantFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }



}