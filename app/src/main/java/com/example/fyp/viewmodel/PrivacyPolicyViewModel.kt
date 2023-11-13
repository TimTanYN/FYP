package com.example.fyp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.fyp.R
import com.example.fyp.adapter.ItemType
import com.example.fyp.adapter.PrivacyPolicyItem

class PrivacyPolicyViewModel : ViewModel() {
    fun getPrivacyPolicyItems(context: Context, link: String, contactInfo: String): List<PrivacyPolicyItem> {
        return listOf(
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_welcome), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_acceptance_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_acceptance_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_collection_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_collection_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_third_party_services_title), ItemType.TITLE),
            PrivacyPolicyItem(String.format(context.getString(R.string.privacy_policy_third_party_services_body), link), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_log_data_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_log_data_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_cookies_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_cookies_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_data_security_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_data_security_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_changes_title), ItemType.TITLE),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_changes_body), ItemType.BODY),
            PrivacyPolicyItem(context.getString(R.string.privacy_policy_contact_title), ItemType.TITLE),
            PrivacyPolicyItem(String.format(context.getString(R.string.privacy_policy_contact_body), contactInfo), ItemType.BODY)
        )
    }
}

