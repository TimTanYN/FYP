package com.example.fyp.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.fyp.R
import com.example.fyp.adapter.FeedbackEnd

class PhotoAndVideo:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photo_video)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val feedbackEnd = intent.getSerializableExtra("feedback") as FeedbackEnd

        println(feedbackEnd.photoUrl)
        feedbackEnd.photoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(findViewById<ImageView>(R.id.photo))
        }

        feedbackEnd.videoUrl?.let {
            val videoView = findViewById<VideoView>(R.id.video)
            videoView.setVideoURI(Uri.parse(it))
            videoView.setMediaController(MediaController(this))
            videoView.requestFocus()
            videoView.start()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            else -> super.onOptionsItemSelected(item)
        }
    }
}