package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fyp.activity.AccountActivity
import com.example.fyp.activity.DashActivity
import com.example.fyp.activity.EditProfileActivity
import com.example.fyp.activity.ForgetPassActivity
import com.example.fyp.activity.SignInActivity
import com.example.fyp.activity.SignUpActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, SignInActivity::class.java)

        startActivity(intent)
    }
}

