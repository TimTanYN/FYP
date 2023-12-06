package com.example.fyp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.example.fyp.database.Cards
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddCardOwnerActivity : AppCompatActivity() {
    private lateinit var cardNumInput: EditText
    private lateinit var expiryDateInput: EditText
    private lateinit var secureCodeInput: EditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputLayout2: TextInputLayout
    private lateinit var textInputLayout3: TextInputLayout
    private lateinit var error : String
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        cardNumInput = findViewById(R.id.cardNumInput)
        expiryDateInput = findViewById(R.id.expiryDateInput)
        secureCodeInput = findViewById(R.id.secureCodeInput)
        textInputLayout = findViewById(R.id.textInputLayout)
        textInputLayout2 = findViewById(R.id.textInputLayout2)
        textInputLayout3 = findViewById(R.id.textInputLayout3)
        val btnAdd= findViewById<Button>(R.id.saveCardBtn)

        setupToolbar()
        setupInputField(cardNumInput, expiryDateInput, secureCodeInput)
        setupCardNumberInput()
        setupExpiryDateInput()
        setupSecureCodeInput()

        btnAdd.setOnClickListener{
            hideKeyboard(it)
            if(validateInputs()){
                saveCardDetailsToFirestore()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, AccountOwnerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveCardDetailsToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cardNumber = cardNumInput.text.toString().trim()

        firestore.collection("Cards")
            .whereEqualTo("cardNumber", cardNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Card with the same number already exists
                    showToast("Invalid card")
                } else {
                    // No card with this number exists, proceed to save
                    val expiryDate = expiryDateInput.text.toString().trim()
                    val secureCode = secureCodeInput.text.toString().trim()

                    val card = Cards(userId, cardNumber, expiryDate, secureCode)

                    firestore.collection("Cards")
                        .add(card)
                        .addOnSuccessListener {
                            showToast("Card saved")
                            val intent = Intent(this, AccountOwnerActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            showToast("Error saving card")
                        }
                }
            }
            .addOnFailureListener { e ->
                showToast("Error checking for existing card: ${e.message}")
            }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        textInputLayout.isErrorEnabled = true
        val cardNumInput = cardNumInput.text.toString().trim()
        if (cardNumInput.isEmpty()) {
            textInputLayout.error = "Card number cannot be empty"
            isValid = false
        } else if (cardNumInput.first() !in listOf('5')) {
            textInputLayout.error = "You can only enter Debit card"
            isValid = false
        }
        else if (cardNumInput.length != 19) {
            textInputLayout.error = "Card number must be in the format XXXX-XXXX-XXXX-XXXX"
            isValid = false
        }
        else {
            textInputLayout.isErrorEnabled = false
        }

        val expiryDateInput = expiryDateInput.text.toString().trim()
        if (expiryDateInput.isEmpty()) {
            textInputLayout2.error = "Expire date cannot be empty"
            isValid = false
        }else if(error == "yes"){
            textInputLayout2.error = "YY must be more than current year"
            isValid = false
        }
        else {
            textInputLayout2.isErrorEnabled = false
        }

        val secureCodeInput = secureCodeInput.text.toString().trim()
        if (secureCodeInput.isEmpty()) {
            textInputLayout3.error = "Secure code cannot be empty"
            isValid = false
        }
        else {
            textInputLayout3.isErrorEnabled = false
        }

        return isValid
    }

    private fun setupCardNumberInput() {
        cardNumInput.addTextChangedListener(object : TextWatcher {
            private var previousString = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val str = s.toString()
                if (str != previousString) {
                    val digits = str.filter { it.isDigit() }
                    val formatted = digits.chunked(4).joinToString("-").take(19)
                    previousString = formatted

                    cardNumInput.removeTextChangedListener(this)
                    cardNumInput.setText(formatted)
                    cardNumInput.setSelection(formatted.length) // Set cursor to the end
                    cardNumInput.addTextChangedListener(this)
                }
            }
        })
    }

    private fun setupExpiryDateInput() {
        expiryDateInput.addTextChangedListener(object : TextWatcher {
            private var previousText = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val currentText = s.toString()
                if (currentText != previousText) {
                    val filtered = currentText.filter { it.isDigit() }
                    val month = when {
                        filtered.length >= 2 -> filtered.take(2).toIntOrNull()?.coerceIn(1, 12)?.toString()?.padStart(2, '0')
                        filtered.isNotEmpty() -> filtered // Keep single digit as is
                        else -> ""
                    }
                    val year = filtered.drop(2).take(2)
                    val currentYearLastTwoDigits = Calendar.getInstance().get(Calendar.YEAR) % 100

                    val formatted = buildString {
                        append(month)
                        if (month?.length == 2 && year.isNotEmpty()) {
                            append("/")
                        }
                        append(year)
                    }

                    if (year.isNotEmpty() && year.length == 2 && year.toInt() <= currentYearLastTwoDigits) {
                        error = "yes"
//                        textInputLayout2.error = "YY must be more than current year"
                    } else {
                        error = "no"
                        previousText = formatted
                        expiryDateInput.removeTextChangedListener(this)
                        expiryDateInput.setText(formatted)
                        expiryDateInput.setSelection(formatted.length.coerceAtMost(expiryDateInput.text.length))
                        expiryDateInput.addTextChangedListener(this)
                    }
                }
            }
        })
    }

    private fun setupSecureCodeInput() {
        secureCodeInput.transformationMethod = PasswordTransformationMethod.getInstance()
        secureCodeInput.filters = arrayOf(InputFilter.LengthFilter(3))
    }

    private fun setupInputField(cardNumInput : EditText, expiryDateInput : EditText, secureCodeInput : EditText) {

        // Set initial hint
        textInputLayout.hint = "Please enter your card number"
        textInputLayout2.hint = "MM/YY"
        textInputLayout3.hint = "CVV"

        // Set onFocusChangeListeners for each EditText
        setFocusChangeListener(cardNumInput, textInputLayout, "Please enter your card number")
        setFocusChangeListener(expiryDateInput, textInputLayout2, "MM/YY")
        setFocusChangeListener(secureCodeInput, textInputLayout3, "CVV", true)
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

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}