package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fyp.R
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.BookingDetailsAgentActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingAgentAdapter(private val context: Context, private var bookings: List<Bookings>) : ArrayAdapter<Bookings>(context, 0, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_booking_agent, parent, false)

        val currentBooking = bookings[position]

        // Set TextViews
        val tvCheckInDate = listItemView.findViewById<TextView>(R.id.tvCheckInDate)
        val tvAgreement = listItemView.findViewById<TextView>(R.id.tvAgreement)
        val tvTenantName = listItemView.findViewById<TextView>(R.id.tvTenantName)
        val tvCommission = listItemView.findViewById<TextView>(R.id.tvCommission)

        tvCheckInDate.text = "${currentBooking.checkIn} want check in"
        calculateCommission(currentBooking.accomID) { commission ->
            tvCommission.text = "RM $commission commission"
        }
        fetchAgreement(currentBooking.accomID) { agreement ->
            tvAgreement.text = "$agreement contract"
        }
        fetchTenantName(currentBooking.userId) { userName ->
            tvTenantName.text = "Applied by $userName"
        }

        // Set Buttons
        val btnView = listItemView.findViewById<Button>(R.id.btnView)
        val btnApprove = listItemView.findViewById<Button>(R.id.btnApprove)
        val btnReject = listItemView.findViewById<Button>(R.id.btnReject)

        // Set visibility based on status
        if (currentBooking.status == "Pending") {
            btnApprove.visibility = View.VISIBLE
            btnReject.visibility = View.VISIBLE
        } else {
            btnApprove.visibility = View.GONE
            btnReject.visibility = View.GONE
        }

        // Set onClickListeners for buttons
        btnView.setOnClickListener {
            val intent = Intent(context, BookingDetailsAgentActivity::class.java)
            intent.putExtra("ACCOM_ID", currentBooking?.accomID)
            context.startActivity(intent)
        }

        btnApprove.setOnClickListener {
            showApproveDialog(currentBooking.accomID)
        }

        btnReject.setOnClickListener {
            showRejectDialog(currentBooking.accomID)
        }

        return listItemView
    }

    private fun calculateCommission(accomID: String, callback: (String) -> Unit) {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    val rentFee = accommodation?.rentFee?.toDoubleOrNull() ?: 0.0
                    val rate = accommodation?.rate?.toDoubleOrNull() ?: 0.0
                    val commission = rentFee * rate
                    callback(String.format("%.2f", commission))
                } else {
                    callback("0.00")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback("Error")
            }
        })
    }

    private fun fetchAgreement(accomID: String, callback: (String) -> Unit) {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    val agreement = accommodation?.agreement ?: "N/A"
                    callback(agreement)
                } else {
                    callback("N/A")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback("Error")
            }
        })
    }

    private fun fetchTenantName(userId: String, callback: (String) -> Unit) {
        if (userId == "null") {
            callback("Unknown")
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun showApproveDialog(accomID: String) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Approval")
            .setMessage("Are you sure you want to approve this booking?")
            .setPositiveButton("Approve") { _, _ ->
                updateBookingStatus(accomID, "Approved")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRejectDialog(accomID: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_reject_reason, null)
        val etRejectReason = dialogView.findViewById<EditText>(R.id.etRejectReason)

        // Regular expression to allow letters, digits, spaces, commas, and periods
        val allowedCharactersRegex = "[a-zA-Z0-9,.\\s]+"

        // Input filter that uses the regular expression
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex(allowedCharactersRegex))) {
                null // Accept the original input
            } else {
                "" // Reject the input
            }
        }

        // Apply the input filter to the EditText
        etRejectReason.filters = arrayOf(inputFilter)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Submit", null) // We set the listener later to prevent auto-dismissal
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val reason = etRejectReason.text.toString().trim()
                if (reason.isEmpty()) {
                    etRejectReason.error = "Reason cannot be empty"
                } else {
                    updateBookingWithReason(accomID, reason)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }


    // Update booking status in Firebase
    private fun updateBookingStatus(accomID: String, newStatus: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.accomID == accomID) {
                                bookingSnapshot.ref.child("status").setValue(newStatus)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Booking Approved", Toast.LENGTH_SHORT).show()
                                        refreshBookings()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to approve booking", Toast.LENGTH_SHORT).show()
                                    }
                                break
                            }
                        }
                    } else {
                        Toast.makeText(context, "Booking not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // Update booking with rejection reason in Firebase
    private fun updateBookingWithReason(accomID: String, reason: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.accomID == accomID) {
                                bookingSnapshot.ref.child("reason").setValue(reason)
                                bookingSnapshot.ref.child("status").setValue("Rejected")
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Booking Rejected", Toast.LENGTH_SHORT).show()
                                        refreshBookings()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to reject booking", Toast.LENGTH_SHORT).show()
                                    }
                                break
                            }
                        }
                    } else {
                        Toast.makeText(context, "Booking not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun refreshBookings() {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedBookings = mutableListOf<Bookings>()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Bookings::class.java)
                    booking?.let { updatedBookings.add(it) }
                }
                this@BookingAgentAdapter.bookings = updatedBookings
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
