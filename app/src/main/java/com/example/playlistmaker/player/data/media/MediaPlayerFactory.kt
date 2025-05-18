package com.example.playlistmaker.player.data.media

import android.media.MediaPlayer

interface MediaPlayerFactory {
    fun create(): MediaPlayer
}

class DefaultMediaPlayerFactory : MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()
}