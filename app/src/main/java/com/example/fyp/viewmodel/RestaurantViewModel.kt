package com.example.fyp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fyp.adapter.Restaurant


class RestaurantViewModel : ViewModel() {

    private val restaurants = MutableLiveData<List<Restaurant>>()

    fun setRestaurants(list: List<Restaurant>) {
        restaurants.value = list
    }

    fun getRestaurants(): LiveData<List<Restaurant>> = restaurants

}
