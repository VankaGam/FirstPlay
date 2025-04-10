package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SettingsRepository

class GetThemeModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean = repository.isDarkTheme()
}