package com.example.fyp.fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fyp.R
import com.example.fyp.activity.SignInActivity
import com.example.fyp.database.Users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OwnerSignUpFragment : Fragment() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repasswordEditText: EditText
    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var mobileNumberInputLayout: TextInputLayout
    private lateinit var passwordEditTextInputLayout: TextInputLayout
    private lateinit var repasswordEditTextInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_owner_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fullNameEditText = view.findViewById(R.id.fullName)
        emailEditText = view.findViewById(R.id.email)
        mobileNumberEditText = view.findViewById(R.id.mobileNumber)
        passwordEditText = view.findViewById(R.id.password)
        repasswordEditText = view.findViewById(R.id.repassword)
        fullNameInputLayout = view.findViewById(R.id.fullNameInputLayout)
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        mobileNumberInputLayout = view.findViewById(R.id.mobileNumberInputLayout)
        passwordEditTextInputLayout = view.findViewById(R.id.passwordInputLayout)
        repasswordEditTextInputLayout = view.findViewById(R.id.repasswordInputLayout)

        val createAccountButton: Button = view.findViewById(R.id.createAccountButton)
        val countryCodeSpinner: Spinner = view.findViewById(R.id.countryCodeSpinner)
        val signIn: TextView = view.findViewById(R.id.signInText)

        setupCountryCodeSpinner(countryCodeSpinner)
        setupNameField(fullNameEditText, fullNameInputLayout)
        setupEmailField(emailEditText, emailInputLayout)
        setupPhoneField(mobileNumberEditText, mobileNumberInputLayout)
        setupPasswordField(passwordEditText, repasswordEditText)

        val whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        fullNameInputLayout.setErrorTextColor(ColorStateList.valueOf(whiteColor))
        emailInputLayout.setErrorTextColor(ColorStateList.valueOf(whiteColor))
        mobileNumberInputLayout.setErrorTextColor(ColorStateList.valueOf(whiteColor))
        passwordEditTextInputLayout.setErrorTextColor(ColorStateList.valueOf(whiteColor))
        repasswordEditTextInputLayout.setErrorTextColor(ColorStateList.valueOf(whiteColor))

        createAccountButton.setOnClickListener {

            if (validateInputs()) {
                val email = emailEditText.text.toString().trim()
                val phoneNumber = mobileNumberEditText.text.toString().trim()

                isEmailRegistered(email) { isEmailRegistered ->
                    Log.d("EmailCheck", "Callback received: $isEmailRegistered")
                    if (!isEmailRegistered) {
                        // Proceed with phone number check
                        checkPhoneNumber(phoneNumber)
                    } else {
                        // Directly update UI here for testing
//                        activity?.runOnUiThread {
                        Log.d("EmailCheck", "Setting email error")
                        emailInputLayout.error = "Email already in use"
//                        }
                    }
                }
            }
        }

        signIn.setOnClickListener {
            showToast("Sign In Clicked")
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPhoneNumber(phoneNumber: String) {

        isPhoneNumberRegistered(phoneNumber) { isPhoneNumberRegistered ->
            Log.d("PhoneCheck", "Callback received: $isPhoneNumberRegistered")
            if (!isPhoneNumberRegistered) {
                // Proceed with account creation
//                createAccount()
            } else {
                // Phone number error
                Log.d("PhoneCheck", "Setting phone error")
                val error = "Phone number already in use"
                mobileNumberInputLayout.error = error.padStart(error.length + 5, ' ')
            }
        }
    }

    private fun createAccount() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val fullName = fullNameEditText.text.toString().trim()
        val phoneNumber = mobileNumberEditText.text.toString().trim()
        val userRole = "Owner"

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    uploadUserData(userId, email, fullName, phoneNumber, userRole)
                } else {
                    // Handle sign-up failure
                }
            }
    }

    private fun isEmailRegistered(email: String, onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isRegistered = task.result?.signInMethods?.isNotEmpty() ?: false
                    Log.d("EmailCheck", "Email: $email, isRegistered: $isRegistered")
                    onComplete(isRegistered)
                } else {
                    Log.e("EmailCheck", "Error checking email: ${task.exception}")
                    onComplete(false)
                }
            }
    }

    private fun isPhoneNumberRegistered(phoneNumber: String, onComplete: (Boolean) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isRegistered = dataSnapshot.exists()
                onComplete(isRegistered)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(false)
            }
        })
    }


    private fun uploadUserData(userId: String, email: String, fullName: String, phoneNumber: String, userRole: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val defaultImage = "https://firebasestorage.googleapis.com/v0/b/finalyearproject-abb52.appspot.com/o/profile.PNG?alt=media&token=ce30c842-c3c2-46da-a51f-6086aa88762a"
        val user = Users(userId, email, fullName, phoneNumber, userRole, defaultImage)
        databaseReference.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Sign Up Success")

                val intent = Intent(activity, SignInActivity::class.java)
                startActivity(intent)
            } else {
                showToast("Sign Up Failure")
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
        } else if (fullName.any { it.isDigit() }) {
            fullNameInputLayout.error = "Name should not contain digits"
            isValid = false
        } else if (fullName.length > 36) {
            fullNameInputLayout.error = "Name should not exceed 36 characters"
            isValid = false
        }else {
            fullNameInputLayout.isErrorEnabled = false
        }

        // Email Validation: Not empty, max length 90, and ends with specific domain extensions
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            emailInputLayout.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Invalid email format"
            isValid = false
        } else if (email.length > 90) {
            emailInputLayout.error = "Email should not exceed 90 characters"
            isValid = false
        } else if (!email.endsWith(".com") && !email.endsWith(".my") && !email.endsWith(".sg") && !email.endsWith(".th") && !email.endsWith(".id")) {
            emailInputLayout.error = "Invalid email format"
            isValid = false
        } else {
            emailInputLayout.isErrorEnabled = false
        }

        // Mobile Number Validation
        val phoneNumber = mobileNumberEditText.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            val error = "Mobile number cannot be empty"
            mobileNumberInputLayout.error = error.padStart(error.length + 5, ' ')
            isValid = false
        } else if(!validatePhoneNumber()){
            isValid = false
        } else {
            mobileNumberInputLayout.isErrorEnabled = false
        }

        // Password Validation: More than 10 characters, max 30 characters, includes letters (uppercase and lowercase), numbers, and only allow symbols like (@, #, $, %, ^, &, +, =, ., ",", !, *, ?)
        val password = passwordEditText.text.toString().trim()
        if (password.isEmpty()) {
            passwordEditTextInputLayout.error = "Password cannot be empty"
            isValid = false
        } else if (password.length > 30) {
            passwordEditTextInputLayout.error = "Password should not exceed 30 characters"
            isValid = false
        } else {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,!*?])(?=\\S+$).{10,}$"
            if (!password.matches(passwordPattern.toRegex())) {
                passwordEditTextInputLayout.error = "Password must be more than 10 characters and include uppercase, lowercase, numbers, and symbols"
                isValid = false
            } else {
                passwordEditTextInputLayout.isErrorEnabled = false
            }
        }

        // Re-enter Password Validation
        val rePassword = repasswordEditText.text.toString().trim()
        if (rePassword.isEmpty()) {
            repasswordEditTextInputLayout.error = "Password cannot be empty"
            isValid = false
        } else if (rePassword != password) {
            repasswordEditTextInputLayout.error = "Passwords do not match"
            isValid = false
        } else {
            repasswordEditTextInputLayout.isErrorEnabled = false
        }

        return isValid
    }

    private fun validatePhoneNumber(): Boolean {
        val countryCode = (view?.findViewById(R.id.countryCodeSpinner) as? Spinner)?.selectedItem.toString()
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
            mobileNumberInputLayout.error = error.padStart(error.length + 5, ' ')
            return false
        } else {
            mobileNumberInputLayout.isErrorEnabled = false
        }
        return true
    }


    private fun setupCountryCodeSpinner(spinner: Spinner) {
        val countryCodesWithNames = listOf("+60 -> Malaysia", "+65 -> Singapore", "+62 -> Indonesia", "+66 -> Thailand") // Dropdown list
        val countryCodes = listOf("+60", "+65", "+62", "+66") // Spinner view

        val countryCodeAdapter = object : ArrayAdapter<String>(
            requireContext(), // Context
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

    private fun setupNameField(nameEditText: EditText, nameInputLayout: TextInputLayout) {
        // Set initial hint
        nameInputLayout.hint = "Please enter your name"

        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameInputLayout.hint = ""
            } else {
                if (nameEditText.text.toString().isEmpty()) {
                    nameInputLayout.hint = "Please enter your name"
                }
            }
        }
    }
    private fun setupEmailField(emailEditText: EditText, emailInputLayout: TextInputLayout) {
        // Set initial hint
        emailInputLayout.hint = "Please enter your email address"

        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailInputLayout.hint = ""
            } else {
                if (emailEditText.text.toString().isEmpty()) {
                    emailInputLayout.hint = "Please enter your email address"
                }
            }
        }

        // Set an editor action listener on the email EditText
