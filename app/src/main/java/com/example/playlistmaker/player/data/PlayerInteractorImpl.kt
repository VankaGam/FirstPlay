package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class PlayerInteractorImpl : PlayerInteractor {
    private var player: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: Flow<Boolean> = _isPlaying.asStateFlow()

    private val _position = MutableStateFlow(0)
    override val position: Flow<Int> = _position.asStateFlow()

    private var scope = CoroutineScope(Dispatchers.Main)

    override fun prepare(track: Track) {
        release()
        player = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepare()
            start()
            setOnCompletionListener {
                _isPlaying.value = false
                _position.value = 0
                scope.cancel()
            }
        }
        _isPlaying.value = true
        scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            while (player != null) {
                _position.value = player?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    override fun playPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    override fun release() {
        scope.cancel()
        player?.release()
        player = null
    }
}