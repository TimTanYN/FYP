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
import com.example.fyp.activity.BookingHistoryDetailsActivity
import com.example.fyp.activity.BookingHistoryListActivity
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

class BookingHistoryAdapter(private val context: Context, private val bookings: List<Bookings>) : ArrayAdapter<Bookings>(context, 0, bookings) {

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
            tvStatus.text = "${currentBooking.status}"
        }

        // Set onClickListeners for buttons
        btnView.setOnClickListener {
            val intent = Intent(context, BookingHistoryDetailsActivity::class.java)
            intent.putExtra("ACCOM_ID", currentBooking?.accomID)
            context.startActivity(intent)
        }

        return listItemView
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
