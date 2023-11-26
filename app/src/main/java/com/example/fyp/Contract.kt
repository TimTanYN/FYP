package com.example.fyp


import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.ContractAdapter
import com.example.fyp.adapter.ContractCard
import com.example.fyp.adapter.ContractCardAdapter
import com.example.fyp.adapter.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.forms.fields.PdfFormField
import com.itextpdf.io.IOException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.File



class Contract :AppCompatActivity(), ContractAdapter.OnItemClickedListener{

    private lateinit var houseAddress: String
    private lateinit var ownerName: String
    private lateinit var rentalAmount: String
    private lateinit var rentalPaymentDate: String
    private lateinit var paymentReceiver: String
    private lateinit var gasPaymentPercentage: String
    private lateinit var gasPaymentAmount: String
    private lateinit var waterPaymentPercentage: String
    private lateinit var waterPaymentAmount: String
    private lateinit var phonePaymentPercentage: String
    private lateinit var phonePaymentAmount: String
    private lateinit var otherPaymentName: String
    private lateinit var otherPaymentPercentage: String
    private lateinit var otherPaymentAmount: String
    private lateinit var lastMonthRentDate: String
    private lateinit var lastMonthRentAmount: String
    private lateinit var securityDepositDate: String
    private lateinit var securityDepositAmount: String
    private lateinit var otherDepositDate: String
    private lateinit var otherDepositAmount: String
    private lateinit var otherDepositDateRange: String

    // Declaring variables for String
    private lateinit var gas: String
    private lateinit var water: String
    private lateinit var phone: String
    private lateinit var other: String
    private lateinit var household: String
    private lateinit var thirdParty: String
    private lateinit var majority: String
    private lateinit var principalTenant: String
    private lateinit var owner: String

