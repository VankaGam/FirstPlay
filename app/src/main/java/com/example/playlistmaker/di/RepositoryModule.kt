package com.example.playlistmaker.di

import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    factory<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(
            get(named(Qualifiers.APP_PREFS))
        )
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

}