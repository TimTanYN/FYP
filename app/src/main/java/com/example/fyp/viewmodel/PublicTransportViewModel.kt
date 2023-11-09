package com.example.fyp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp.adapter.PublicTransport

class PublicTransportViewModel : ViewModel() {

    private val transitDetails = MutableLiveData<List<PublicTransport>>()

    fun setTransitDetails(details: List<PublicTransport>) {
        transitDetails.value = details
    }

    fun getTransitDetails(): LiveData<List<PublicTransport>> = transitDetails
}