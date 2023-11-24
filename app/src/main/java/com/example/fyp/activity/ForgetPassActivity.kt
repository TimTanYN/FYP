package com.example.fyp.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.fyp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgetPassActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var recoveryAccountButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pass)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.email)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        recoveryAccountButton = findViewById(R.id.recoveryAccountButton)
        val signIn: TextView = findViewById(R.id.signInText)

        setupEmailField(emailEditText)

        recoveryAccountButton.setOnClickListener {
            if (validateInputs()) {
                // Proceed with password recovery
                sendPasswordResetEmail()
            }
        }

        signIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
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

        return isValid
    }

    private fun setupEmailField(emailEditText: EditText) {
        // Set initial hint
        emailInputLayout.hint = "Please enter your email address for recovery"


        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailInputLayout.hint = ""
            } else {
                if (emailEditText.text.toString().isEmpty()) {
                    emailInputLayout.hint = "Please enter your email address for recovery"
                }
            }
        }
    }

    private fun sendPasswordResetEmail() {
        val email = emailEditText.text.toString().trim()

        // Attempt to sign in with a dummy password
        auth.signInWithEmailAndPassword(email, "dummyPassword")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // This case is use to check the email is exist in the firebase authentication
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        // Email not registered
                        showToast("Invalid email")
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // Email is registered, send reset email
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { resetTask ->
                                if (resetTask.isSuccessful) {
                                    showToast("Please check your email")
                                    openEmailClient() // Open the email client

                                } else {
                                    showToast("Please try again")
                                }
                            }
                    } else {
                        // Other errors
                        showToast("Please try again")
                    }
                }
            }
    }

    private fun openEmailClient() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("Email not installed")
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

//    private fun sendPasswordResetEmail() {
//        val email = emailEditText.text.toString().trim()
//
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
////                    Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show()
//                } else {
////                    Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
}