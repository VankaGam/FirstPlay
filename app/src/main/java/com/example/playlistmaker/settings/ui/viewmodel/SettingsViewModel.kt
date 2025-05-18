package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.usecase.GetThemeModeUseCase
import com.example.playlistmaker.settings.domain.usecase.SetThemeModeUseCase

class SettingsViewModel(
    private val getTheme: GetThemeModeUseCase,
    private val setTheme: SetThemeModeUseCase
) : ViewModel() {

    private val _isDarkMode = MutableLiveData<Boolean>(getTheme())
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    fun switchTheme(enabled: Boolean) {
        setTheme(enabled)
        _isDarkMode.value = enabled
    }
}