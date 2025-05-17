package com.example.playlistmaker.domain.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val THEME_KEY = "isDarkTheme"
    }

    override fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, enabled).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}