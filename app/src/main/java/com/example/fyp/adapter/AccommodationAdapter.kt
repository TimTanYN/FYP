package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.EditAccommodationActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class AccommodationAdapter(context: Context, private val accommodations: List<Accommodations>) : ArrayAdapter<Accommodations>(context, 0, accommodations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val view = convertView ?: layoutInflater.inflate(R.layout.list_accommodation, parent, false)

        val accommodation = getItem(position)
        val imageContainer = view.findViewById<LinearLayout>(R.id.imageContainer)

        imageContainer.removeAllViews()

        loadImagesForAccommodation(accommodation?.accomID ?: "", imageContainer)

        val nameTextView = view.findViewById<TextView>(R.id.accommodationName)
        val addressTextView = view.findViewById<TextView>(R.id.address)
        val rentFeeTextView = view.findViewById<TextView>(R.id.rentFee)
        val agentImageView = view.findViewById<CircleImageView>(R.id.agentImage)
        val agentName = view.findViewById<TextView>(R.id.agentName)

        nameTextView.text = accommodation?.accomName
        addressTextView.text = "${accommodation?.accomAddress1} ${accommodation?.accomAddress2}"
        rentFeeTextView.text = "RM ${String.format("%.2f", accommodation?.rentFee?.toDoubleOrNull() ?: 0.0)}"

        var agentEmail = ""
        var agentPhoneNumber = ""

        loadAgentImage(accommodation?.agentId ?: "", agentImageView, agentName) { user ->
            agentEmail = user.email
            agentPhoneNumber = user.phoneNumber
        }
//        loadAgentImage(accommodation?.agentId ?: "", agentImageView, agentName)

        val contactButton = view.findViewById<Button>(R.id.contactButton)
        contactButton.setOnClickListener {
            showContactOptions(agentEmail, agentPhoneNumber)
        }

        val viewButton = view.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            val intent = Intent(context, AccommodationDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", accommodation?.accomID)
            context.startActivity(intent)
        }

        return view
    }

    private fun loadImagesForAccommodation(accomID: String, imageContainer: LinearLayout) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("AccommodationImages")
        databaseReference.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (accommodationImageSnapshot in snapshot.children) {
                            val imageUrl = accommodationImageSnapshot.child("images").getValue(String::class.java)
                            imageUrl?.let { displayImage(Uri.parse(it), imageContainer) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun displayImage(uri: Uri, imageContainer: LinearLayout) {
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(400, 400) // Set your desired size
            scaleType = ImageView.ScaleType.FIT_XY
        }
        Glide.with(context).load(uri).into(imageView)
        imageContainer.addView(imageView)
    }

//    private fun loadAgentImage(agentId: String, agentImageView: CircleImageView, agentName: TextView) {
//        if (agentId != "null" && agentId.isNotEmpty()) {
//            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
//            usersRef.child(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val user = snapshot.getValue(Users::class.java)
//                        user?.imageLink?.let { imageUrl ->
//                            Glide.with(context).load(imageUrl).into(agentImageView)
//                        }
//                        agentName.text = user?.fullName
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle errors
//                }
//            })
//        }
//    }

    private fun loadAgentImage(agentId: String, agentImageView: CircleImageView, agentName: TextView, onAgentLoaded: (Users) -> Unit) {
        if (agentId != "null" && agentId.isNotEmpty()) {
            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
            usersRef.child(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Users::class.java)
                        user?.imageLink?.let { imageUrl ->
                            Glide.with(context).load(imageUrl).into(agentImageView)
                        }
                        agentName.text = user?.fullName
                        user?.let { onAgentLoaded(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }

    private fun showContactOptions(email: String, phoneNumber: String) {
        val options = arrayOf("Send email to agent", "Call agent")
        AlertDialog.Builder(context)
            .setTitle("Contact Agent")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sendEmail(email)
                    1 -> makePhoneCall(phoneNumber)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

}
