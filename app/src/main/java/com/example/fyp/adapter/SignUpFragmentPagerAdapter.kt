package com.example.fyp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.fyp.fragment.AgentSignUpFragment
import com.example.fyp.fragment.OwnerSignUpFragment
import com.example.fyp.fragment.UserSignUpFragment

class SignUpFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UserSignUpFragment()
            1 -> AgentSignUpFragment()
            2 -> OwnerSignUpFragment()
            else -> UserSignUpFragment()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "User"
            1 -> "Agent"
            2 -> "Owner"
            else -> "User"
        }
    }
}
