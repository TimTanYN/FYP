package com.example.fyp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.fyp.R
import com.example.fyp.activity.SignInActivity
import com.google.android.material.textfield.TextInputLayout

class AgentSignUpFragment : Fragment() {
    private lateinit var passwordEditTextInputLayout: TextInputLayout
    private lateinit var repasswordEditTextInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_sign_up, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullName: EditText = view.findViewById(R.id.fullName)
        val email: EditText = view.findViewById(R.id.email)
        val mobileNumber: EditText = view.findViewById(R.id.mobileNumber)
        val createAccountButton: Button = view.findViewById(R.id.createAccountButton)
        val countryCodeSpinner: Spinner = view.findViewById(R.id.countryCodeSpinner)
        val passwordEditText: EditText = view.findViewById(R.id.password)
        val repasswordEditText: EditText = view.findViewById(R.id.repassword)
        val signIn: TextView = view.findViewById(R.id.signInText)

        passwordEditTextInputLayout = view.findViewById(R.id.passwordInputLayout)
        repasswordEditTextInputLayout = view.findViewById(R.id.repasswordInputLayout)

        setupCountryCodeSpinner(countryCodeSpinner)
        setupPasswordField(passwordEditText, repasswordEditText)

        createAccountButton.setOnClickListener {
            // Insert your account creation logic here.
        }

        signIn.setOnClickListener {
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupCountryCodeSpinner(spinner: Spinner) {
        val countryCodeAdapter = ArrayAdapter(
            requireContext(), // Use requireContext() to get the context inside a fragment.
            android.R.layout.simple_spinner_item,
            listOf("+60 -> Malaysia", "+65 -> Singapora", "+62 -> Indonesia", "+66 -> Thailand") // Sample country codes.
        )
        countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = countryCodeAdapter
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
}