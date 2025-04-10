package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.TrackRepository
import com.example.playlistmaker.presentation.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModelFactory

object Creator {

    // SharedPreferences
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun provideSearchViewModel(context: Context): SearchViewModel {
        val trackRepo = TrackRepositoryImpl()
        val historyRepo = SearchHistoryRepositoryImpl(context)

        return SearchViewModel(
            SearchTracksUseCase(trackRepo),
            SaveTrackToHistoryUseCase(historyRepo),
            GetSearchHistoryUseCase(historyRepo),
            ClearSearchHistoryUseCase(historyRepo)
        )
    }
}
