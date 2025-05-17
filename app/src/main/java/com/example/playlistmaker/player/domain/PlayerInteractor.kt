package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {
    fun prepare(track: Track)
    fun playPause()
    fun release()
    val isPlaying: Flow<Boolean>
    val position: Flow<Int>
}