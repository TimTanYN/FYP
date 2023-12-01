package com.example.fyp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordEditTextInputLayout: TextInputLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signInAccountButton: Button = findViewById(R.id.signInAccountButton)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        val signUp: TextView = findViewById(R.id.signUpText)
        val forgetPass: TextView = findViewById(R.id.forgetPass)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordEditTextInputLayout = findViewById(R.id.passwordInputLayout)

        setupEmailField(emailEditText)
        setupPasswordField(passwordEditText)

        signInAccountButton.setOnClickListener {
            hideKeyboard(it) // Hide the keyboard when the sign-in button is clicked
            if (validateInputs()) {
                // Initialize Firebase Auth
                auth = FirebaseAuth.getInstance()
                signInUser()
            }
        }

        signUp.setOnClickListener {
            hideKeyboard(it)
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        forgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPassActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    showToast("Sign In Success")

                    // Get the current user's ID
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        fetchNewUserAttribute(it)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    showToast("Sign In Failure")
                }
            }
    }

    private fun fetchNewUserAttribute(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val newUser = dataSnapshot.child("newUser").getValue(String::class.java)
                val userRole = dataSnapshot.child("userRole").getValue(String::class.java)
//                newUser?.let {
//                    if (newUser == "yes" && userRole == "User"){
//                        val intent = Intent(this, EditProfileActivity::class.java)
//                        startActivity(intent)
//                    }else{
//                        val intent = Intent(this, AccountActivity::class.java)
//                        startActivity(intent)
//                    }
//
//                    if (newUser == "yes" && userRole == "Agent"){
//                        val intent = Intent(this, EditProfileAgentActivity::class.java)
//                        startActivity(intent)
//                    }else{
//                        val intent = Intent(this, AccountAgentActivity::class.java)
//                        startActivity(intent)
//                    }
//
//                    if (newUser == "yes" && userRole == "User"){
//                        val intent = Intent(this, EditProfileOwnerActivity::class.java)
//                        startActivity(intent)
//                    }else{
//                        val intent = Intent(this, AccountOwnerActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
                if (newUser == "yes") {
                    when (userRole) {
                        "User" -> startActivity(Intent(this, EditProfileActivity::class.java))
                        "Agent" -> startActivity(Intent(this, EditProfileAgentActivity::class.java))
                        "Owner" -> startActivity(Intent(this, EditProfileOwnerActivity::class.java))
                    }
                } else {
                    when (userRole) {
                        "User" -> startActivity(Intent(this, AccountActivity::class.java))
                        "Agent" -> startActivity(Intent(this, AccountAgentActivity::class.java))
                        "Owner" -> startActivity(Intent(this, AccountOwnerActivity::class.java))
                    }
                }
            }
        }.addOnFailureListener {
            showToast("Failed to fetch user data")
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Email Validation
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            emailInputLayout.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Invalid email format"
            isValid = false
        } else if (email.length > 90) {
            emailInputLayout.error = "Email should not exceed 90 characters"
            isValid = false
        } else {
            emailInputLayout.isErrorEnabled = false
        }

        // Password Validation
        val password = passwordEditText.text.toString().trim()
        if (password.isEmpty()) {
            passwordEditTextInputLayout.error = "Password cannot be empty"
            isValid = false
        } else if (password.length > 30) {
            passwordEditTextInputLayout.error = "Password should not exceed 30 characters"
            isValid = false
        } else {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,!*?])(?=\\S+$).{10,}$"
            if (!password.matches(passwordPattern.toRegex())) {
                passwordEditTextInputLayout.error = "Password must be more than 10 characters and include uppercase, lowercase, numbers, and symbols"
                isValid = false
            } else {
                passwordEditTextInputLayout.isErrorEnabled = false
            }
        }

        return isValid
    }

    private fun setupEmailField(emailEditText: EditText) {
        // Set initial hint
        emailInputLayout.hint = "Please enter your email address"

        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailInputLayout.hint = ""
            } else {
                if (emailEditText.text.toString().isEmpty()) {
                    emailInputLayout.hint = "Please enter your email address"
                }
            }
        }
    }

    private fun setupPasswordField(passwordEditText: EditText) {
        // Set initial hints
        passwordEditTextInputLayout.hint = "Please enter your password"

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordEditTextInputLayout.hint = ""
            } else {
                if (passwordEditText.text.toString().isEmpty()) {
                    passwordEditTextInputLayout.hint = "Please enter your password"
                }
            }
        }

        // Set an OnEditorActionListener to handle the "Done" action on the keyboard
        passwordEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // User has pressed Done on the keyboard
                hideKeyboard(v) // Call a method to hide the keyboard
                true // Consume the action
            } else {
                false // Do not consume the action
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
