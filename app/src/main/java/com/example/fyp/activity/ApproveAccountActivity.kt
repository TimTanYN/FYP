package com.example.fyp.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.UserAdapter
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApproveAccountActivity : AppCompatActivity() {

    private lateinit var accountListView: ListView
    private lateinit var noAccountListView: TextView
    private lateinit var headerLayout: LinearLayout
    private lateinit var adapter: UserAdapter
    private var usersList = mutableListOf<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve_account)
        var checkButton = findViewById<Button>(R.id.checkButton)
        accountListView = findViewById(R.id.accountListView)
        noAccountListView = findViewById(R.id.noAccountListView)
        headerLayout = findViewById(R.id.headerLayout)

        setupToolbar()

        fetchUsers()
        checkButton.setOnClickListener {
            val url = "https://search.lppeh.gov.my/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    private fun fetchUsers() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    if (user != null && user.valid == "no") {
                        usersList.add(user)
                    }
                }
                if (usersList.isEmpty()) {
                    noAccountListView.visibility = View.VISIBLE
                    accountListView.visibility = View.GONE
                    headerLayout.visibility = View.GONE

                } else {
                    adapter = UserAdapter(this@ApproveAccountActivity, usersList)
                    accountListView.adapter = adapter
                    headerLayout.visibility = View.VISIBLE
                    accountListView.visibility = View.VISIBLE
                    noAccountListView.visibility = View.GONE

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashActivity::class.java)
            startActivity(intent)
        }
    }
}