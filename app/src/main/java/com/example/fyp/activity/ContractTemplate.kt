package com.example.fyp.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ContractTemplate:AppCompatActivity() {


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
    private lateinit var lastMonthRentDate: EditText
    private lateinit var lastMonthRentAmount: EditText
    private lateinit var securityDepositDate: EditText
    private lateinit var securityDepositAmount: EditText
    private lateinit var otherDepositDate: EditText
    private lateinit var otherDepositAmount: EditText
    private lateinit var otherDepositDateRange: EditText

    // Declaring variables for checkboxes
    private lateinit var gasCheckBox: CheckBox
    private lateinit var waterCheckBox: CheckBox
    private lateinit var phoneCheckBox: CheckBox
    private lateinit var otherCheckBox: CheckBox
    private lateinit var household: CheckBox
    private lateinit var thirdParty: CheckBox
    private lateinit var majority: CheckBox
    private lateinit var principalTenant: CheckBox
    private lateinit var owner: CheckBox

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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        lastMonthRentDate = findViewById(R.id.lastMonthRentDate)
        lastMonthRentAmount = findViewById(R.id.lastMonthRentAmount)
        securityDepositAmount = findViewById(R.id.securityDepositAmount)
        securityDepositDate = findViewById(R.id.securityDepositDate)
        otherDepositAmount = findViewById(R.id.otherDepositAmount)
        otherDepositDate = findViewById(R.id.otherDepositDate)
        otherDepositDateRange = findViewById(R.id.otherDepositDateRange)



        if (ownerNameEditText.text.toString().trim().isEmpty()) {
            ownerNameEditText.error = "This field cannot be empty"
        }

        if (rentalAmountEditText.text.toString().trim().isEmpty()) {
            rentalAmountEditText.error = "This field cannot be empty"
        }

        if (rentalPaymentDateEditText.text.toString().trim().isEmpty()) {
            rentalPaymentDateEditText.error = "This field cannot be empty"
        }

        if (paymentReceiverEditText.text.toString().trim().isEmpty()) {
            paymentReceiverEditText.error = "This field cannot be empty"
        }

        if (gasPaymentPercentageEditText.text.toString().trim().isEmpty()) {
            gasPaymentPercentageEditText.error = "This field cannot be empty"
        }

        if (gasPaymentAmountEditText.text.toString().trim().isEmpty()) {
            gasPaymentAmountEditText.error = "This field cannot be empty"
        }

        if (waterPaymentPercentageEditText.text.toString().trim().isEmpty()) {
            waterPaymentPercentageEditText.error = "This field cannot be empty"
        }

        if (waterPaymentAmountEditText.text.toString().trim().isEmpty()) {
            waterPaymentAmountEditText.error = "This field cannot be empty"
        }

        if (phonePaymentPercentageEditText.text.toString().trim().isEmpty()) {
            phonePaymentPercentageEditText.error = "This field cannot be empty"
        }

        if (phonePaymentAmountEditText.text.toString().trim().isEmpty()) {
            phonePaymentAmountEditText.error = "This field cannot be empty"
        }

        if (otherPaymentNameEditText.text.toString().trim().isEmpty()) {
            otherPaymentNameEditText.error = "This field cannot be empty"
        }

        if (otherPaymentPercentageEditText.text.toString().trim().isEmpty()) {
            otherPaymentPercentageEditText.error = "This field cannot be empty"
        }

        if (otherPaymentAmountEditText.text.toString().trim().isEmpty()) {
            otherPaymentAmountEditText.error = "This field cannot be empty"
        }

        if (lastMonthRentDate.text.toString().trim().isEmpty()) {
            lastMonthRentDate.error = "This field cannot be empty"
        }

        if (lastMonthRentAmount.text.toString().trim().isEmpty()) {
            lastMonthRentAmount.error = "This field cannot be empty"
        }
        if (securityDepositAmount.text.toString().trim().isEmpty()) {
            securityDepositAmount.error = "This field cannot be empty"
        }
        if (securityDepositDate.text.toString().trim().isEmpty()) {
            securityDepositDate.error = "This field cannot be empty"
        }
        if (otherDepositAmount.text.toString().trim().isEmpty()) {
            otherDepositAmount.error = "This field cannot be empty"
        }
        if (otherDepositDate.text.toString().trim().isEmpty()) {
            otherDepositDate.error = "This field cannot be empty"
        }
        if (otherDepositDateRange.text.toString().trim().isEmpty()) {
            otherDepositDateRange.error = "This field cannot be empty"
        }

        val name = findViewById<EditText>(R.id.contractNameText)
        val remark = findViewById<EditText>(R.id.remarkText)
        if (name.text.toString().trim().isEmpty()) {
            name.error = "This field cannot be empty"
        }
        if (remark.text.toString().trim().isEmpty()) {
            remark.error = "This field cannot be empty"
        }
        // Initialize variables for checkboxes
        gasCheckBox = findViewById(R.id.gas)
        waterCheckBox = findViewById(R.id.water)
        phoneCheckBox = findViewById(R.id.phone)
        otherCheckBox = findViewById(R.id.other)
        household = findViewById(R.id.houseHold)
        thirdParty = findViewById(R.id.thirdParty)
        majority = findViewById(R.id.majority)
        principalTenant = findViewById(R.id.pricipalTenant)
        owner = findViewById(R.id.owner)

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

    private fun getData(){

        val commonAreaGroup = findViewById<RadioGroup>(R.id.commonAreaGroup)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val kitchenGroup = findViewById<RadioGroup>(R.id.kitchenGroup)
        val overnightGuestGroup = findViewById<RadioGroup>(R.id.overnightGuestGroup)
        val kitchenAppliancesGroup = findViewById<RadioGroup>(R.id.kitchenAppliancesGroup)
        val smokingGroup = findViewById<RadioGroup>(R.id.smokingGroup)
        val alcoholGroup = findViewById<RadioGroup>(R.id.alcoholGroup)
        val telephoneGroup = findViewById<RadioGroup>(R.id.telephoneGroup)
        val studyGroup = findViewById<RadioGroup>(R.id.studyGroup)
        val personalItemGroup = findViewById<RadioGroup>(R.id.personalItemGroup)
        val musicGroup = findViewById<RadioGroup>(R.id.musicGroup)
        val bedroomAssignmentGroup = findViewById<RadioGroup>(R.id.bedroomAssignmentGroup)
        val petsGroup = findViewById<RadioGroup>(R.id.petsGroup)
        if (radioGroup.checkedRadioButtonId == -1||commonAreaGroup.checkedRadioButtonId == -1||kitchenGroup.checkedRadioButtonId == -1||overnightGuestGroup.checkedRadioButtonId == -1||kitchenAppliancesGroup.checkedRadioButtonId == -1||smokingGroup.checkedRadioButtonId == -1||alcoholGroup.checkedRadioButtonId == -1||telephoneGroup.checkedRadioButtonId == -1||studyGroup.checkedRadioButtonId == -1||personalItemGroup.checkedRadioButtonId == -1||musicGroup.checkedRadioButtonId == -1||petsGroup.checkedRadioButtonId == -1||bedroomAssignmentGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_LONG).show()
        }else{
            val ownerName = ownerNameEditText.text.toString()
            val rentalAmount = rentalAmountEditText.text.toString().toDoubleOrNull()
            val rentalPaymentDate = rentalPaymentDateEditText.text.toString()
            val paymentReceiver = paymentReceiverEditText.text.toString()
            val lastMonthRentalAmount = lastMonthRentAmount.text.toString().toDoubleOrNull()
            val lastMonthRentalDate = lastMonthRentDate.text.toString()
            val securityDepositDate = securityDepositDate.text.toString()
            val securityDepositAmount = securityDepositAmount.text.toString().toDoubleOrNull()
            val otherDepositAmount = otherDepositAmount.text.toString().toDoubleOrNull()
            val otherDepositDate = otherDepositDate.text.toString()
            val otherDepositDateRange = otherDepositDateRange.text.toString()

            // Check if the utility checkboxes are checked
            val isGasChecked = gasCheckBox.isChecked
            val isWaterChecked = waterCheckBox.isChecked
            val isPhoneChecked = phoneCheckBox.isChecked
            val isOtherChecked = otherCheckBox.isChecked
            val isHouseholdChecked = household.isChecked
            val isThirdPartyChecked = thirdParty.isChecked
            val isMajorityChecked = majority.isChecked
            val isPrincipalTenantChecked = principalTenant.isChecked
            val isOwnerChecked = owner.isChecked

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

            val gas = if(isGasChecked) "Yes" else "No"
            val water = if(isWaterChecked) "Yes" else "No"
            val phone = if(isPhoneChecked) "Yes" else "No"
            val other = if(isOtherChecked) "Yes" else "No"
            val household = if(isHouseholdChecked) "Yes" else "No"
            val thirdparty = if(isThirdPartyChecked) "Yes" else "No"
            val majority = if(isMajorityChecked) "Yes" else "No"
            val principalTenant = if(isPrincipalTenantChecked) "Yes" else "No"
            val owner = if(isOwnerChecked) "Yes" else "No"

            val name = findViewById<EditText>(R.id.contractNameText).text.toString()
            val remark = findViewById<EditText>(R.id.remarkText).text.toString()
            val data: MutableMap<String, Any> = HashMap()
            data["ownerName"] = ownerName
            data["rentalAmount"] = rentalAmount.toString()
            data["rentalPaymentDate"] = rentalPaymentDate
            data["paymentReceiver"] = paymentReceiver
            data["lastMonthRentalAmount"] = lastMonthRentalAmount.toString()
            data["lastMonthRentalDate"] = lastMonthRentalDate
            data["securityDepositDate"] = securityDepositDate
            data["securityDepositAmount"] = securityDepositAmount.toString()
            data["otherDepositAmount"] = otherDepositAmount.toString()
            data["otherDepositDate"] = otherDepositDate
            data["otherDepositDateRange"] = otherDepositDateRange
            data["cleaningValue"] = cleaningValue
            data["kitchenUseValue"] = kitchenUseValue
            data["overnightGuestValue"] = overnightGuestValue
            data["kitchenAppliancesValue"] = kitchenAppliancesValue
            data["commonAreaValue"] = commonAreaValue
            data["smokingValue"] = smokingValue
            data["alcoholValue"] = alcoholValue
            data["telephoneValue"] = telephoneValue
            data["studyValue"] = studyValue
            data["personalItemValue"] = personalItemValue
            data["musicValue"] = musicValue
            data["bedroomAssignmentValue"] = bedroomAssignmentValue
            data["petsValue"] = petsValue
            data["gas"] = gas
            data["water"] = water
            data["phone"] = phone
            data["other"] = other
            data["gasPaymentPercentage"] = gasPaymentPercentage.toString()
            data["gasPaymentAmount"] = gasPaymentAmount.toString()
            data["waterPaymentPercentage"] = waterPaymentPercentage.toString()
            data["waterPaymentAmount"] = waterPaymentAmount.toString()
            data["phonePaymentPercentage"] = phonePaymentPercentage.toString()
            data["phonePaymentAmount"] = phonePaymentAmount.toString()
            data["otherPaymentName"] = otherPaymentName
            data["otherPaymentPercentage"] = otherPaymentPercentage.toString()
            data["otherPaymentAmount"] = otherPaymentAmount.toString()
            data["household"] = household
            data["thirdparty"] = thirdparty
            data["majority"] = majority
            data["principalTenant"] = principalTenant
            data["owner"] = owner
            data["name"] = name
            data["remark"] = remark
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            val contractTemplate = db.collection("Contract Template").document(userId.toString()).collection("con1").document()

            contractTemplate.set(data)
                .addOnSuccessListener {
                    // Handle successful write
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    Toast.makeText(this, "Saved to Database", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // Handle any write errors
                    Log.w(TAG, "Error writing document", e)
                }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submit -> {
                getData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}