package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(
            searchTracks = get(),
            saveToHistory = get(),
            loadHistoryUseCase = get(),
            clearHistoryUseCase = get()
        )
    }

    viewModel {
        PlayerViewModel(
            interactor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            getTheme = get(),
            setTheme = get()
        )
    }

    viewModel { PlaylistViewModel() }
    viewModel { FavoritesViewModel() }
}