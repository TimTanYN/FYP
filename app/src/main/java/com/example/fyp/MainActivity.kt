package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fyp.activity.AccommodationJobListActivity
import com.example.fyp.activity.AccommodationListActivity
import com.example.fyp.activity.AccountActivity
import com.example.fyp.activity.AccountAgentActivity
import com.example.fyp.activity.AccountOwnerActivity
import com.example.fyp.activity.AddAccommodationActivity
import com.example.fyp.activity.AddCardActivity
import com.example.fyp.activity.ApproveAccountActivity
import com.example.fyp.activity.BookingListActivity
import com.example.fyp.activity.BookingListAgentActivity
import com.example.fyp.activity.ChangePasswordActivity
import com.example.fyp.activity.CommissionListActivity
import com.example.fyp.activity.DashActivity
import com.example.fyp.activity.EditProfileActivity
import com.example.fyp.activity.ForgetPassActivity
import com.example.fyp.activity.ManageAccommodationActivity
import com.example.fyp.activity.SettingOwnerActivity
import com.example.fyp.activity.SignInActivity
import com.example.fyp.activity.SignUpActivity
import com.google.android.gms.maps.model.Dash

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
   

        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
    }
}

