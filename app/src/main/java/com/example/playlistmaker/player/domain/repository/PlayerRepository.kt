package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun prepare(track: Track)
    fun playPause()
    fun release()
    val isPlaying: Flow<Boolean>
    val position: Flow<Int>
}