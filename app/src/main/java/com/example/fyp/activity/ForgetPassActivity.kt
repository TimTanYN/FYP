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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
        val newPassword = generateRandomPassword(12) // Generate a 12-character password

        // Check if the email exists in the Realtime Database under "Users"
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Email exists in the "Users" node, reset password and send email
                        for (userSnapshot in snapshot.children) {
                            val userId = userSnapshot.key
                            if (userId != null) {
                                auth.fetchSignInMethodsForEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val signInMethods = task.result?.signInMethods
                                            showToast(signInMethods.toString())
                                            if (signInMethods != null && signInMethods.isNotEmpty()) {
                                                // Email is registered, reset password
                                                auth.currentUser?.updatePassword(newPassword)
                                                    ?.addOnCompleteListener { resetTask ->
                                                        if (resetTask.isSuccessful) {
                                                            val username = getUsernameForCurrentUser()
                                                            if (username != null) {
                                                                // Store the new password in the Firebase Realtime Database
                                                                databaseReference.child(userId)
                                                                    .child("password")
                                                                    .setValue(newPassword)
                                                                sendEmailWithNewPassword(email, newPassword, username)
                                                            } else {
                                                                showToast("Failed to retrieve username")
                                                            }
                                                        } else {
                                                            showToast("Failed to reset password")
                                                        }
                                                    }
                                            } else {
                                                // Email not registered
                                                showToast("Invalid email")
                                            }
                                        } else {
                                            // Other errors
                                            showToast("Please try again")
                                        }
                                    }
                            }
                        }
                    } else {
                        // Email not found in the Realtime Database
                        showToast("Invalid email")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error
                }
            })
    }


    private fun generateRandomPassword(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789!@#$%^&*()"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun sendEmailWithNewPassword(email: String, newPassword: String, username:String) {
        val senderEmail = "roommatesystem2023@gmail.com"
        val senderPassword = "yomx pdzv pllj vogr" // Be cautious with hardcoding credentials

        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(email))
                subject = "RoomMate Password Reset"
                val emailContent = """
                <html>
                    <body>
                        <p>Dear $username,</p>
                        <p>Your new password for RoomMate is: <b>$newPassword</b></p>
                        <p>Please change your password after logging in.</p>
                        <p>Thank you,<br/><b>RoomMate Management</b></p>
                    </body>
                </html>
            """.trimIndent()
                setContent(emailContent, "text/html; charset=utf-8")
            }

            Thread {
                Transport.send(message)
            }.start()
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun getUsernameForCurrentUser(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.displayName
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