package com.example.playlistmaker.presentation.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.usecase.GetThemeModeUseCase
import com.example.playlistmaker.domain.usecase.SetThemeModeUseCase


class SettingsViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val sharedPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val repository = SettingsRepositoryImpl(sharedPrefs)

    private val getThemeModeUseCase = GetThemeModeUseCase(repository)
    private val setThemeModeUseCase = SetThemeModeUseCase(repository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(getThemeModeUseCase, setThemeModeUseCase) as T
    }
}