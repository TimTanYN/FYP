package com.example.fyp

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class Trip:AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    var userData = 0
//    val db = FirebaseFirestore.getInstance()
    var city = "Kuala Lumpur"
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val kl = LatLng(3.1319, 101.6841)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kl, 10f))

        mMap.setOnMapLongClickListener { latLng ->
            if (startLatLng == null) {
                startLatLng = latLng
                placeMarkerAndRetrieveLocation(latLng, "Start Location")
            } else if (endLatLng == null) {
                endLatLng = latLng
                placeMarkerAndRetrieveLocation(latLng, "End Location")
                calculateRoute(startLatLng!!, endLatLng!!)
            } else {
                // Reset everything for new selection
                mMap.clear()
                startLatLng = null
                endLatLng = null
            }
        }
    }

    private fun calculateRoute(start: LatLng, end: LatLng) {
        val mode = "transit"  // or "transit"
        val apiKey = "AIzaSyAKrD9Kp41hhUHoWQiNqU8ns1k2lJhygpU"
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${start.latitude},${start.longitude}&destination=${end.latitude},${end.longitude}&mode=$mode&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val jsonResponse = JSONObject(it.string())
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val legs = route.getJSONArray("legs")
                        var totalDurationValue = 0  // To accumulate the total duration

                        val transitList = mutableListOf<String>()  // To accumulate transit details

                        for (i in 0 until legs.length()) {
                            val leg = legs.getJSONObject(i)
                            val steps = leg.getJSONArray("steps")
                            for (j in 0 until steps.length()) {
                                val step = steps.getJSONObject(j)
                                val travelMode = step.getString("travel_mode")
                                if (travelMode == "TRANSIT") {
                                    val transitDetails = step.getJSONObject("transit_details")
                                    val line = transitDetails.getJSONObject("line")
                                    val vehicle = line.getJSONObject("vehicle")
                                    val vehicleType = vehicle.getString("type")
                                    val vehicleName = vehicle.getString("name")
                                    val shortName = line.optString("short_name")

                                    transitList.add("Vehicle Type: $vehicleType, Vehicle Name: $vehicleName, Line: $shortName")
                                }
                            }
                            val duration = leg.getJSONObject("duration")
                            totalDurationValue += duration.getInt("value")

                        }
                        Log.d("Debug", "Total Duration in seconds: $totalDurationValue")

                        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
                        val initialTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(currentTime.time)
                        Log.d("InitialTime", "Initial Time: $initialTime")
                        currentTime.add(Calendar.SECOND, totalDurationValue)
                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

                        val eta = timeFormat.format(currentTime.time)
                        Log.d("ETA", "ETA: $eta")

                        for (transitDetail in transitList) {
                            Log.d("TransitDetails", transitDetail)
                        }

                        val polyline = route.getJSONObject("overview_polyline").getString("points")
                        val decodedPath = PolyUtil.decode(polyline)
                        runOnUiThread {
                            mMap.addPolyline(PolylineOptions().addAll(decodedPath))
                        }
                    }
                }
            }
        })
    }



    private fun placeMarkerAndRetrieveLocation(latLng: LatLng, title: String) {
        // Clear existing markers
//        mMap.clear()

        // Add marker on the chosen location with the given title
        currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(title))

        // Optionally: retrieve address from latLng
        fetchAddress(latLng)
    }

    private fun fetchAddress(latLng: LatLng) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    city = addresses[0].locality ?: return
                    Toast.makeText(this, "Selected city: $city", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Address not found!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error fetching address. Please try again.", Toast.LENGTH_SHORT)
                .show()
//            Toast.makeText(this, "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}", Toast.LENGTH_SHORT).show()

        }
    }



//    private fun searchLocation(location: String) {
//        val geocoder = Geocoder(this)
//        try {
//            val addressList = geocoder.getFromLocationName(location, 1)
//            if (addressList != null && addressList.isNotEmpty()) {
//                val address = addressList[0]
//                val latLng = LatLng(address.latitude, address.longitude)
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                placeMarkerAndRetrieveLocation(latLng)
//            } else {
//                Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error searching location. Please try again.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//
//    fun hideKeyboard(view: View) {
//        val inputMethodManager =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }


}


