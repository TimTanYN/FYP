package com.example.fyp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.adapter.StateCityAdapter
import com.example.fyp.database.Users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileAgentActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var regionEditText: TextView
    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var mobileNumberInputLayout: TextInputLayout
    private lateinit var stateInputLayout: TextInputLayout
    private lateinit var cityInputLayout: TextInputLayout
    private lateinit var countryCodeSpinner: Spinner
    private lateinit var profileImageView: CircleImageView
    private lateinit var btnImage: ImageView

    private var city:String = ""
    private var selectedImageUri: Uri? = null // URI of the selected image

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_agent)

        fullNameEditText = findViewById(R.id.edtName)
        emailEditText = findViewById(R.id.edtEmail)
        mobileNumberEditText = findViewById(R.id.mobileNumber)
        stateSpinner = findViewById(R.id.stateSpinner)
        citySpinner = findViewById(R.id.citySpinner)
        regionEditText = findViewById(R.id.regionEditText)
        fullNameInputLayout = findViewById(R.id.fullNameInputLayout)
        mobileNumberInputLayout = findViewById(R.id.mobileNumberInputLayout)
        stateInputLayout = findViewById(R.id.stateInputLayout)
        cityInputLayout = findViewById(R.id.cityInputLayout)
        countryCodeSpinner = findViewById(R.id.countryCodeSpinner)
        profileImageView = findViewById(R.id.profile_image)
        btnImage = findViewById(R.id.editProfile)
        val btnUpdate = findViewById<TextView>(R.id.btnUpdate)

        regionEditText.setText("Malaysia")
        setupToolbar()
        setupCountryCodeSpinner()
        setupSpinners()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { fetchUserData(it) }

        // Setup listener for state spinner
        setupStateSpinnerListener()

        btnUpdate.setOnClickListener {

            hideKeyboard(it)
            if(validateInputs()){

                //Save the data
                updateUserData()

            }
        }

        btnImage.setOnClickListener {
            openImageChooser()
        }

    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveUserData(userId: String, imageUrl: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        val updatedUser = Users(
            userId = userId,
            email = emailEditText.text.toString().trim(),
            fullName = fullNameEditText.text.toString().trim(),
            phoneNumber = countryCodeSpinner.selectedItem.toString() + mobileNumberEditText.text.toString().trim(),
            userRole = "User",
            imageLink = imageUrl,
            state = stateSpinner.selectedItem.toString(),
            city = citySpinner.selectedItem.toString(),
            newUser = "no"
        )
        userRef.setValue(updatedUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Profile updated successfully")
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)

            } else {
                showToast("Failed to update profile")
            }
        }
    }

    private fun fetchUserData(userId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                user?.let {
                    // Set the user data to the fields
                    fullNameEditText.setText(it.fullName)
                    emailEditText.setText(it.email)

                    // Handle phone number
                    val countryCode = it.phoneNumber.take(3)
                    val phoneNumber = it.phoneNumber.drop(3)
                    setCountryCodeSpinner(countryCode)
                    mobileNumberEditText.setText(phoneNumber)

                    city = it.city
                    // Load profile image
                    Glide.with(this@EditProfileAgentActivity)
                        .load(it.imageLink)
                        .into(profileImageView)


                    // Handle state and city
                    setStateAndCity(it.state, it.city)

                    if (it.newUser == "yes") {
                        disableFieldsForNewAccount()
                    } else {
                        disableFieldsForProfileEditing()
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Failed to retrieve data")
            }
        })
    }


    private fun updateUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            if (selectedImageUri != null) {
                // If a new image is selected, upload it first
                uploadProfileImage(uid) { imageUrl ->
                    saveUserData(uid, imageUrl)
                }
            } else {
                // Fetch the existing image URL from Firebase and use it
                fetchCurrentImageUrl(uid) { currentImageUrl ->
                    saveUserData(uid, currentImageUrl)
                }
            }
        }
    }

    private fun fetchCurrentImageUrl(userId: String, callback: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                user?.let {
                    callback(it.imageLink)
                } ?: callback("https://firebasestorage.googleapis.com/v0/b/finalyearproject-abb52.appspot.com/o/profile.PNG?alt=media&token=ce30c842-c3c2-46da-a51f-6086aa88762a")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback("https://firebasestorage.googleapis.com/v0/b/finalyearproject-abb52.appspot.com/o/profile.PNG?alt=media&token=ce30c842-c3c2-46da-a51f-6086aa88762a")
            }
        })
    }

    private fun uploadProfileImage(userId: String, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("$userId.jpg")
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    callback(downloadUri.toString())
                } else {
                    showToast("Failed to upload profile image")
                }
            }
        }
    }
    private fun setCountryCodeSpinner(countryCode: String) {
        val countryCodes = listOf("+60", "+65", "+62", "+66")
        val index = countryCodes.indexOf(countryCode)
        if (index != -1) {
            countryCodeSpinner.setSelection(index)
        }
    }

    private fun setStateAndCity(state: String?, city: String?) {
        // Set the state spinner
        val states = listOf("Please choose your preferred state") + stateCitiesMap.keys.toList()
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        val stateIndex = states.indexOf(state).takeIf { it >= 0 } ?: 0
        stateSpinner.setSelection(stateIndex, false) // Set selection without triggering listener

        // Update the city spinner based on the state
        updateCitySpinner(state ?: "Please choose your preferred state", city)
    }

    private fun setupStateSpinnerListener() {
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedState = parent.getItemAtPosition(position).toString()
                updateCitySpinner(selectedState)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                updateCitySpinner("Please choose your preferred state")
            }
        }
    }

    private fun updateCitySpinner(state: String, selectedCity: String? = null) {
        val cities = if (state in stateCitiesMap.keys) {
            listOf("Please choose your preferred city") + stateCitiesMap[state]!!
        } else {
            listOf("Please choose your preferred city")
        }


        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        var cityIndex = 0
        // Set city if not null and exists in the list
        if (city.toString() != "null"){
            cityIndex = cities.indexOfFirst { it.equals(city, ignoreCase = true) }.takeIf { it >= 0 } ?: 0
        }else{
            cityIndex = cities.indexOfFirst { it.equals(selectedCity, ignoreCase = true) }.takeIf { it >= 0 } ?: 0

        }
        citySpinner.setSelection(cityIndex, false) // Set selection without triggering listener
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
                val selectedState = parent.getItemAtPosition(position).toString()
                updateCitySpinner(selectedState)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                updateCitySpinner("Please choose your preferred state")
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Full Name Validation: No digits allowed, not empty, and max length 36
        val fullName = fullNameEditText.text.toString().trim()
        if (fullName.isEmpty()) {
            fullNameInputLayout.error = "Name cannot be empty"
            isValid = false
        } else if (!fullName.all { it.isLetter() || it.isWhitespace() }) {
            fullNameInputLayout.error = "Invalid name format"
            isValid = false
        } else if (fullName.length > 36) {
            fullNameInputLayout.error = "Name should not exceed 36 characters"
            isValid = false
        }else {
            fullNameInputLayout.isErrorEnabled = false
        }

        // Mobile Number Validation
        val phoneNumber = mobileNumberEditText.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            val error = "Phone number cannot be empty"
            mobileNumberInputLayout.error = error.padStart(error.length + 0, ' ')
            isValid = false
        } else if(!validatePhoneNumber()){
            isValid = false
        } else {
            mobileNumberInputLayout.isErrorEnabled = false
        }

        // Validate State and City Spinners
        if (stateSpinner.selectedItem.toString() == "Please choose your preferred state") {
            val error = "Please choose the preferred state"
            stateInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else {
            stateInputLayout.isErrorEnabled = false
        }

        if (citySpinner.selectedItem.toString() == "Please choose your preferred city") {
            val error = "Please choose the preferred city"
            cityInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else if(citySpinner.selectedItem.toString() == "Please choose your preferred state"){
            val error = "Please choose the preferred state"
            cityInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else{
            cityInputLayout.isErrorEnabled = false
        }

        return isValid
    }

    private fun disableFieldsForNewAccount() {
        regionEditText.isEnabled = false
        emailEditText.isEnabled = false
        fullNameEditText.isEnabled = false
        mobileNumberEditText.isEnabled = false
        fullNameInputLayout.isErrorEnabled = false
        mobileNumberInputLayout.isErrorEnabled = false

        profileImageView.isEnabled = false
        countryCodeSpinner.isEnabled = false

        btnImage.visibility = View.GONE
    }

    private fun disableFieldsForProfileEditing() {
        regionEditText.isEnabled = false
        emailEditText.isEnabled = false
    }

    private fun validatePhoneNumber(): Boolean {
        val countryCode = countryCodeSpinner.selectedItem.toString()
        val phoneNumber = mobileNumberEditText.text.toString().trim()

        val pattern = when (countryCode) {
            "+60" -> "^1[0-9]{7,9}$" // Malaysian mobile number
            "+65" -> "^[689][0-9]{7}$" // Singaporean mobile number
            "+62" -> "^[0-9]{8,11}$" // Indonesian mobile number
            "+66" -> "^(8|9|6)[0-9]{8}$" // Thai mobile number
            else -> null // Default pattern
        }

        if (pattern == null || !phoneNumber.matches(pattern.toRegex())) {
            val error = "Invalid phone number for selected country"
            mobileNumberInputLayout.error = error.padStart(error.length + 0, ' ')
            return false
        } else {
            mobileNumberInputLayout.isErrorEnabled = false
        }
        return true
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
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

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
