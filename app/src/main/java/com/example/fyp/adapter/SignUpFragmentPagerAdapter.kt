package com.example.fyp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.fyp.fragment.AgentSignUpFragment
import com.example.fyp.fragment.OwnerSignUpFragment
import com.example.fyp.fragment.UserSignUpFragment

class SignUpFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = listOf(UserSignUpFragment(), AgentSignUpFragment(), OwnerSignUpFragment())

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "User"
            1 -> "Agent"
            2 -> "Owner"
            else -> "User"
        }
    }
}