//        emailEditText.setOnEditorActionListener { v, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                // User has pressed Next on the keyboard
//                hideKeyboard(v) // Call a method to hide the keyboard
//                true // Consume the action
//            } else {
//                false // Do not consume the action
//            }
//        }
    }

    private fun setupPhoneField(mobileNumberEditText: EditText, mobileNumberInputLayout: TextInputLayout) {
        // Set initial hint
        mobileNumberEditText.hint = "Mobile number"

        mobileNumberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mobileNumberInputLayout.hint = ""
            } else {
                if (mobileNumberEditText.text.toString().isEmpty()) {
                    mobileNumberEditText.hint = "Mobile number"
                }
            }
        }
    }


    private fun setupPasswordField(passwordEditText: EditText, repasswordEditText: EditText) {
        // Set initial hints
        passwordEditTextInputLayout.hint = "Please enter your password"
        repasswordEditTextInputLayout.hint = "Please re-enter your password"

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordEditTextInputLayout.hint = ""
            } else {
                if (passwordEditText.text.toString().isEmpty()) {
                    passwordEditTextInputLayout.hint = "Please enter your password"
                }
            }
        }

        repasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                repasswordEditTextInputLayout.hint = ""
            } else {
                if (repasswordEditText.text.toString().isEmpty()) {
                    repasswordEditTextInputLayout.hint = "Please re-enter your password"
                }
            }
        }

        // Set an editor action listener on the re-enter password EditText
        repasswordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // User has pressed Done on the keyboard
                hideKeyboard(v) // Call a method to hide the keyboard
                true // Consume the action
            } else {
                false // Do not consume the action
            }
        }
    }

    // Add a method to hide the keyboard
    private fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}