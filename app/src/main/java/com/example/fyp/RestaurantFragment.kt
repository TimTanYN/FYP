package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.PublicTransportAdapter
import com.example.fyp.adapter.Restaurant
import com.example.fyp.adapter.RestaurantAdapter
import com.example.fyp.viewmodel.RestaurantViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantFragment : Fragment(), RestaurantAdapter.OnRestaurantClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var model: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        model = ViewModelProvider(requireActivity())[RestaurantViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    private lateinit var restaurantLists: RecyclerView
    private lateinit var adapter:  ArrayAdapter<String>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantLists = view.findViewById(R.id.restaurant)
        val adapter = RestaurantAdapter(emptyList(),this) // Start with an empty list
        restaurantLists.adapter = adapter
        restaurantLists.layoutManager = LinearLayoutManager(context)



        model.getRestaurants().observe(viewLifecycleOwner, Observer<List<Restaurant>> { detail ->
            // Log to check if this observer is being called
            println("res")
            Log.d("RestaurantDetailLog", "Observer called with details: $detail")
            adapter.updateRestaurants(detail)
        })
        // Update UI with transit details here
    }

    override fun onRestaurantClick(restaurant: Restaurant) {
        val intent = Intent(requireContext(), Contract::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Restaurant.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}