package com.example.playlistmaker.player.data.interactor

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.media.MediaPlayerFactory
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerInteractorImpl(
    private val mediaPlayerFactory: MediaPlayerFactory
) : PlayerInteractor {

    private var player: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()
    private val _position = MutableStateFlow(0)
    override val position = _position.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var updateJob: Job? = null

    override fun prepare(track: Track) {
        release()
        player = mediaPlayerFactory.create().apply {
            setDataSource(track.previewUrl)
            prepare()
            setOnCompletionListener {
                _isPlaying.value = false
                _position.value = 0
                stopPositionUpdates()
            }
        }
        _isPlaying.value = false
        _position.value = 0
    }

    override fun playPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                stopPositionUpdates()
            } else {
                it.start()
                _isPlaying.value = true
                startPositionUpdates()
            }
        }
    }

    override fun release() {
        stopPositionUpdates()
        player?.release()
        player = null
    }

    private fun startPositionUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            while (_isPlaying.value) {
                _position.value = player?.currentPosition ?: 0
                delay(300)
            }
        }
    }

    private fun stopPositionUpdates() {
        updateJob?.cancel()
        updateJob = null
    }
}
