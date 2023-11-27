package com.example.fyp

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
    private var selectedImageUri: Uri? = null
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
          // Ensure the fragment's view is not null
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            val selectedImageView: ImageButton = findViewById(R.id.image)
            selectedImageView.setImageURI(selectedImageUri)
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

    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri) {
        // Reference to Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference.child("upload${UUID.randomUUID()}")

        // Upload the image to Firebase Storage
        storageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                // Get the download URL of the uploaded image
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Use the downloadUri (which is a URI pointing to the uploaded image in Firebase Storage)
                    Log.d("Upload", "Uploaded image URL: $downloadUri")

                    // Get a reference to the Firestore database
                    val db = FirebaseFirestore.getInstance()

                    // Create a reference to the specific collection and document where you want to save the downloadUri
                    val documentReference = db.collection("yourCollectionName").document("yourDocumentId")

                    // Use the set() or update() method to save the downloadUri to the specified document
                    val data = hashMapOf(
                        "imageUrl" to downloadUri.toString(),
                        "serviceValue" to serviceValue.toString(),
                        "performanceValue" to performanceValue.toString(),
                        "appValue" to appValue.toString(),
                        "comment" to comment.toString()
                    )
                    documentReference.set(data)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document successfully written!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error writing document", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors during the upload
                Log.e("Upload", "Error uploading image", exception)
            }
    }
}