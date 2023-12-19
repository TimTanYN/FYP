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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.activity.AccommodationJobDetailsActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class AccommodationJobAdapter(private val context: Context, private val accommodations: List<Accommodations>) : ArrayAdapter<Accommodations>(context, 0, accommodations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.job_item, parent, false)

        val currentAccommodation = accommodations[position]
        val imageContainer = listItemView.findViewById<LinearLayout>(R.id.imageContainer)

        imageContainer.removeAllViews()

        loadImagesForAccommodation(currentAccommodation?.accomID ?: "", imageContainer)

        val accommodationName = listItemView.findViewById<TextView>(R.id.accommodationName)
        val commissionTextView = listItemView.findViewById<TextView>(R.id.commission)
        val commissionRateTextView = listItemView.findViewById<TextView>(R.id.commissionRate)
        val stateTextView = listItemView.findViewById<TextView>(R.id.accommodationState)
        val cityTextView = listItemView.findViewById<TextView>(R.id.accommodationCity)

        accommodationName.text = "Accommodation Name: ${currentAccommodation.accomName}"

        val commission = calculateCommission(currentAccommodation.rentFee, currentAccommodation.rate)
        commissionTextView.text = "Total Commission: $commission"

        commissionRateTextView.text = "Commission Rate: ${currentAccommodation.rate}%"

        stateTextView.text = "State: ${currentAccommodation.state}"

        cityTextView.text = "City: ${currentAccommodation.city}"

        val ownerImageView = listItemView.findViewById<CircleImageView>(R.id.ownerImage)
        val ownerName = listItemView.findViewById<TextView>(R.id.ownerName)



//        loadOwnerImage(currentAccommodation?.ownerId ?: "", ownerImageView, ownerName)


        val viewButton = listItemView.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            val intent = Intent(context, AccommodationJobDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        var ownerEmail = ""
        var ownerPhoneNumber = ""

        loadOwnerImage(currentAccommodation?.ownerId ?: "", ownerImageView, ownerName) { user ->
            ownerEmail = user.email
            ownerPhoneNumber = user.phoneNumber
        }

        val contactButton = listItemView.findViewById<Button>(R.id.contactButton)
        contactButton.setOnClickListener {
            showContactOptions(ownerEmail, ownerPhoneNumber)
        }

        return listItemView
    }

    private fun showContactOptions(email: String, phoneNumber: String) {
        val options = arrayOf("Send email to owner", "Call owner")
        AlertDialog.Builder(context)
            .setTitle("Contact Owner")
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

    private fun calculateCommission(rentFee: String, rate: String): String {

        val commission = rentFee.toDouble() * rate.toDouble()
        return "RM ${String.format("%.2f", commission)}"
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

    private fun loadOwnerImage(ownerId: String, ownerImageView: CircleImageView, ownerName: TextView, onOwnerLoaded: (Users) -> Unit) {
        if (ownerId != "null" && ownerId.isNotEmpty()) {
            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
            usersRef.child(ownerId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(Users::class.java)
                        user?.imageLink?.let { imageUrl ->
                            Glide.with(context).load(imageUrl).into(ownerImageView)
                        }
                        ownerName.text = user?.fullName
                        user?.let { onOwnerLoaded(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }
}
