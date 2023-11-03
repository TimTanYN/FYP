package com.example.fyp

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity


class ContractTemplate:AppCompatActivity() {

    private lateinit var houseAddressEditText: EditText
    private lateinit var ownerNameEditText: EditText
    private lateinit var rentalAmountEditText: EditText
    private lateinit var rentalPaymentDateEditText: EditText
    private lateinit var paymentReceiverEditText: EditText
    private lateinit var gasPaymentPercentageEditText: EditText
    private lateinit var gasPaymentAmountEditText: EditText
    private lateinit var waterPaymentPercentageEditText: EditText
    private lateinit var waterPaymentAmountEditText: EditText
    private lateinit var phonePaymentPercentageEditText: EditText
    private lateinit var phonePaymentAmountEditText: EditText
    private lateinit var otherPaymentNameEditText: EditText
    private lateinit var otherPaymentPercentageEditText: EditText
    private lateinit var otherPaymentAmountEditText: EditText

    // Declaring variables for checkboxes
    private lateinit var gasCheckBox: CheckBox
    private lateinit var waterCheckBox: CheckBox
    private lateinit var phoneCheckBox: CheckBox
    private lateinit var otherCheckBox: CheckBox

    private lateinit var cleaningYes: RadioButton
    private lateinit var cleaningNo: RadioButton
    private lateinit var kitchenUseYes: RadioButton
    private lateinit var kitchenUseNo: RadioButton
    private lateinit var overnightGuestYes: RadioButton
    private lateinit var overnightGuestNo: RadioButton
    private lateinit var kitchenAppliancesYes: RadioButton
    private lateinit var kitchenAppliancesNo: RadioButton
    private lateinit var smokingYes: RadioButton
    private lateinit var smokingNo: RadioButton
    private lateinit var commonAreaYes: RadioButton
    private lateinit var commonAreaNo: RadioButton
    private lateinit var alcoholYes: RadioButton
    private lateinit var alcoholNo: RadioButton
    private lateinit var telephoneYes: RadioButton
    private lateinit var telephoneNo: RadioButton
    private lateinit var studyYes: RadioButton
    private lateinit var studyNo: RadioButton
    private lateinit var personalItemYes: RadioButton
    private lateinit var personalItemNo: RadioButton
    private lateinit var musicYes: RadioButton
    private lateinit var musicNo: RadioButton
    private lateinit var bedroomAssignmentYes: RadioButton
    private lateinit var bedroomAssignmentNo: RadioButton
    private lateinit var petsYes: RadioButton
    private lateinit var petsNo: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contract_template)

        houseAddressEditText = findViewById(R.id.houseAddress)
        ownerNameEditText = findViewById(R.id.ownerName)
        rentalAmountEditText = findViewById(R.id.rentalAmount)
        rentalPaymentDateEditText = findViewById(R.id.rentalPaymentDate)
        paymentReceiverEditText = findViewById(R.id.paymentReceiver)
        gasPaymentPercentageEditText = findViewById(R.id.gasPaymentPercentage)
        gasPaymentAmountEditText = findViewById(R.id.gasPaymentAmount)
        waterPaymentPercentageEditText = findViewById(R.id.waterPaymentPercentage)
        waterPaymentAmountEditText = findViewById(R.id.waterPaymentAmount)
        phonePaymentPercentageEditText = findViewById(R.id.phonePaymentPercentage)
        phonePaymentAmountEditText = findViewById(R.id.phonePaymentAmount)
        otherPaymentNameEditText = findViewById(R.id.otherPaymentName)
        otherPaymentPercentageEditText = findViewById(R.id.otherPaymentPercentage)
        otherPaymentAmountEditText = findViewById(R.id.otherPaymentAmount)

        // Initialize variables for checkboxes
        gasCheckBox = findViewById(R.id.gas)
        waterCheckBox = findViewById(R.id.water)
        phoneCheckBox = findViewById(R.id.phone)
        otherCheckBox = findViewById(R.id.other)

        cleaningYes = findViewById(R.id.cleaningYes)
        cleaningNo = findViewById(R.id.cleaningNo)
        kitchenUseYes = findViewById(R.id.kitchenUseYes)
        kitchenUseNo = findViewById(R.id.kitchenUseNo)
        overnightGuestYes = findViewById(R.id.overnightGuestYes)
        overnightGuestNo = findViewById(R.id.overnightGuestNo)
        kitchenAppliancesYes = findViewById(R.id.kitchenAppliancesYes)
        kitchenAppliancesNo = findViewById(R.id.kitchenAppliancesNo)
        smokingYes = findViewById(R.id.smokingYes)
        smokingNo = findViewById(R.id.smokingNo)
        commonAreaYes = findViewById(R.id.commonAreaYes)
        commonAreaNo = findViewById(R.id.commonAreaNo)
        alcoholYes = findViewById(R.id.alcoholYes)
        alcoholNo = findViewById(R.id.alcoholNo)
        telephoneYes = findViewById(R.id.telephoneYes)
        telephoneNo = findViewById(R.id.telephoneNo)
        studyYes = findViewById(R.id.studyYes)
        studyNo = findViewById(R.id.studyNo)
        personalItemYes = findViewById(R.id.personalItemYes)
        personalItemNo = findViewById(R.id.personalItemNo)
        musicYes = findViewById(R.id.musicYes)
        musicNo = findViewById(R.id.musicNo)
        bedroomAssignmentYes = findViewById(R.id.bedroomAssignmentYes)
        bedroomAssignmentNo = findViewById(R.id.bedroomAssignmentNo)
        petsYes = findViewById(R.id.petsYes)
        petsNo = findViewById(R.id.petsNo)



    }

    fun getData(){


        val houseAddress = houseAddressEditText.text.toString()
        val ownerName = ownerNameEditText.text.toString()
        val rentalAmount = rentalAmountEditText.text.toString().toDoubleOrNull()
        val rentalPaymentDate = rentalPaymentDateEditText.text.toString()
        val paymentReceiver = paymentReceiverEditText.text.toString()

        // Check if the utility checkboxes are checked
        val isGasChecked = gasCheckBox.isChecked
        val isWaterChecked = waterCheckBox.isChecked
        val isPhoneChecked = phoneCheckBox.isChecked
        val isOtherChecked = otherCheckBox.isChecked

        val isCleaningChecked = cleaningYes.isChecked
        val isKitchenUseChecked = kitchenUseYes.isChecked
        val isOvernightGuestChecked = overnightGuestYes.isChecked
        val isKitchenAppliancesChecked = kitchenAppliancesYes.isChecked
        val isSmokingChecked = smokingYes.isChecked
        val isCommonAreaChecked = commonAreaYes.isChecked
        val isAlcoholChecked = alcoholYes.isChecked
        val isTelephoneChecked = telephoneYes.isChecked
        val isStudyChecked = studyYes.isChecked
        val isPersonalItemChecked = personalItemYes.isChecked
        val isMusicChecked = musicYes.isChecked
        val isBedroomAssignmentChecked = bedroomAssignmentYes.isChecked
        val isPetsChecked = petsYes.isChecked

        val cleaningValue = if(isCleaningChecked)cleaningYes.text.toString() else cleaningNo.text.toString()
        val kitchenUseValue = if (isKitchenUseChecked) kitchenUseYes.text.toString() else kitchenUseNo.text.toString()
        val overnightGuestValue = if (isOvernightGuestChecked) overnightGuestYes.text.toString() else overnightGuestNo.text.toString()
        val kitchenAppliancesValue = if (isKitchenAppliancesChecked) kitchenAppliancesYes.text.toString() else kitchenAppliancesNo.text.toString()
        val smokingValue = if (isSmokingChecked) smokingYes.text.toString() else smokingNo.text.toString()
        val commonAreaValue = if (isCommonAreaChecked) commonAreaYes.text.toString() else commonAreaNo.text.toString()
        val alcoholValue = if (isAlcoholChecked) alcoholYes.text.toString() else alcoholNo.text.toString()
        val telephoneValue = if (isTelephoneChecked) telephoneYes.text.toString() else telephoneNo.text.toString()
        val studyValue = if (isStudyChecked) studyYes.text.toString() else studyNo.text.toString()
        val personalItemValue = if (isPersonalItemChecked) personalItemYes.text.toString() else personalItemNo.text.toString()
        val musicValue = if (isMusicChecked) musicYes.text.toString() else musicNo.text.toString()
        val bedroomAssignmentValue = if (isBedroomAssignmentChecked) bedroomAssignmentYes.text.toString() else bedroomAssignmentNo.text.toString()
        val petsValue = if (isPetsChecked) petsYes.text.toString() else petsNo.text.toString()

        // Get the values for utility details if the respective utility is checked
        val gasPaymentPercentage = if (isGasChecked) gasPaymentPercentageEditText.text.toString().toIntOrNull() else null
        val gasPaymentAmount = if (isGasChecked) gasPaymentAmountEditText.text.toString().toDoubleOrNull() else null
        val waterPaymentPercentage = if (isWaterChecked) waterPaymentPercentageEditText.text.toString().toIntOrNull() else null
        val waterPaymentAmount = if (isWaterChecked) waterPaymentAmountEditText.text.toString().toDoubleOrNull() else null
        val phonePaymentPercentage = if (isPhoneChecked) phonePaymentPercentageEditText.text.toString().toIntOrNull() else null
        val phonePaymentAmount = if (isPhoneChecked) phonePaymentAmountEditText.text.toString().toDoubleOrNull() else null
        val otherPaymentName = if (isOtherChecked) otherPaymentNameEditText.text.toString() else ""
        val otherPaymentPercentage = if (isOtherChecked) otherPaymentPercentageEditText.text.toString().toIntOrNull() else null
        val otherPaymentAmount = if (isOtherChecked) otherPaymentAmountEditText.text.toString().toDoubleOrNull() else null


    }
}