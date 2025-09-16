package com.example.playlistmaker.player.service

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerBar {
    fun prepare(url: String, artist: String, title: String)
    fun play()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun state(): StateFlow<AudioPlayerService.ServicePlayerState>
    fun progressMs(): StateFlow<Long>
    fun showNotification()
    fun hideNotification()
}