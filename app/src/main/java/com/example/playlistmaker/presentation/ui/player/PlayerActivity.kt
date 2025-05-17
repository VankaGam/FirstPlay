package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.viewmodel.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    private lateinit var playButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var trackDurationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var currentTimeTextView: TextView

    private val handler = Handler()
    private lateinit var timerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        val track = intent.getParcelableExtra<Track>("track")
        track?.let {
            updateUI(it)
            playerViewModel.preparePlayer(it)
            playerViewModel.setOnCompleteListener {
                stopTimer()
                currentTimeTextView.text = "00:00"
                playButton.setImageResource(R.drawable.play_button)
            }
        }

        playButton.setOnClickListener {
            playerViewModel.playbackControl()

            if (playerViewModel.isPlaying()) {
                startTimer()
                playButton.setImageResource(R.drawable.knob_pause)
            } else {
                stopTimer()
                playButton.setImageResource(R.drawable.play_button)
            }
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        playButton = findViewById(R.id.playButton)
        coverImageView = findViewById(R.id.coverArtwork)
        trackNameTextView = findViewById(R.id.trackName)
        artistNameTextView = findViewById(R.id.artistName)
        trackDurationValue = findViewById(R.id.time)
        albumValue = findViewById(R.id.albumName)
        yearValue = findViewById(R.id.releaseYear)
        genreValue = findViewById(R.id.genreTrack)
        countryValue = findViewById(R.id.countryTrack)
        currentTimeTextView = findViewById(R.id.currentTime)
    }

    private fun updateUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationValue.text = formatTime(track.trackTimeMillis?.toInt() ?: 0)
        albumValue.text = track.collectionName ?: "—"
        yearValue.text = track.releaseDate?.substring(0, 4) ?: "—"
        genreValue.text = track.primaryGenreName ?: "—"
        countryValue.text = track.country ?: "—"
        currentTimeTextView.text = "00:00"

        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                currentTimeTextView.text = formatTime(playerViewModel.getCurrentPosition())
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        if (::timerRunnable.isInitialized) {
            handler.removeCallbacks(timerRunnable)
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        playerViewModel.release()
    }

}