package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fyp.activity.AccountActivity
import com.example.fyp.activity.SignInActivity
import com.example.fyp.activity.SignUpActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            val intent = Intent(this, MapTabs::class.java)
        startActivity(intent)
    }
}

