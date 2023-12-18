package com.example.fyp.database

data class Bookings(
    val accomID:String = "",
    val userId:String = "",
    val agentId:String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val status: String = "",
    val total: String = "",
    val reason: String = ""
)
