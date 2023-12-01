package com.example.fyp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView

class AddAccommodationActivity : AppCompatActivity() {
    private lateinit var edtAccName: EditText
    private lateinit var edtAccAddress1: EditText
    private lateinit var edtAccAddress2: EditText
    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var rentFeeEditText: EditText
    private lateinit var regionEditText: EditText
    private lateinit var edtAccDesc: EditText
    private lateinit var accNameInputLayout: TextInputLayout
    private lateinit var accAddressLine1InputLayout: TextInputLayout
    private lateinit var accAddressLine2InputLayout: TextInputLayout
    private lateinit var rentFeeInputLayout: TextInputLayout
    private lateinit var stateInputLayout: TextInputLayout
    private lateinit var cityInputLayout: TextInputLayout
    private lateinit var accDescInputLayout: TextInputLayout


    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_accommodation)

        edtAccName = findViewById(R.id.edtAccName)
        edtAccAddress1 = findViewById(R.id.edtAccAddress1)
        edtAccAddress2 = findViewById(R.id.edtAccAddress2)
        stateSpinner = findViewById(R.id.stateSpinner)
        citySpinner = findViewById(R.id.citySpinner)
        rentFeeEditText = findViewById(R.id.rentFeeEditText)
        regionEditText = findViewById(R.id.regionEditText)
        edtAccDesc = findViewById(R.id.edtAccDesc)
        accNameInputLayout = findViewById(R.id.accNameInputLayout)
        accAddressLine1InputLayout = findViewById(R.id.accAddressLine1InputLayout)
        accAddressLine2InputLayout = findViewById(R.id.accAddressLine2InputLayout)
        rentFeeInputLayout = findViewById(R.id.rentFeeInputLayout)
        stateInputLayout = findViewById(R.id.stateInputLayout)
        cityInputLayout = findViewById(R.id.cityInputLayout)
        accDescInputLayout = findViewById(R.id.accDescInputLayout)
        val btnUpload = findViewById<Button>(R.id.btnUploadImage)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        regionEditText.setText("Malaysia")
        regionEditText.isEnabled = false

        setupToolbar()

        btnUpload.setOnClickListener {
            // Open image picker to select multiple images
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }


    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ManageAccommodationActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)
            if (data?.clipData != null) {
                // Multiple images selected
                val clipData = data.clipData!!
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    val imageView = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(150, 150) // Set your desired size
                        scaleType = ImageView.ScaleType.FIT_XY
                        setImageURI(imageUri)
                    }
                    imageContainer.addView(imageView)
                }
            } else if (data?.data != null) {
                // Single image selected
                val imageUri = data.data
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(150, 150) // Set your desired size
                    scaleType = ImageView.ScaleType.FIT_XY
                    setImageURI(imageUri)
                }
                imageContainer.addView(imageView)
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

//        val fullName = fullNameEditText.text.toString().trim()
//        if (fullName.isEmpty()) {
//            fullNameInputLayout.error = "Name cannot be empty"
//            isValid = false
//        } else if (!fullName.all { it.isLetter() || it.isWhitespace() }) {
//            fullNameInputLayout.error = "Invalid name format"
//            isValid = false
//        } else if (fullName.length > 36) {
//            fullNameInputLayout.error = "Name should not exceed 36 characters"
//            isValid = false
//        }else {
//            fullNameInputLayout.isErrorEnabled = false
//        }

        // Validate State and City Spinners
        if (stateSpinner.selectedItem.toString() == "Please choose your state") {
            val error = "Please choose the state"
            stateInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else {
            stateInputLayout.isErrorEnabled = false
        }

        if (citySpinner.selectedItem.toString() == "Please choose the city") {
            val error = "Please choose the city"
            cityInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else if(citySpinner.selectedItem.toString() == "Please choose the state"){
            val error = "Please choose the state"
            cityInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else{
            cityInputLayout.isErrorEnabled = false
        }

        return isValid
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

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}