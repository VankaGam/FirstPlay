package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.usecase.AddToHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.TrackRepository
import com.example.playlistmaker.presentation.ui.viewmodel.SearchViewModelFactory
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModelFactory

object Creator {

    // SharedPreferences
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    // Репозиторий треков
    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl()
    }

    // Репозиторий истории
    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    // UseCase: Поиск треков
    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(provideTrackRepository())
    }

    // UseCase: История
    fun provideSaveTrackUseCase(context: Context): AddToHistoryUseCase {
        return AddToHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideGetHistoryUseCase(context: Context): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideClearHistoryUseCase(context: Context): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        return SearchViewModelFactory(
            provideSearchTracksUseCase(),
            provideSaveTrackUseCase(context),
            provideGetHistoryUseCase(context),
            provideClearHistoryUseCase(context)
        )
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        val prefs = provideSharedPreferences(context)
        return SettingsViewModelFactory(prefs)
    }
}
