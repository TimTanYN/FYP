package com.example.fyp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fyp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class Feedback:AppCompatActivity() {

    private val REQUEST_CODE_IMAGE_PICK = 1001
    private val REQUEST_CODE_VIDEO_PICK = 2001
    private lateinit var storageRef: StorageReference
    var selectedImageUri: Uri? = null
    var selectedVideoUri: Uri? = null
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var fullName : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.feedback)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val pickImageButton: ImageButton = findViewById(R.id.image)
        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        val pickVideoButton: ImageButton = findViewById(R.id.video)
        pickVideoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_CODE_VIDEO_PICK)
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/$userId/fullName")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    fullName = dataSnapshot.getValue(String::class.java)
                    Log.d("Firebase", "Full name: $fullName")
                    // Update your UI here with the retrieved full name
                } else {
                    Log.d("Firebase", "No data available at this path")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.w("Firebase", "Error reading data", databaseError.toException())
            }
        })

        val commentText = findViewById<EditText>(R.id.commentText)

        if (commentText.text.toString().trim().isEmpty()) {
            commentText.error = "This field cannot be empty"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICK -> selectedImageUri = data.data
                REQUEST_CODE_VIDEO_PICK -> selectedVideoUri = data.data
            }


        }
    }

    var serviceValue : Double = 0.0
    var performanceValue : Double = 0.0
    var appValue : Double = 0.0
    var comment : String = ""

    fun storeValue(){
        val service = findViewById<RatingBar>(R.id.serviceRating)
        val performance = findViewById<RatingBar>(R.id.performanceRating)
        val app = findViewById<RatingBar>(R.id.userFriendlyRating)
        val commentText = findViewById<EditText>(R.id.commentText)


        serviceValue = service.rating.toDouble()
        performanceValue = performance.rating.toDouble()
        appValue = app.rating.toDouble()
        comment = commentText.text.toString()
    }

    fun uploadMediaAndData() {
        val storageRef = FirebaseStorage.getInstance().reference
        val db = FirebaseFirestore.getInstance()
        val id = UUID.randomUUID().toString()
        val documentReference = db.collection("Feedback").document(id)
        storeValue()
        // Data map for Firestore
        val data = hashMapOf<String, Any>(
            "serviceValue" to serviceValue.toString(),
            "performanceValue" to performanceValue.toString(),
            "appValue" to appValue.toString(),
            "comment" to comment,
            "id" to id,
            "response" to "",
            "userId" to userId.toString(),
            "name" to fullName.toString()
        )

        // Upload Image
        selectedImageUri?.let { imageUri ->
            val imageRef = storageRef.child("uploads/${UUID.randomUUID()}")
            imageRef.putFile(imageUri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    data["imageUrl"] = uri.toString()

                    // Proceed to upload video if available
                    uploadVideoAndSaveData(selectedVideoUri, storageRef, documentReference, data)
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error uploading image", e)
            }
        } ?: run {
            // If no image, directly proceed to upload video
            uploadVideoAndSaveData(selectedVideoUri, storageRef, documentReference, data)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submit -> {
                uploadMediaAndData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun incrementFeedbackCount(context: Context) {
        val sharedPreferences = context.getSharedPreferences("FeedbackPrefs", Context.MODE_PRIVATE)
        val currentCount = sharedPreferences.getInt("feedbackCount", 0)
        sharedPreferences.edit().putInt("feedbackCount", currentCount + 1).apply()
    }

    fun uploadVideoAndSaveData(selectedVideoUri: Uri?, storageRef: StorageReference, documentReference: DocumentReference, data: HashMap<String, Any>) {
        selectedVideoUri?.let { videoUri ->
            val videoRef = storageRef.child("uploads/${UUID.randomUUID()}")
            videoRef.putFile(videoUri).addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { uri ->
                    data["videoUrl"] = uri.toString()
                }.addOnCompleteListener {
                    // Save data to Firestore after video upload is complete
                    saveDataToFirestore(documentReference, data)
                }
            }.addOnFailureListener { e ->
                Log.w("Firestore", "Error uploading video", e)
                // Save data to Firestore even if video upload fails
                saveDataToFirestore(documentReference, data)
            }
        } ?: run {
            // If no video, save other data directly to Firestore
            saveDataToFirestore(documentReference, data)
        }
    }

    fun saveDataToFirestore(documentReference: DocumentReference, data: HashMap<String, Any>) {
        documentReference.set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Document successfully written!")
                incrementFeedbackCount(this)}
            .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
    }
}