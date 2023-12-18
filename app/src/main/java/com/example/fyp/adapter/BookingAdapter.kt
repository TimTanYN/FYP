package com.example.fyp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fyp.R
import com.example.fyp.activity.AccommodationDetailsActivity
import com.example.fyp.activity.AddCardActivity
import com.example.fyp.activity.BookingDetailsActivity
import com.example.fyp.activity.BookingDetailsAgentActivity
import com.example.fyp.activity.BookingHistoryListActivity
import com.example.fyp.activity.BookingListActivity
import com.example.fyp.activity.SettingActivity
import com.example.fyp.database.Accommodations
import com.example.fyp.database.Bookings
import com.example.fyp.database.Cards
import com.example.fyp.database.Payments
import com.example.fyp.database.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter(private val context: Context, private val bookings: List<Bookings>) : ArrayAdapter<Bookings>(context, 0, bookings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_booking, parent, false)

        val currentBooking = bookings[position]

        // Set TextViews
        val tvCheckInDate = listItemView.findViewById<TextView>(R.id.tvCheckInDate)
        tvCheckInDate.text = "${currentBooking.checkIn} want check in"

        val tvCheckOutDate = listItemView.findViewById<TextView>(R.id.tvCheckOutDate)
        tvCheckOutDate.text = "${currentBooking.checkOut} want check out"

        val tvAgreement = listItemView.findViewById<TextView>(R.id.tvAgreement)
        fetchAgreement(currentBooking.accomID) { agreement ->
            tvAgreement.text = "$agreement contract"
        }

        val tvRentFee = listItemView.findViewById<TextView>(R.id.tvRentFee)
        fetchRentFee(currentBooking.accomID) { rentFee ->
            tvRentFee.text = "RM $rentFee per month"
        }


        val tvAgentName = listItemView.findViewById<TextView>(R.id.tvAgentName)

        // Load tenant name from Users
        fetchAgentName(currentBooking.agentId) { agentName ->
            tvAgentName.text = "Handle by $agentName"
        }


        val tvStatus = listItemView.findViewById<TextView>(R.id.tvStatus)
        // Set Buttons
        val btnView = listItemView.findViewById<Button>(R.id.btnView)
        val btnCancel = listItemView.findViewById<Button>(R.id.btnCancel)
        val btnPay = listItemView.findViewById<Button>(R.id.btnPay)

        // Set visibility based on status
        if (currentBooking.status == "Approved") {
            btnCancel.visibility = View.GONE
            btnPay.visibility = View.VISIBLE
            tvStatus.text = "${currentBooking.status} by agent"
        } else if (currentBooking.status == "Rejected"){
            btnCancel.visibility = View.GONE
            tvStatus.text = "${currentBooking.status} by agent"
        } else if (currentBooking.status == "Pending"){
            btnCancel.visibility = View.VISIBLE
            tvStatus.text = "${currentBooking.status} by agent"
        }else if (currentBooking.status == "Paid"){
            btnCancel.visibility = View.GONE
            btnPay.visibility = View.GONE
            tvStatus.text = "${currentBooking.status} by agent"

        }

        // Set onClickListeners for buttons
        btnView.setOnClickListener {
            val intent = Intent(context, BookingDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", currentBooking?.accomID)
            context.startActivity(intent)
        }

        btnCancel.setOnClickListener {
            showCancelConfirmationDialog(currentBooking)
        }

        btnPay.setOnClickListener {
            val currentBooking = bookings[position]
            checkUserCardAndPay(currentBooking)
        }

        return listItemView
    }

    private fun showCancelConfirmationDialog(booking: Bookings) {
        AlertDialog.Builder(context)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes") { _, _ ->
                cancelBooking(booking)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelBooking(booking: Bookings) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(booking.accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val currentBooking = bookingSnapshot.getValue(Bookings::class.java)
                            if (currentBooking != null && currentBooking.userId == booking.userId) {
                                bookingSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, BookingListActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show()
                                    }
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkUserCardAndPay(booking: Bookings) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("Cards")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val card = documents.documents.first().toObject(Cards::class.java)
                    card?.let {
                        showPaymentConfirmationDialog(it.cardNumber, booking)
                    }
                } else {
                    val intent = Intent(context, AddCardActivity::class.java)
                    context.startActivity(intent)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun showPaymentConfirmationDialog(cardNumber: String, booking: Bookings) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Payment")
            .setMessage("Do you want to use card XXXX-XXXX-XXXX-${cardNumber.takeLast(4)} to pay?")
            .setPositiveButton("Yes") { _, _ ->
                processPayment(cardNumber, booking)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun processPayment(cardNumber: String, booking: Bookings) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        // Fetch Accommodation details
        fetchAccommodationDetails(booking.accomID) { accommodation ->
            // Fetch agent's and owner's card details
            fetchCardDetails(booking.agentId) { agentCard ->
                fetchCardDetails(accommodation.ownerId) { ownerCard ->
                    // Create and store payment record
                    val payment = Payments(
                        accomID = booking.accomID,
                        tenantId = auth.currentUser?.uid ?: "",
                        agentId = booking.agentId,
                        deposit = calculateDeposit(accommodation.rentFee),
                        commission = calculateCommission(accommodation.rentFee, accommodation.rate),
                        rentMonth = accommodation.rentFee,
                        total = booking.total,
                        tenantCard = cardNumber,
                        agentCard = agentCard,
                        ownerCard = ownerCard
                    )

                    firestore.collection("Payments")
                        .add(payment)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Payment Success", Toast.LENGTH_SHORT).show()
                            updateBookingStatus(booking.accomID, auth.currentUser?.uid ?: "", "Paid")
                            updateOtherBookingsStatus(booking.accomID, auth.currentUser?.uid ?: "")
                            val intent = Intent(context, SettingActivity::class.java)
                            context.startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun updateBookingStatus(accomID: String, userId: String, newStatus: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.userId == userId) {
                                bookingSnapshot.ref.child("status").setValue(newStatus)
                                    .addOnSuccessListener {
                                    }
                                    .addOnFailureListener {
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun updateOtherBookingsStatus(accomID: String, currentUserId: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("Bookings")
        bookingRef.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Bookings::class.java)
                            if (booking != null && booking.userId != currentUserId) {
                                // Update status to "Rejected" for other bookings
                                bookingSnapshot.ref.child("status").setValue("Rejected")
                                bookingSnapshot.ref.child("reason").setValue("It has been sold")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    private fun fetchAccommodationDetails(accomID: String, callback: (Accommodations) -> Unit) {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    if (accommodation != null) {
                        callback(accommodation)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun calculateDeposit(rentFee: String): String {
        // Assuming deposit is twice the rent fee
        return (rentFee.toDouble() * 2).toString()
    }

    private fun calculateCommission(rentFee: String, rate: String): String {
        // Assuming commission is calculated based on the rate
        return (rentFee.toDouble() * rate.toDouble()).toString()
    }

    private fun fetchCardDetails(userId: String, callback: (String) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Cards")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val card = documents.documents.first().toObject(Cards::class.java)
                    callback(card?.cardNumber ?: "")
                } else {
                    callback("")
                }
            }
            .addOnFailureListener {
                callback("")
            }
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

    private fun fetchRentFee(accomID: String, callback: (String) -> Unit) {
        val accommodationsRef = FirebaseDatabase.getInstance().getReference("Accommodations")
        accommodationsRef.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    val rentFee = accommodation?.rentFee ?: "N/A"
                    callback(rentFee)
                } else {
                    callback("N/A")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback("Error")
            }
        })
    }

    private fun fetchAgentName(userId: String, callback: (String) -> Unit) {
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
}
