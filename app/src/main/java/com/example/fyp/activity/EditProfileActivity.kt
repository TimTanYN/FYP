package com.example.fyp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.adapter.StateCityAdapter

class EditProfileActivity : AppCompatActivity() {

    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        stateSpinner = findViewById(R.id.stateSpinner)
        citySpinner = findViewById(R.id.citySpinner)

        setupSpinners()
        setupToolbar()
        setupCountryCodeSpinner()

    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back action
        }
    }

    private fun setupSpinners() {
        // Include a default selection for the state spinner
        val states = listOf("Please choose your preferred state") + stateCitiesMap.keys.toList()
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        // Initialize the city spinner with a default message
        updateCitySpinner("Please choose your preferred state")

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val state = parent.getItemAtPosition(position).toString()
                updateCitySpinner(state)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case where no state selection is made
                updateCitySpinner("Please choose your preferred state")
            }
        }
    }

    private fun updateCitySpinner(state: String) {
        val cities = if (state == "Please choose your preferred state") {
            listOf("Please choose your preferred state")
        } else {
            listOf("Please choose your preferred city") + (stateCitiesMap[state] ?: emptyList())
        }

        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }

    private val stateCitiesMap = mapOf(
        "Johor" to listOf(
            "Johor Bahru", "Tebrau", "Pasir Gudang", "Bukit Indah", "Skudai", "Kluang", "Batu Pahat", "Muar", "Ulu Tiram", "Senai", "Segamat", "Kulai", "Kota Tinggi", "Pontian Kechil", "Tangkak", "Bukit Bakri", "Yong Peng", "Pekan Nenas", "Labis", "Mersing", "Simpang Renggam", "Parit Raja", "Kelapa Sawit", "Buloh Kasap", "Chaah"
        ),
        "Kedah" to listOf(
            "Sungai Petani", "Alor Setar", "Kulim", "Jitra / Kubang Pasu", "Baling", "Pendang", "Langkawi", "Yan", "Sik", "Kuala Nerang", "Pokok Sena", "Bandar Baharu"
        ),
        "Kelantan" to listOf(
            "Kota Bharu", "Pangkal Kalong", "Tanah Merah", "Peringat", "Wakaf Baru", "Kadok", "Pasir Mas", "Gua Musang", "Kuala Krai", "Tumpat"
        ),
        "Melaka" to listOf(
            "Bandaraya Melaka", "Bukit Baru", "Ayer Keroh", "Klebang", "Masjid Tanah", "Sungai Udang", "Batu Berendam", "Alor Gajah", "Bukit Rambai", "Ayer Molek", "Bemban", "Kuala Sungai Baru", "Pulau Sebang", "Jasin"
        ),
        "Negeri Sembilan" to listOf(
            "Seremban", "Port Dickson", "Nilai", "Bahau", "Tampin", "Kuala Pilah"
        ),
        "Pahang" to listOf(
            "Kuantan", "Temerloh", "Bentong", "Mentakab", "Raub", "Jerantut", "Pekan", "Kuala Lipis", "Bandar Jengka", "Bukit Tinggi"
        ),
        "Perak" to listOf(
            "Ipoh", "Taiping", "Sitiawan", "Simpang Empat", "Teluk Intan", "Batu Gajah", "Lumut", "Kampung Koh", "Kuala Kangsar", "Sungai Siput Utara", "Tapah", "Bidor", "Parit Buntar", "Ayer Tawar", "Bagan Serai", "Tanjung Malim", "Lawan Kuda Baharu", "Pantai Remis", "Kampar"
        ),
        "Perlis" to listOf(
            "Kangar", "Kuala Perlis"
        ),
        "Pulau Pinang" to listOf(
            "Bukit Mertajam", "Georgetown", "Sungai Ara", "Gelugor", "Ayer Itam", "Butterworth", "Perai", "Nibong Tebal", "Permatang Kucing", "Tanjung Tokong", "Kepala Batas", "Tanjung Bungah", "Juru"
        ),
        "Sabah" to listOf(
            "Kota Kinabalu", "Sandakan", "Tawau", "Lahad Datu", "Keningau", "Putatan", "Donggongon", "Semporna", "Kudat", "Kunak", "Papar", "Ranau", "Beaufort", "Kinarut", "Kota Belud"
        ),
        "Sarawak" to listOf(
            "Kuching", "Miri", "Sibu", "Bintulu", "Limbang", "Sarikei", "Sri Aman", "Kapit", "Batu Delapan Bazaar", "Kota Samarahan"
        ),
        "Selangor" to listOf(
            "Subang Jaya", "Klang", "Ampang Jaya", "Shah Alam", "Petaling Jaya", "Cheras", "Kajang", "Selayang Baru", "Rawang", "Taman Greenwood", "Semenyih", "Banting", "Balakong", "Gombak Setia", "Kuala Selangor", "Serendah", "Bukit Beruntung", "Pengkalan Kundang", "Jenjarom", "Sungai Besar", "Batu Arang", "Tanjung Sepat", "Kuang", "Kuala Kubu Baharu", "Batang Berjuntai", "Bandar Baru Salak Tinggi", "Sekinchan", "Sabak", "Tanjung Karang", "Beranang", "Sungai Pelek", "Sepang"
        ),
        "Terengganu" to listOf(
            "Kuala Terengganu", "Chukai", "Dungun", "Kerteh", "Kuala Berang", "Marang", "Paka", "Jerteh"
        ),
        "Wilayah Persekutuan" to listOf(
            "Kuala Lumpur", "Labuan", "Putrajaya"
        )

    )

    private fun setupCountryCodeSpinner() {
        val spinner: Spinner = findViewById(R.id.countryCodeSpinner)
        val countryCodesWithNames = listOf("+60 -> Malaysia", "+65 -> Singapore", "+62 -> Indonesia", "+66 -> Thailand") // Dropdown list
        val countryCodes = listOf("+60", "+65", "+62", "+66") // Spinner view

        val countryCodeAdapter = object : ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_spinner_item, // Layout for the normal spinner view
            countryCodes // Data
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                // Provide the layout for the dropdown view
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.text = countryCodesWithNames[position]
                return view
            }
        }

        countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = countryCodeAdapter
    }
}
