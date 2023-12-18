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
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.AccommodationDetailsOwnerActivity
import com.example.fyp.activity.BookingHistoryDetailsActivity
import com.example.fyp.activity.DeleteCardActivity
import com.example.fyp.activity.DeleteCardAgentActivity
import com.example.fyp.activity.DeleteCardOwnerActivity
import com.example.fyp.activity.EditAccommodationActivity
import com.example.fyp.activity.ManageAccommodationActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AccommodationsAdapter(private val context: Context, private val accommodations: List<Accommodations>) : ArrayAdapter<Accommodations>(context, 0, accommodations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.accommodation_item, parent, false)

        val currentAccommodation = accommodations[position]
        val imageContainer = listItemView.findViewById<LinearLayout>(R.id.imageContainer)

        imageContainer.removeAllViews()

        loadImagesForAccommodation(currentAccommodation?.accomID ?: "", imageContainer)


        val nameTextView = listItemView.findViewById<TextView>(R.id.accommodationName)
        nameTextView.text = "Accommodation Name: ${currentAccommodation.accomName}"

        val rentFeeTextView = listItemView.findViewById<TextView>(R.id.rentFee)
        rentFeeTextView.text = "Rent Fee per Month: RM ${currentAccommodation.rentFee}"

        val contractTextView = listItemView.findViewById<TextView>(R.id.contractAgreement)
        contractTextView.text = "Contract Agreement: ${currentAccommodation.agreement}"

        // Fetch and set the agent's name
        val agentNameTextView = listItemView.findViewById<TextView>(R.id.agentName)
        fetchAgentName(currentAccommodation.agentId) { agentName ->

            if(!currentAccommodation.agentId.equals("null")){
                agentNameTextView.text = "Agent Name: $agentName"
            }else{
                agentNameTextView.text = "$agentName"
            }
        }

        // Set up the buttons
        val viewButton = listItemView.findViewById<Button>(R.id.viewButton)
        viewButton.setOnClickListener {
            val intent = Intent(context, AccommodationDetailsOwnerActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        val editButton = listItemView.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            val intent = Intent(context, EditAccommodationActivity::class.java)
            intent.putExtra("ACCOM_ID", currentAccommodation.accomID)
            context.startActivity(intent)
        }

        val deleteButton = listItemView.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            checkAndDeleteAccommodation(currentAccommodation.accomID)
        }

        return listItemView
    }

    private fun checkAndDeleteAccommodation(accomID: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Payments")
            .whereEqualTo("accomID", accomID)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    showDeleteConfirmationDialog(accomID)
                } else {
                    Toast.makeText(context, "Accommodation had tenant rent", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
            }
    }

    private fun showDeleteConfirmationDialog(accomID: String) {
        AlertDialog.Builder(context)
            .setTitle("Delete Accommodation")
            .setMessage("Are you sure you want to delete this accommodation?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAccommodation(accomID)
            }
            .setNegativeButton("No", null)
            .show()
    }

//    private fun deleteAccommodation(accomID: String) {
//        val databaseRef = FirebaseDatabase.getInstance().getReference("Accommodations")
//        val imagesRef = FirebaseDatabase.getInstance().getReference("AccommodationImages")
//        val storageRef = FirebaseStorage.getInstance().getReference("accommodation_images/$accomID")
//
//        databaseRef.child(accomID).removeValue()
//            .addOnSuccessListener {
//                imagesRef.orderByChild("accomID").equalTo(accomID)
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for (imageSnapshot in snapshot.children) {
//                                imageSnapshot.ref.removeValue()
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Handle errors
//                        }
//                    })
//
//                storageRef.listAll()
//                    .addOnSuccessListener { listResult ->
//                        listResult.items.forEach { item ->
//                            item.delete()
//                        }
//                    }
//                    .addOnFailureListener {
//                        // Handle any errors
//                    }
//
//                Toast.makeText(context, "Accommodation deleted successfully", Toast.LENGTH_SHORT).show()
//                val intent = Intent(context, ManageAccommodationActivity::class.java)
//                context.startActivity(intent)
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to delete accommodation", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun deleteAccommodation(accomID: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        val imagesRef = FirebaseDatabase.getInstance().getReference("AccommodationImages")
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")
        val storageRef = FirebaseStorage.getInstance().getReference("accommodation_images/$accomID")

        // Delete Accommodation record
        databaseRef.child(accomID).removeValue()
            .addOnSuccessListener {
                // Delete Accommodation Images
                imagesRef.orderByChild("accomID").equalTo(accomID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (imageSnapshot in snapshot.children) {
                                imageSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors
                        }
                    })

                // Delete Accommodation Images from Storage
                storageRef.listAll()
                    .addOnSuccessListener { listResult ->
                        listResult.items.forEach { item ->
                            item.delete()
                        }
                    }
                    .addOnFailureListener {
                        // Handle any errors
                    }

                // Delete related Workers records
                workersRef.orderByChild("accomID").equalTo(accomID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (workerSnapshot in snapshot.children) {
                                workerSnapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors
                        }
                    })

                Toast.makeText(context, "Accommodation deleted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, ManageAccommodationActivity::class.java)
                context.startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete accommodation", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchAgentName(agentId: String, callback: (String) -> Unit) {
        if (agentId == "null") {
            callback("Don't have agent apply")
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.child(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    callback(user?.fullName ?: "Unknown")
                } else {
                    callback("Unknown")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("Error fetching data")
            }
        })
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
}