    private lateinit var cleaningValue: String
    private lateinit var cleaningNo: String
    private lateinit var kitchenUseValue: String
    private lateinit var kitchenUseNo: String
    private lateinit var overnightGuestValue: String
    private lateinit var overnightGuestNo: String
    private lateinit var kitchenAppliancesValue: String
    private lateinit var kitchenAppliancesNo: String
    private lateinit var smokingValue: String
    private lateinit var smokingNo: String
    private lateinit var commonAreaValue: String
    private lateinit var commonAreaNo: String
    private lateinit var alcoholValue: String
    private lateinit var alcoholNo: String
    private lateinit var telephoneValue: String
    private lateinit var telephoneNo: String
    private lateinit var studyValue: String
    private lateinit var studyNo: String
    private lateinit var personalItemValue: String
    private lateinit var personalItemNo: String
    private lateinit var musicValue: String
    private lateinit var musicNo: String
    private lateinit var bedroomAssignmentValue: String
    private lateinit var bedroomAssignmentNo: String
    private lateinit var petsValue: String
    private lateinit var petsNo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contract)
        fetchEntireCollection()


        val productList = listOf(
            Product("Product 1", "Description 1", "Price 1"),
            Product("Product 2", "Description 2", "Price 2"),
            Product("Product 3", "Description 1", "Price 1"),
            Product("Product 4", "Description 1", "Price 1"),
            Product("Product 5", "Description 1", "Price 1"),
            Product("Product 6", "Description 1", "Price 1"),
            // Add more products as needed
        )

        val recyclerView: RecyclerView = findViewById(R.id.Contract)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ContractAdapter(productList,this)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)

                // Ensure that we have a layoutManager and that the snapHelper has a snapped view
                val layoutManager = rv.layoutManager as LinearLayoutManager?
                val snapView = snapHelper.findSnapView(layoutManager)
                val snapPosition = snapView?.let { layoutManager?.getPosition(it) }

                for (i in 0 until rv.childCount) {
                    val child = rv.getChildAt(i)
                    val childPosition = layoutManager?.getPosition(child)

                    // Normalize the scale based on the child's position relative to the snapped view
                    val isCentered = childPosition == snapPosition
                    val scale = if (isCentered) 1f else 0.8f // Full size for centered, scaled down for others
                    val alpha = if (isCentered) 1f else 0.5f // Fully opaque for centered, translucent for others

                    child.scaleX = scale
                    child.scaleY = scale
                    child.alpha = alpha
                }
            }
        })
        val leftArrowButton: ImageButton = findViewById(R.id.leftArrowButton)
        val rightArrowButton: ImageButton = findViewById(R.id.rightArrowButton)

        leftArrowButton.setOnClickListener {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val snapView = snapHelper.findSnapView(layoutManager)
            snapView?.let {
                val currentPosition = layoutManager.getPosition(it)
                val targetPosition = currentPosition - 1
                if (targetPosition >= 0) {
                    recyclerView.smoothScrollToPosition(targetPosition)
                    Log.d("PositionLeft", "Scrolling to: $targetPosition")
                }
            }
        }

        rightArrowButton.setOnClickListener {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val snapView = snapHelper.findSnapView(layoutManager)
            snapView?.let {
                val currentPosition = layoutManager.getPosition(it)
                val targetPosition = currentPosition + 1
                if (targetPosition < recyclerView.adapter?.itemCount ?: 0) {
                    recyclerView.smoothScrollToPosition(targetPosition)
                    Log.d("PositionRight", "Scrolling to: $targetPosition")
                }
            }
        }

        val list = listOf(
            ContractCard("Product 1", "Description 1", "Price 1", R.drawable.ic_launcher_background),
            ContractCard("Product 1", "Description 1", "Price 1", R.drawable.bus),
            ContractCard("Product 1", "Description 1", "Price 1", R.drawable.bus),
            ContractCard("Product 1", "Description 1", "Price 1", R.drawable.bus),
            ContractCard("Product 1", "Description 1", "Price 1", R.drawable.bus),
            ContractCard("Product 1", "Description 1", "Price 1",R.drawable.ic_launcher_background)
            // Add more products as needed
        )
        val contractCard = findViewById<RecyclerView>(R.id.contractCard)
        contractCard.layoutManager = GridLayoutManager(this, 1) // 2 items per row
        val adapter = ContractCardAdapter(list)
        contractCard.adapter = adapter
    }

    override fun onItemClicked(position: Int) {
        // Code to handle item click at the position
    }

    override fun onButtonClicked(position: Int) {
        main()
    }
    fun fetchEntireCollection() {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Contract Template").document("uniqueUserId")

// Asynchronously retrieve the document
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document was found
                    houseAddress = documentSnapshot.getString("houseAddress").toString()
                    ownerName = documentSnapshot.getString("ownerName").toString()
                    rentalAmount = documentSnapshot.getString("rentalAmount").toString()
                    rentalPaymentDate =documentSnapshot.getString("rentalPaymentDate").toString()
                    paymentReceiver = documentSnapshot.getString("paymentReceiver").toString()
                    gasPaymentPercentage = documentSnapshot.getString("gasPaymentPercentage").toString()
                    gasPaymentAmount = documentSnapshot.getString("gasPaymentAmount").toString()
                    waterPaymentPercentage = documentSnapshot.getString("waterPaymentPercentage").toString()
                    waterPaymentAmount = documentSnapshot.getString("waterPaymentAmount").toString()
                    phonePaymentAmount = documentSnapshot.getString("phonePaymentAmount").toString()
                    phonePaymentPercentage = documentSnapshot.getString("phonePaymentPercentage").toString()
                    otherPaymentName = documentSnapshot.getString("otherPaymentName") ?: "Default Name"
                    otherPaymentPercentage = documentSnapshot.getString("otherPaymentPercentage") ?: "Default Percentage"
                    otherPaymentAmount = documentSnapshot.getString("otherPaymentAmount") ?: "Default Amount"
                    lastMonthRentDate = documentSnapshot.getString("lastMonthRentDate").toString() ?: "Default Date"
                    lastMonthRentAmount = documentSnapshot.getString("lastMonthRentAmount") ?: "Default Amount"
                    securityDepositDate = documentSnapshot.getString("securityDepositDate") ?: "Default Date"
                    securityDepositAmount = documentSnapshot.getString("securityDepositAmount") ?: "Default Amount"
                    otherDepositDate = documentSnapshot.getString("otherDepositDate") ?: "Default Date"
                    otherDepositAmount = documentSnapshot.getString("otherDepositAmount") ?: "Default Amount"
                    otherDepositDateRange = documentSnapshot.getString("otherDepositDateRange") ?: "Default Date Range"
                    gas = documentSnapshot.getString("gasFieldNameInFirestore") ?: "defaultGas"
                    water = documentSnapshot.getString("waterFieldNameInFirestore") ?: "defaultWater"
                    phone = documentSnapshot.getString("phonePaymentPercentage") ?: "defaultPhone"
                    other = documentSnapshot.getString("other") ?: "default_value"
                    household = documentSnapshot.getString("household") ?: "default_value"
                    thirdParty = documentSnapshot.getString("thirdParty") ?: "default_value"
                    majority = documentSnapshot.getString("majority") ?: "default_value"
                    principalTenant = documentSnapshot.getString("principalTenant") ?: "default_value"
                    owner = documentSnapshot.getString("owner") ?: "default_value"
                    cleaningValue = documentSnapshot.getString("cleaningValue") ?: "default_value"
                    kitchenUseValue = documentSnapshot.getString("kitchenUseValue") ?: "default_value"
                    overnightGuestValue = documentSnapshot.getString("overnightGuestValue") ?: "default_value"
                    kitchenAppliancesValue = documentSnapshot.getString("kitchenAppliancesValue") ?: "default_value"
                    smokingValue = documentSnapshot.getString("smokingValue") ?: "default_value"
                    commonAreaValue = documentSnapshot.getString("commonAreaValue") ?: "Default value if null"
                    alcoholValue = documentSnapshot.getString("alcoholValue") ?: "Default value if null"
                    telephoneValue = documentSnapshot.getString("telephoneValue") ?: "Default value if null"
                    studyValue = documentSnapshot.getString("studyValue") ?: "Default value if null"
                    personalItemValue = documentSnapshot.getString("personalItemValue") ?: "Default value if null"
                    musicValue = documentSnapshot.getString("musicValue") ?: "Default Value if null"
                    bedroomAssignmentValue = documentSnapshot.getString("bedroomAssignmentValue") ?: "Default Value if null"
                    petsValue = documentSnapshot.getString("petsValue") ?: "Default Value if null"
                    val tips = documentSnapshot.getString("alcoholValue") // Correctly access the field
                    // You now have the data from the "tips" field, do something with it
                    Log.d("Firestore", "Tips: $tips")
                } else {
                    // No such document
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Log.d("Firestore", "get failed with ", exception)
            }
    }

    fun fillPdfTemplate(inputPdf: String, outputPdf: String, data: Map<String, String>) {
        PdfReader(inputPdf).use { reader ->
            PdfWriter(outputPdf).use { writer ->
                PdfDocument(reader, writer).use { pdfDoc ->
                    val form = PdfAcroForm.getAcroForm(pdfDoc, true)

                    data.forEach { (fieldName, fieldValue) ->
                        val formField = form.getField(fieldName)
                        formField?.setValue(fieldValue)
                    }

                    form.flattenFields() // Make the form read-only if you don't need to edit after saving.
                    pdfDoc.close()
                }
            }
        }
    }

    fun main() {
        val context = this // Assuming 'this' is a Context instance.
        val inputPdf = "rental-agreement-room.pdf" // The template PDF with placeholders

        // The path where the filled PDF will be saved
        val outputPdfPath =
            File(context.getExternalFilesDir(null), "rental-agreement-room-filled.pdf").absolutePath

        val data = mapOf(
            "Address" to houseAddress,
            "RentalAmount" to rentalAmount,
            "RentalPaymentDate" to rentalPaymentDate,
            "Cleaning" to cleaningValue,
            "Kitchen" to kitchenUseValue,
            "OvernightGuest" to overnightGuestValue,
            "Smoking" to smokingValue,
            "Alcohol" to alcoholValue,
            "Study" to studyValue,
            "Music" to musicValue,
            "Pets" to petsValue,
            "KitchenAppliances" to kitchenAppliancesValue,
            "CommonArea" to commonAreaValue,
            "Telephone" to telephoneValue,
            "BedroomAssignment" to bedroomAssignmentValue,
            "RefundableRent" to lastMonthRentDate,
            "RefundableRentAmount" to lastMonthRentAmount,
            "SecurityDeposit" to securityDepositDate,
            "SecurityDepositAmount" to securityDepositAmount,
            "OtherDepositAmount" to otherDepositAmount,
            "OtherDepositDate" to otherDepositDate,
            "OtherDepositRange" to otherDepositDateRange

        )

        try {
            context.assets.open(inputPdf).use { assetInputStream ->
                PdfReader(assetInputStream).use { reader ->
                    PdfWriter(outputPdfPath).use { writer ->
                        PdfDocument(reader, writer).use { pdfDoc ->
                            val form = PdfAcroForm.getAcroForm(pdfDoc, true)
                            val checkboxField = form.getField("utility")
                            checkboxField.setValue("Yes")

                            if (checkboxField is PdfFormField) {
                                // This will tell you the possible values of the checkbox
                                println("Possible values: ${checkboxField.valueAsString}")

                                // Try to set the value of the checkbox to "Yes"
                                // If "Yes" does not work, you may need to try "On" or check the actual value as printed above
                                checkboxField.setValue("Yes")

                                // You can also make sure the field is not read-only by doing:
                                checkboxField.setReadOnly(false)
                            }
                            // Select a radio button. Replace "radio_field_name" and "option" with actual values.
                            val radioGroup = form.getField("UtilityNotInclude")
                            radioGroup?.setValue("Value_znks")

                            data.forEach { (fieldName, fieldValue) ->
                                val formField = form.getField(fieldName)
                                formField?.setValue(fieldValue)
                            }

                            form.flattenFields() // Make the form read-only if you don't need to edit after saving.
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun updateArrowButtons() {
        val recyclerView: RecyclerView = findViewById(R.id.Contract)
        val leftArrowButton: ImageButton = findViewById(R.id.leftArrowButton)
        val rightArrowButton: ImageButton = findViewById(R.id.rightArrowButton)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        leftArrowButton.isEnabled = layoutManager.findFirstCompletelyVisibleItemPosition() > 0
        rightArrowButton.isEnabled = layoutManager.findLastCompletelyVisibleItemPosition() < (recyclerView.adapter?.itemCount?.minus(1) ?: 0)
    }

}


