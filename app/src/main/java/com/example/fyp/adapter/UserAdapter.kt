package com.example.fyp.adapter

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fyp.R
import com.example.fyp.database.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class UserAdapter(private val context: Context, private val users: List<Users>) : BaseAdapter() {

    override fun getCount(): Int = users.size

    override fun getItem(position: Int): Any = users[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_account, parent, false)

        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvAccountType = view.findViewById<TextView>(R.id.tvAccountType)
        val btnCopy = view.findViewById<Button>(R.id.btnCopy)
        val btnApprove = view.findViewById<Button>(R.id.btnApprove)

        val user = getItem(position) as Users

        tvEmail.text = user.email
        tvAccountType.text = user.userRole

        btnCopy.setOnClickListener {
            // Copy contact number after removing prefix
            val contactNumber = user.phoneNumber.removePrefix("+6")
            copyToClipboard(contactNumber)
        }

        btnApprove.setOnClickListener {
            // Update user's valid status in Firebase
            showConfirmationDialog(users[position])
        }

        return view
    }
    private fun showConfirmationDialog(user: Users) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Approval")
            .setMessage("Are you sure you want to approve this account?")
            .setPositiveButton("Approve") { dialog, which ->
                sendEmailDirectly(user.email, user.fullName)
                updateUserValidStatus(user.userId, "yes")

            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun getCurrentUserEmail(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.email
    }

    private fun sendEmailDirectly(receiverEmail: String, userName: String) {
        val senderEmail = getCurrentUserEmail() ?: return // Handle this case as needed

        val properties = Properties().apply {

            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "465")
            put("mail.smtp.auth", "true")
            put("mail.smtp.ssl.enable", "true")

        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                // Replace with the actual sender's email and the correct password or App Password
                return PasswordAuthentication(senderEmail, "yomx pdzv pllj vogr")
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))
                subject = "Account Approved"

                val emailContent = """
                <html>
                    <body>
                        <p>Dear $userName,</p>
                        <p>Welcome to join RoomMate! Your account has been approved. You can now log in with this account now.</p>
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

    private fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Contact copied", Toast.LENGTH_SHORT).show()
    }

    private fun updateUserValidStatus(userId: String, status: String) {
        // Firebase database reference to update the user's valid status
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.child(userId).child("valid").setValue(status)
            .addOnSuccessListener {
                Toast.makeText(context, "User approved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Fail to approve", Toast.LENGTH_SHORT).show()

            }
    }
}
