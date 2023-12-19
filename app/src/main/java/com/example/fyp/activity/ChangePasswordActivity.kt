package com.example.fyp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var originalPasswordInputLayout: TextInputLayout
    private lateinit var originalPassword:EditText
    private lateinit var newPasswordInputLayout: TextInputLayout
    private lateinit var newPassword:EditText
    private lateinit var reNewPasswordInputLayout: TextInputLayout
    private lateinit var reNewPassword:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        originalPasswordInputLayout = findViewById(R.id.originalPasswordInputLayout)
        originalPassword = findViewById(R.id.originalPassword)
        newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout)
        reNewPasswordInputLayout = findViewById(R.id.reNewPasswordInputLayout)
        newPassword = findViewById(R.id.newPassword)
        reNewPassword = findViewById(R.id.reNewPassword)


        val btnChange = findViewById<Button>(R.id.btnChange)

        setupToolbar()

        btnChange.setOnClickListener {
            hideKeyboard(it)

            if (validateInputs()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val email = currentUser?.email ?: return@setOnClickListener
                val originalPassword = originalPassword.text.toString().trim()
                val newPassword = newPassword.text.toString().trim()

                // Authenticate with original password
                val credential = EmailAuthProvider.getCredential(email, originalPassword)
                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Update password
                            currentUser.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        showToast("Password changed")
//                                        val intent = Intent(this, SettingActivity::class.java)
//                                        startActivity(intent)
                                        FirebaseAuth.getInstance().signOut() // Sign out
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    } else {
                                        showToast("Failed to change password")
                                    }
                                }
                        } else {
                            showToast("Invalid original password")
                        }
                    }
            }
        }

    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val originalPassword = originalPassword.text.toString().trim()
        val newPassword = newPassword.text.toString().trim()
        val reNewPassword = reNewPassword.text.toString().trim()

        // Password Validation
        if (originalPassword.isEmpty()) {
            originalPasswordInputLayout.error = "Original password field cannot be empty"
            isValid = false
        } else {
            originalPasswordInputLayout.isErrorEnabled = false

        }

        if (newPassword.isEmpty()) {
            newPasswordInputLayout.error = "New password field cannot be empty"
            isValid = false
        } else if (newPassword.length > 30) {
            newPasswordInputLayout.error = "New password should not exceed 30 characters"
            isValid = false
        } else if (newPassword.equals(originalPassword)) {
            newPasswordInputLayout.error = "New password should not match with original password"
            isValid = false
        } else {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,!*?])(?=\\S+$).{10,}$"
            if (!newPassword.matches(passwordPattern.toRegex())) {
                newPasswordInputLayout.error = "New password must be more than 10 characters and include uppercase, lowercase, numbers, and symbols"
                isValid = false
            } else {
                newPasswordInputLayout.isErrorEnabled = false
            }
        }

        if (reNewPassword.isEmpty()) {
            reNewPasswordInputLayout.error = "Re-enter password field cannot be empty"
            isValid = false
        } else if (!reNewPassword.equals(newPassword)) {
            reNewPasswordInputLayout.error = "Both passwords must match"
            isValid = false
        } else {
            reNewPasswordInputLayout.isErrorEnabled = false
        }
        return isValid
    }


    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
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