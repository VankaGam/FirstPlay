package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModelFactory
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModelFactory
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory

object Creator {

    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        val trackRepo = TrackRepositoryImpl()
        val historyRepo = SearchHistoryRepositoryImpl(
            provideSearchHistoryPrefs(context)
        )
        return SearchViewModelFactory(
            SearchTracksUseCase(trackRepo),
            SaveTrackToHistoryUseCase(historyRepo),
            GetSearchHistoryUseCase(historyRepo),
            ClearSearchHistoryUseCase(historyRepo)
        )
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        return SettingsViewModelFactory(context)
    }

    fun provideSearchHistoryPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    fun providePlayerInteractor(): PlayerRepository = PlayerRepositoryImpl()
    fun providePlayerViewModelFactory(): PlayerViewModelFactory {
        return PlayerViewModelFactory(providePlayerInteractor())
    }
}
