package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class GetThemeModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean = repository.isDarkTheme()
}