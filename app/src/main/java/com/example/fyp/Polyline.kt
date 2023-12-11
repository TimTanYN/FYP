package com.example.fyp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp.adapter.PublicTransport
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil

class Polyline:AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.polyline)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val transport = intent.getSerializableExtra("Transport_DETAILS") as? PublicTransport
        mMap = googleMap

        // Add a polyline
        val decodedPath: List<LatLng> = PolyUtil.decode(transport?.polyline ?: "")

// Add the decoded path to the map
        val polylineOptions = PolylineOptions().addAll(decodedPath)
        mMap.addPolyline(polylineOptions
            .width(10f) // Width of the polyline
            .color(Color.BLUE) // Color of the polyline
            .geodesic(true))// Whether the polyline should follow the curve of the Earth)
        if (decodedPath.isNotEmpty()) {
            val centerPoint = decodedPath[decodedPath.size / 2]
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 10f))
        }
    }
}