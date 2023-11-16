package com.example.fyp.adapter

import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fyp.PublicTransport
import com.example.fyp.RestaurantFragment
import com.example.fyp.Trip_map

class MapTabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){


    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PublicTransport()
            1 -> Trip_map()
            2 -> RestaurantFragment()
            3 -> PublicTransport()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }



}