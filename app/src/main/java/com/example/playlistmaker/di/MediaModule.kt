package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.media.DefaultMediaPlayerFactory
import com.example.playlistmaker.player.data.media.MediaPlayerFactory
import org.koin.dsl.module

val mediaModule = module {
    single<MediaPlayerFactory> { DefaultMediaPlayerFactory() }
}