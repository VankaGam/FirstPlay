package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModels {
        Creator.providePlayerViewModelFactory()
    }

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
    //private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()

        val track = intent.getSerializableExtra("track") as Track
        updateUI(track)
        viewModel.prepare(track)

        lifecycleScope.launch {
            viewModel.state.collectLatest { st ->
                playButton.setImageResource(
                    if (st.isPlaying) R.drawable.knob_pause else R.drawable.play_button
                )
                currentTimeTextView.text = formatTime(st.position)
                //seekBar.progress = st.position
            }
        }

        playButton.setOnClickListener {
            viewModel.playPause()
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
        //seekBar = findViewById(R.id.seekBar)
    }

    private fun updateUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationValue.text = formatTime(track.trackTimeMillis.toInt())
        albumValue.text = track.collectionName ?: "—"
        yearValue.text = track.releaseDate?.substring(0, 4) ?: "—"
        genreValue.text = track.primaryGenreName ?: "—"
        countryValue.text = track.country ?: "—"
        currentTimeTextView.text = formatTime(0)
        //seekBar.max = track.trackTimeMillis.toInt()

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }
}