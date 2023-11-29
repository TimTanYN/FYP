package com.example.fyp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.feedback)
        storeValue()

        val pickImageButton: ImageButton = findViewById(R.id.image)
        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        val pickVideoButton: ImageButton = findViewById(R.id.video)
        pickVideoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_CODE_VIDEO_PICK)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICK -> selectedImageUri = data.data
                REQUEST_CODE_VIDEO_PICK -> selectedVideoUri = data.data
            }

            // Check if both image and video are selected, then start upload
            if (selectedImageUri != null && selectedVideoUri != null) {
                uploadMediaAndData()
            }
        }
    }

    var serviceValue : Double = 0.0
    var performanceValue : Double = 0.0
    var appValue : Double = 0.0
    var comment : String = ""

    fun storeValue(){
        val service = findViewById<RatingBar>(R.id.serviceRating)
        val performance =  findViewById<RatingBar>(R.id.performanceRating)
        val app = findViewById<RatingBar>(R.id.userFriendlyRating)
        val commentText = findViewById<EditText>(R.id.commentText)
        comment = commentText.text.toString()


        service.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                serviceValue = service.rating.toDouble()
                println(serviceValue)
            }
        }

        app.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                appValue = app.rating.toDouble()
                println(appValue)
            }
        }

        performance.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                performanceValue = performance.rating.toDouble()
                println(performanceValue)
            }
        }
    }

    fun uploadMediaAndData() {
        // Reference to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("yourCollectionName").document("yourDocumentId")

        // Data map for Firestore
        val data = hashMapOf<String, Any>(
            // Add other data fields here
            "serviceValue" to serviceValue.toString(),
            "performanceValue" to performanceValue.toString(),
            "appValue" to appValue.toString(),
            "comment" to comment
        )

        // Upload Image
        selectedImageUri?.let {
            val imageRef = storageRef.child("uploads/${UUID.randomUUID()}")
            imageRef.putFile(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    data["imageUrl"] = uri.toString()

                    // Upload Video
                    selectedVideoUri?.let { videoUri ->
                        val videoRef = storageRef.child("uploads/${UUID.randomUUID()}")
                        videoRef.putFile(videoUri).addOnSuccessListener {
                            videoRef.downloadUrl.addOnSuccessListener { videoUri ->
                                data["videoUrl"] = videoUri.toString()

                                // Save data to Firestore
                                documentReference.set(data)
                                    .addOnSuccessListener { Log.d("Firestore", "Document successfully written!") }
                                    .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
                            }
                        }
                    }
                }
            }
        }
    }
}