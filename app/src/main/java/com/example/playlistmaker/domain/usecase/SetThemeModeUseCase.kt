package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class SetThemeModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }
}