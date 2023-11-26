package com.example.fyp.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.fyp.R
import com.example.fyp.adapter.SignUpFragmentPagerAdapter
import com.example.fyp.fragment.UserSignUpFragment
import com.google.android.material.tabs.TabLayout

class SignUpActivity : AppCompatActivity() {

    private var selectedRole: String = "User"
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        viewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = SignUpFragmentPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 3 // Keep all tabs in memory

        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                hideKeyboard()
                viewPager.currentItem = tab.position
                resetFragmentView(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Optional: Implement behavior for when a tab is unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Optional: Handle tab reselect
            }
        })
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun resetFragmentView(position: Int) {
        val fragment = (viewPager.adapter as? SignUpFragmentPagerAdapter)?.getItem(position)
        fragment?.view?.requestLayout()
    }
}