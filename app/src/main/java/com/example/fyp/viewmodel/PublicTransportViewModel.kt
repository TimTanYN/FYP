package com.example.fyp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PublicTransportViewModel : ViewModel(){

    private val transitDetails = MutableLiveData<List<String>>()

    fun setTransitDetails(details: List<String>) {
        transitDetails.value = details
    }

    fun getTransitDetails(): LiveData<List<String>> = transitDetails
}