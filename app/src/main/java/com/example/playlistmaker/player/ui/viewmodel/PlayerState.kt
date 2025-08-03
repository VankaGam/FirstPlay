package com.example.playlistmaker.player.ui.viewmodel

import com.example.playlistmaker.search.domain.model.Track

data class PlayerState(
    val track: Track,
    val isPlaying: Boolean = false,
    val position: Int = 0,
    val isFavorite: Boolean = track.isFavorite
)