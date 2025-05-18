package com.example.playlistmaker.settings.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.usecase.GetThemeModeUseCase
import com.example.playlistmaker.settings.domain.usecase.SetThemeModeUseCase


class SettingsViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val sharedPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val repo = SettingsRepositoryImpl(sharedPrefs)
    private val getTheme = GetThemeModeUseCase(repo)
    private val setTheme = SetThemeModeUseCase(repo)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(getTheme, setTheme) as T
    }
}