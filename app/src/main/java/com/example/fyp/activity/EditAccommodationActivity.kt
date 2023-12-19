package com.example.fyp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.database.AccommodationImages
import com.example.fyp.database.Accommodations
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.atomic.AtomicInteger

class EditAccommodationActivity : AppCompatActivity() {

    private lateinit var edtAccName: EditText
    private lateinit var edtAccAddress1: EditText
    private lateinit var edtAccAddress2: EditText
    private lateinit var stateSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var rentFeeEditText: EditText
    private lateinit var regionEditText: EditText
    private lateinit var contractSpinner: Spinner
    private lateinit var edtAccDesc: EditText
    private lateinit var accNameInputLayout: TextInputLayout
    private lateinit var accAddressLine1InputLayout: TextInputLayout
    private lateinit var accAddressLine2InputLayout: TextInputLayout
    private lateinit var rentFeeInputLayout: TextInputLayout
    private lateinit var stateInputLayout: TextInputLayout
    private lateinit var cityInputLayout: TextInputLayout
    private lateinit var contractInputLayout: TextInputLayout
    private lateinit var accDescInputLayout: TextInputLayout
    private lateinit var imageErrorInputLayout: TextInputLayout
    private lateinit var imageContainer: LinearLayout
    private lateinit var accomID: String
    private val imageUris = mutableListOf<Uri>()
    private val removedImageUrls = mutableListOf<String>()
    private var city:String = ""
    private var rate:String = ""


    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_accommodation)

        edtAccName = findViewById(R.id.edtAccName)
        edtAccAddress1 = findViewById(R.id.edtAccAddress1)
        edtAccAddress2 = findViewById(R.id.edtAccAddress2)
        stateSpinner = findViewById(R.id.stateSpinner)
        citySpinner = findViewById(R.id.citySpinner)
        rentFeeEditText = findViewById(R.id.rentFeeEditText)
        regionEditText = findViewById(R.id.regionEditText)
        contractSpinner = findViewById(R.id.contractSpinner)
        edtAccDesc = findViewById(R.id.edtAccDesc)
        accNameInputLayout = findViewById(R.id.accNameInputLayout)
        accAddressLine1InputLayout = findViewById(R.id.accAddressLine1InputLayout)
        accAddressLine2InputLayout = findViewById(R.id.accAddressLine2InputLayout)
        rentFeeInputLayout = findViewById(R.id.rentFeeInputLayout)
        stateInputLayout = findViewById(R.id.stateInputLayout)
        cityInputLayout = findViewById(R.id.cityInputLayout)
        contractInputLayout = findViewById(R.id.contractInputLayout)
        accDescInputLayout = findViewById(R.id.accDescInputLayout)
        imageErrorInputLayout = findViewById(R.id.imageErrorInputLayout)
        imageContainer = findViewById(R.id.imageContainer)
        val btnUpload = findViewById<Button>(R.id.btnUploadImage)
        val btnSave = findViewById<Button>(R.id.btnSave)

        setupSettings()
        setupToolbar()
        setupSpinners()
        setupFee()
        setupContractSpinner()

        accomID = intent.getStringExtra("ACCOM_ID").toString()

        loadImagesForAccommodation(accomID)
        loadAccommodationData(accomID)

        setupInputField(edtAccName, edtAccAddress1, edtAccAddress2, rentFeeEditText, edtAccDesc)

        btnUpload.setOnClickListener {
            // Open image picker to select multiple images
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, EditAccommodationActivity.IMAGE_PICK_CODE)
        }

        btnSave.setOnClickListener {
            hideKeyboard(it)
            if (validateInputs()) {
                uploadImages(accomID, imageUris) { imageUrls ->
                    storeAccommodationData(accomID, imageUrls)
                    deleteRemovedImages(accomID)
                    showToast("Accommodation record updated")
                    val intent = Intent(this, ManageAccommodationActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

    private fun deleteRemovedImages(accomID: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val databaseRef = FirebaseDatabase.getInstance().getReference("AccommodationImages")

        removedImageUrls.forEach { imageUrl ->
            // Delete from Firebase Storage
            val fileRef = storageRef.storage.getReferenceFromUrl(imageUrl)
            fileRef.delete().addOnSuccessListener {
                // Delete reference from Firebase Realtime Database
                databaseRef.orderByChild("accomID").equalTo(accomID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (childSnapshot in snapshot.children) {
                                    val accommodationImage = childSnapshot.getValue(AccommodationImages::class.java)
                                    if (accommodationImage?.images == imageUrl) {
                                        childSnapshot.ref.removeValue()
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
            }.addOnFailureListener {
            }
        }
    }

    private fun loadAccommodationData(accomID: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Accommodations")
        databaseReference.child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val accommodation = snapshot.getValue(Accommodations::class.java)
                    accommodation?.let {
                        // Remove hint before setting text
                        accNameInputLayout.hint = ""
                        edtAccName.setText(it.accomName)

                        accAddressLine1InputLayout.hint = ""
                        edtAccAddress1.setText(it.accomAddress1)

                        accAddressLine2InputLayout.hint = ""
                        edtAccAddress2.setText(it.accomAddress2)

                        rentFeeInputLayout.hint = ""
                        rentFeeEditText.setText(it.rentFee)

                        accDescInputLayout.hint = ""
                        edtAccDesc.setText(it.accomDesc)

                        // Set state and update city spinner accordingly
                        selectSpinnerItemByValue(stateSpinner, it.state)
                        city = it.city
                        updateCitySpinner(it.state)

                        // Set city after updating city spinner
                        selectSpinnerItemByValue(citySpinner, it.city)

                        // Set contract agreement
                        selectSpinnerItemByValue(contractSpinner, it.agreement)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load accommodation data")
            }
        })
    }

    private fun selectSpinnerItemByValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter
        for (position in 0 until adapter.count) {
            if (adapter.getItem(position).toString() == value) {
                spinner.setSelection(position)
                return
            }
        }
    }

    private fun loadImagesForAccommodation(accomID: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("AccommodationImages")
        databaseReference.orderByChild("accomID").equalTo(accomID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (accommodationImageSnapshot in snapshot.children) {
                            val imageUrl = accommodationImageSnapshot.child("images").getValue(String::class.java)
                            imageUrl?.let { displayImage(Uri.parse(it)) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun displayImage(uri: Uri) {
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(400, 400) // Set your desired size
            scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(this@EditAccommodationActivity)
                .load(uri)
                .into(this)
            setOnClickListener { showRemoveImageDialog(this, uri) }
        }
        imageContainer.addView(imageView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditAccommodationActivity.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val processImage = { uri: Uri ->
                imageUris.add(uri) // Add URI to the list
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(400, 400) // Set your desired size
                    scaleType = ImageView.ScaleType.FIT_XY
                    setImageURI(uri)
                    setOnClickListener { showRemoveImageDialog(this, uri) }
                }
                imageContainer.addView(imageView)
            }

            data?.clipData?.let { clipData ->
                // Multiple images selected
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    processImage(imageUri)
                }
            } ?: data?.data?.let { imageUri ->
                // Single image selected
                processImage(imageUri)
            }
        }
    }

    private fun showRemoveImageDialog(imageView: ImageView, uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("Remove Image")
            .setMessage("Do you want to remove this image?")
            .setPositiveButton("Yes") { dialog, _ ->
                val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)
                imageContainer.removeView(imageView)
                imageUris.remove(uri) // Remove URI from the list
                removedImageUrls.add(uri.toString()) // Add to removed images list
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun uploadImages(accomID: String, images: List<Uri>, callback: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageUrls = mutableListOf<String>()
        val uploadedCount = AtomicInteger(0)

        if (images.isEmpty()) {
            // No new images to upload, call the callback with an empty list
            callback(imageUrls)
            return
        }

        images.forEach { uri ->
            val fileRef = storageRef.child("accommodation_images/$accomID/${uri.lastPathSegment}")
            fileRef.putFile(uri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageUrls.add(downloadUri.toString())
                    if (uploadedCount.incrementAndGet() == images.size) {
                        callback(imageUrls)
                    }
                } else {
                    showToast("Failed to upload image")
                }
            }
        }
    }

    private fun storeAccommodationData(accomID: String, imageUrls: List<String>) {
        val database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("Accommodations").child(accomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingAccommodation = snapshot.getValue(Accommodations::class.java)
                val existingAgentId = existingAccommodation?.agentId ?: "null"

                val accommodation = Accommodations(
                    accomID = accomID,
                    accomName = edtAccName.text.toString().trim(),
                    accomAddress1 = edtAccAddress1.text.toString().trim(),
                    accomAddress2 = edtAccAddress2.text.toString().trim(),
                    accomDesc = edtAccDesc.text.toString().trim(),
                    rentFee = rentFeeEditText.text.toString().trim(),
                    state = stateSpinner.selectedItem.toString(),
                    city = citySpinner.selectedItem.toString(),
                    agreement = contractSpinner.selectedItem.toString(),
                    rate = rate,
                    ownerId = userId,
                    agentId = existingAgentId
                )

                database.child("Accommodations").child(accomID).setValue(accommodation)
                    .addOnSuccessListener {
                        storeAccommodationImages(accomID, imageUrls)
//                        if (existingAgentId != "null") {
//                            updateWorkerSalary(accomID, existingAgentId, accommodation.rentFee, accommodation.rate)
//                        }

                        checkForExistingPayment(accomID, existingAgentId, accommodation) { shouldUpdateSalary ->
                            if (shouldUpdateSalary) {
                                updateWorkerSalary(accomID, existingAgentId, accommodation.rentFee, accommodation.rate)
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }

                // Check if there is a corresponding payment record for this accomID
//                checkForExistingPayment(accomID, existingAgentId, accommodation) { shouldUpdateSalary ->
//                    if (shouldUpdateSalary) {
//                        updateWorkerSalary(accomID, existingAgentId, accommodation.rentFee, accommodation.rate)
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun checkForExistingPayment(accomID: String, existingAgentId: String, accommodation: Accommodations, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore
        firestore.collection("Payments")
            .whereEqualTo("accomID", accomID)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No existing payment record, proceed with updating salary
                    callback(true)
                } else {
                    // Existing payment record found, do not update salary
                    callback(false)
                }
            }
            .addOnFailureListener {
                // Handle any errors
            }
    }

    private fun updateWorkerSalary(accomID: String, agentId: String, rentFee: String, rate: String) {
        val newSalary = calculateCommission(rentFee, rate)
        val workersRef = FirebaseDatabase.getInstance().getReference("Workers")

        workersRef.orderByChild("agentId").equalTo(agentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        if (child.child("accomID").getValue(String::class.java) == accomID) {
                            child.ref.child("salary").setValue(newSalary)
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun calculateCommission(rentFee: String, rate: String): String {
        val monthlyCommission = rentFee.toDouble() * rate.toDouble()
        return "RM ${String.format("%.2f", monthlyCommission)}"
    }

    private fun storeAccommodationImages(accomID: String, imageUrls: List<String>) {
        val database = FirebaseDatabase.getInstance().reference
        val accomImagesRef = database.child("AccommodationImages")

        imageUrls.forEach { imageUrl ->
            // Create a new AccommodationImages object for each image
            val accommodationImage = AccommodationImages(accomID, imageUrl)

            // Generate a unique child for each AccommodationImages object
            val uniqueImageRef = accomImagesRef.push()
            uniqueImageRef.setValue(accommodationImage)
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                }
        }
    }

    private fun setupContractSpinner() {
        val spinner: Spinner = findViewById(R.id.contractSpinner)
        val contractDurations = listOf("1 year", "2 years", "3 years", "4 years", "5 years")

        val contractAdapter = object : ArrayAdapter<String>(
            this, // Context
            android.R.layout.simple_spinner_item, // Layout for the normal spinner view
            contractDurations // Data
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                // Provide the layout for the dropdown view
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.text = contractDurations[position]
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Disable the first item (prompt)
            }
        }

        contractAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = contractAdapter
        spinner.setSelection(0) // Set default selection to the prompt

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                rate = when (position) {
                    0, 1, 2 -> "1.25" // For 1, 2, 3 years
                    3 -> "1.50" // For 4 years
                    4 -> "1.75" // For 5 years
                    else -> "1.75" // For more than 5 years
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                rate = ""
            }
        }
    }

    private fun setupSettings(){
        regionEditText.setText("Malaysia")
        regionEditText.isEnabled = false
        edtAccDesc.imeOptions = EditorInfo.IME_ACTION_DONE
        edtAccDesc.setRawInputType(InputType.TYPE_CLASS_TEXT)
        edtAccDesc.setOnTouchListener { v, event ->
            if (v.id == R.id.edtAccDesc) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
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

    private fun setupFee(){
        rentFeeEditText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() != current) {
                    rentFeeEditText.removeTextChangedListener(this)

                    val userInput = s.toString().replace(Regex("[^\\d.]"), "")
                    val splitInput = userInput.split(".")

                    var beforeDecimal = ""
                    var afterDecimal = ""

                    if (splitInput.isNotEmpty()) {
                        beforeDecimal = splitInput[0].take(5) // Take only first 5 digits
                        // Remove leading zeros except for a single zero before decimal
                        if (beforeDecimal.length > 1 && beforeDecimal.startsWith("0")) {
                            beforeDecimal = beforeDecimal.toInt().toString()
                        }
                    }
                    if (splitInput.size > 1) {
                        afterDecimal = splitInput[1].take(2) // Take only first 2 digits after decimal
                    }

                    val formatted = buildString {
                        append(beforeDecimal)
                        if (afterDecimal.isNotEmpty() || (userInput.endsWith(".") && beforeDecimal.isNotEmpty())) {
                            append(".")
                            append(afterDecimal)
                        }
                    }

                    current = formatted
                    rentFeeEditText.setText(formatted)
                    rentFeeEditText.setSelection(formatted.length)

                    rentFeeEditText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun setupSpinners() {
        // Include a default selection for the state spinner
        val states = listOf("Please choose the state") + stateCitiesMap.keys.toList()
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stateSpinner.adapter = stateAdapter

        // Initialize the city spinner with a default message
        updateCitySpinner("Please choose the state")

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val state = parent.getItemAtPosition(position).toString()
                updateCitySpinner(state)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case where no state selection is made
                updateCitySpinner("Please choose the state")
            }
        }
    }

    private fun updateCitySpinner(state: String, selectedCity: String? = null) {
        val cities = if (state in stateCitiesMap.keys) {
            listOf("Please choose the city") + stateCitiesMap[state]!!
        } else {
            listOf("Please choose the state")
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

    private fun setupInputField(edtAccName : EditText, edtAccAddress1 : EditText, edtAccAddress2 : EditText, rentFeeEditText : EditText, edtAccDesc: EditText) {

        // Set initial hint
        accNameInputLayout.hint = "Please enter your accommodation name"
        accAddressLine1InputLayout.hint = "Please enter your accommodation address"
        accAddressLine2InputLayout.hint = "(Optional) Please continue enter the address"
        rentFeeInputLayout.hint = "Please enter rent fee"
        accDescInputLayout.hint = "Please enter your accommodation descriptions"

        // Set onFocusChangeListeners for each EditText
        setFocusChangeListener(edtAccName, accNameInputLayout, "Please enter your accommodation name")
        setFocusChangeListener(edtAccAddress1, accAddressLine1InputLayout, "Please enter your accommodation address")
        setFocusChangeListener(edtAccAddress2, accAddressLine2InputLayout, "(Optional) Please continue enter the address")
        setFocusChangeListener(rentFeeEditText, rentFeeInputLayout, "Please enter rent fee")
        setFocusChangeListener(edtAccDesc, accDescInputLayout, "Please enter your accommodation descriptions", true)
    }

    private fun setFocusChangeListener(editText: EditText, inputLayout: TextInputLayout, hint: String, closeKeyboardOnDone: Boolean = false) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputLayout.hint = ""
            } else {
                if (editText.text.toString().isEmpty()) {
                    inputLayout.hint = hint
                }
            }
        }

        if (closeKeyboardOnDone) {
            editText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v)
                    true // Consume the action
                } else {
                    false // Do not consume the action
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val edtAccName = edtAccName.text.toString().trim()
        if (edtAccName.isEmpty()) {
            accNameInputLayout.error = "Accommodation name cannot be empty"
            isValid = false
        } else if (!edtAccName.all { it.isLetter() || it.isWhitespace() || it.isDigit()}) {
            accNameInputLayout.error = "Invalid name format"
            isValid = false
        }
        else {
            accNameInputLayout.isErrorEnabled = false
        }

        val edtAccAddress1 = edtAccAddress1.text.toString().trim()
        if (edtAccAddress1.isEmpty()) {
            accAddressLine1InputLayout.error = "Accommodation address cannot be empty"
            isValid = false
        } else if (!isValidAddressFormat(edtAccAddress1)) {
            accAddressLine1InputLayout.error = "Invalid accommodation address format"
            isValid = false
        }else if (edtAccAddress1.length > 43) {
            accAddressLine1InputLayout.error = "Input out of range"
            isValid = false
        }else {
            accAddressLine1InputLayout.isErrorEnabled = false
        }

        val edtAccAddress2 = edtAccAddress2.text.toString().trim()
        if (edtAccAddress2.equals(edtAccAddress1) && !edtAccAddress2.isEmpty()){
            accAddressLine2InputLayout.error = "Both accommodation address blank cannot be same"
        } else if (!isValidAddressFormat(edtAccAddress2) && !edtAccAddress2.isEmpty()) {
            accAddressLine2InputLayout.error = "Invalid accommodation address format"
            isValid = false
        }else {
            accAddressLine2InputLayout.isErrorEnabled = false
        }

        // Validate State and City Spinners
        if (stateSpinner.selectedItem.toString() == "Please choose the state") {
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

        val rentFeeEditText = rentFeeEditText.text.toString().trim()
        if (rentFeeEditText.isEmpty()){
            rentFeeInputLayout.error = "Rent fee cannot be empty"
        }else if (rentFeeEditText in listOf("0", "0.0", "0.00")){
            rentFeeInputLayout.error = "Rent fee cannot be free"
        }
        else if (!rentFeeEditText.matches("^(?!0\\d)\\d{1,5}(\\.\\d{0,2})?$".toRegex())) {
            rentFeeInputLayout.error = "Invalid rent fee format"
            isValid = false
        }else {
            val rentFeeValue = rentFeeEditText.toDoubleOrNull() ?: 0.0
            if (rentFeeValue < 100) {
                rentFeeInputLayout.error = "Rent fee cannot less than 100"
                isValid = false
            } else {
                rentFeeInputLayout.isErrorEnabled = false
            }
        }

        val edtAccDesc = edtAccDesc.text.toString().trim()
        if (edtAccDesc.isEmpty()){
            accDescInputLayout.error = "Description cannot be empty"
        }else {
            accDescInputLayout.isErrorEnabled = false
        }

        val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)
        if (imageContainer.childCount == 0) {
            // No images uploaded, show an error
            val error = "Please upload at least one image"
            imageErrorInputLayout.error = error.padStart(error.length + 3, ' ')
            isValid = false
        } else {
            // Images uploaded, clear any previous error
            imageErrorInputLayout.isErrorEnabled = false
        }

        return isValid
    }


    // Function to check if the address format is valid
    private fun isValidAddressFormat(address: String): Boolean {
        var hasLetter = false
        var hasDigit = false
        var hasOnlyAllowedSymbols = true

        address.forEach { char ->
            when {
                char.isLetter() -> hasLetter = true
                char.isDigit() -> hasDigit = true
                char.isWhitespace() -> {} // Allow spaces
                !char.isLetterOrDigit() && char !in listOf(',', '.', '/', ' ') -> hasOnlyAllowedSymbols = false
            }
        }

        return (hasLetter || hasDigit) && hasOnlyAllowedSymbols
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