package com.example.playlistmaker.di

import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistWorkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistWorkModule = module {
    viewModel { (playlistId: Long) ->
        PlaylistWorkViewModel(
            interactor = get(),
            playlistId = playlistId
        )
    }
}