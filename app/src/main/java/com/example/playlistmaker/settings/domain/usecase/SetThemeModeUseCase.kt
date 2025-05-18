package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SetThemeModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }
}