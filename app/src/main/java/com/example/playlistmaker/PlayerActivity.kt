package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import model.Track

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>("track")
        if (track != null) {
            updateUI(track)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateUI(track: Track) {
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.time).text = track.getFormattedTrackTime()
        findViewById<TextView>(R.id.albumName).text = track.collectionName ?: "Неизвестный альбом"
        findViewById<TextView>(R.id.releaseYear).text = track.releaseDate?.take(4) ?: "Неизвестный год"
        findViewById<TextView>(R.id.genreTrack).text = track.primaryGenreName ?: "Неизвестный жанр"
        findViewById<TextView>(R.id.countryTrack).text = track.country ?: "Неизвестная страна"

        val coverArtwork = findViewById<ImageView>(R.id.coverArtwork)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(coverArtwork)
    }
}