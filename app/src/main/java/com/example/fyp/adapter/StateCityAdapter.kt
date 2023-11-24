package com.example.fyp.adapter

import android.content.Context
import android.widget.ArrayAdapter

class StateCityAdapter(context: Context) {
    private var states = listOf<String>()
    private var cities = listOf<String>()

    private val stateAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, states)
    private val cityAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, cities)

    init {
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    fun setStateList(stateList: List<String>) {
        states = stateList
        stateAdapter.clear()
        stateAdapter.addAll(stateList)
        stateAdapter.notifyDataSetChanged()
    }

    fun setCityList(cityList: List<String>) {
        cities = cityList
        cityAdapter.clear()
        cityAdapter.addAll(cityList)
        cityAdapter.notifyDataSetChanged()
    }

    fun getStateAdapter() = stateAdapter
    fun getCityAdapter() = cityAdapter
}