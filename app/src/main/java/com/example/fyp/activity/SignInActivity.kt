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
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.R
import com.google.android.material.textfield.TextInputLayout

class SignInActivity : AppCompatActivity() {
    private lateinit var passwordEditTextInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val email: EditText = findViewById(R.id.email)
        val signInAccountButton: Button = findViewById(R.id.signInAccountButton)
        val passwordEditText: EditText = findViewById(R.id.password)
        val signUp: TextView = findViewById(R.id.signUpText)
        val forgetPass: TextView = findViewById(R.id.forgetPass)

        passwordEditTextInputLayout = findViewById(R.id.passwordInputLayout)

        setupPasswordField(passwordEditText)

        signInAccountButton.setOnClickListener {
            hideKeyboard(it) // Hide the keyboard when the sign-in button is clicked
            // Insert your account creation logic here.
        }

        signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        forgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPassActivity::class.java)
            startActivity(intent)
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
}
