package com.example.playlistmaker

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import model.Track
import java.io.IOException

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playPauseButton: ImageButton
    private lateinit var currentTimeText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null
    private var isPlaying = false
    private var isPrepared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playPauseButton = findViewById(R.id.playButton)
        currentTimeText = findViewById(R.id.currentTime)
        playPauseButton.isEnabled = false

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        val track = intent.getParcelableExtra<Track>("track")
        if (track != null) {
            updateUI(track)
        }


        if (track == null) return

        if (track.previewUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Аудио недоступно", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializePlayer(track.previewUrl)
        setupListeners()
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

    private fun initializePlayer(previewUrl: String) {
        releasePlayer()

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            try {
                setDataSource(previewUrl)
                setOnPreparedListener {
                    isPrepared = true
                    playPauseButton.isEnabled = true
                    if (isPlaying) {
                        startPlayback()
                    }
                }
                setOnCompletionListener {
                    stopPlayback(resetPosition = true)
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("PlayerActivity", "Ошибка MediaPlayer: $what, $extra")
                    true
                }
                prepareAsync()
            } catch (e: IOException) {
                Log.e("PlayerActivity", "Ошибка инициализации MediaPlayer", e)
                Toast.makeText(this@PlayerActivity, "Ошибка загрузки аудио", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        updateTimeRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (isPlaying && isPrepared) {
                        currentTimeText.text = formatTime(player.currentPosition)
                        handler.postDelayed(this, 200)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        playPauseButton.setOnClickListener {
            if (isPrepared) {
                if (isPlaying) {
                    pausePlayback()
                } else {
                    startPlayback()
                }
            }
        }
    }

    private fun startPlayback() {
        mediaPlayer?.let { player ->
            try {
                player.start()
                isPlaying = true
                playPauseButton.setImageResource(R.drawable.knob_pause)
                updateTimeRunnable?.let { handler.post(it) }
            } catch (e: IllegalStateException) {
                Log.e("PlayerActivity", "Ошибка старта воспроизведения", e)
            }
        }
    }

    private fun pausePlayback() {
        try {
            mediaPlayer?.pause()
            isPlaying = false
            playPauseButton.setImageResource(R.drawable.play_button)
        } catch (e: IllegalStateException) {
            Log.e("PlayerActivity", "Ошибка паузы", e)
        }

        if (updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable!!)
        }
    }

    private fun stopPlayback(resetPosition: Boolean) {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.pause()
                }
                isPlaying = false
                playPauseButton.setImageResource(R.drawable.play_button)
                updateTimeRunnable?.let { handler.removeCallbacks(it) }

                if (resetPosition) {
                    player.seekTo(0)
                    currentTimeText.text = getString(R.string.music_time)
                } else {

                }
            } catch (e: IllegalStateException) {
                Log.e("PlayerActivity", "Ошибка остановки", e)
            }
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun releasePlayer() {
        mediaPlayer?.let { player ->
            try {
                player.reset()
                player.release()
            } catch (e: Exception) {
                Log.e("PlayerActivity", "Ошибка освобождения ресурсов", e)
            }
        }
        mediaPlayer = null
        isPrepared = false
        isPlaying = false
    }

    override fun onPause() {
        super.onPause()
        pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTimeRunnable?.let { handler.removeCallbacks(it) }
        releasePlayer()
    }
}