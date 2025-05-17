package com.example.playlistmaker.presentation.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.model.Track

class PlayerViewModel : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = STATE_DEFAULT
    private var onCompleteCallback: (() -> Unit)? = null

    fun preparePlayer(track: Track) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(track.previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer?.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompleteCallback?.invoke()
        }
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    fun startPlayer() {
        mediaPlayer?.start()
        playerState = STATE_PLAYING
    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        playerState = STATE_PAUSED
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = STATE_DEFAULT
    }

    fun isPlaying(): Boolean {
        return playerState == STATE_PLAYING
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun setOnCompleteListener(callback: () -> Unit) {
        onCompleteCallback = callback
    }
}