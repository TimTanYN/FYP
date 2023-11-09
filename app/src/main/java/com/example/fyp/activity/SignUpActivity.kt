package com.example.fyp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.fyp.R
import com.example.fyp.adapter.SignUpFragmentPagerAdapter
import com.google.android.material.tabs.TabLayout

class SignUpActivity : AppCompatActivity() {

    private var selectedRole: String = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Set up the ViewPager and TabLayout
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = SignUpFragmentPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        // Set the default tab to the User Sign Up Fragment
        viewPager.currentItem = 0

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedRole = tab.text.toString() // This will update the selectedRole with "User", "Agent", or "Owner"
                viewPager.currentItem = tab.position // This will switch the fragment when a tab is selected
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Implement behavior for when a tab is unselected if necessary
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselect if necessary
            }
        })

    }
}
