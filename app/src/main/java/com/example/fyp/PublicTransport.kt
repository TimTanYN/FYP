package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fyp.viewmodel.PublicTransportViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PublicTransport.newInstance] factory method to
 * create an instance of this fragment.
 */
class PublicTransport : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var model: PublicTransportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        model = ViewModelProvider(requireActivity())[PublicTransportViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public_transport, container, false)
    }


//    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        listView = view.findViewById(R.id.list_view)
//        adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_list_item_1,
//            mutableListOf<String>() // Start with an empty list
//        )
//        listView.adapter = adapter

        val vehicleTypeTextView: TextView = view.findViewById(R.id.vehicleTypeTextView)
        model.getTransitDetails().observe(viewLifecycleOwner, Observer<List<String>> { details ->
            // Log to check if this observer is being called
            Log.d("TransitDetailLog", "Observer called with details: ${details.get(0)}")
            details.forEach { detail ->
                // Assuming the detail string is well formatted as specified
                val parts = detail.split(", ") // Split by comma and space to get each part
                val vehicleTypePart = parts.find { it.startsWith("Vehicle Type:") }
                val vehicleType = vehicleTypePart?.substringAfter(": ") ?: "N/A" // Extract the vehicle type
                val vehicleNamePart = parts.find { it.startsWith("Vehicle Name:") }
                val vehicleName = vehicleNamePart?.substringAfter(": ") ?: "N/A" // Extract the vehicle type
                val linePart = parts.find { it.startsWith("Line:") }
                val line = linePart?.substringAfter(": ") ?: "N/A" // Extract the vehicle type

                // Now set the extracted vehicle type to the TextView
                vehicleTypeTextView.text = "Vehicle Type: $vehicleType\nVehicle Name: $vehicleName\nLine: $line"
            }

//            if (details.isNotEmpty()) {
//                // Clear the adapter and add all the new details
//                adapter.clear()
//                adapter.addAll(details.get(0))
//                adapter.notifyDataSetChanged()
//            } else {
//                // Log if the details list is empty
//                Log.d("TransitDetailLog", "No transit details to display.")
//            }
        })
            // Update UI with transit details here
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PublicTransport.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PublicTransport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}