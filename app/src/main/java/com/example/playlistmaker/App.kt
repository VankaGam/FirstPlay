package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var isDarkThemeEnabled = false

    override fun onCreate() {
        super.onCreate()
        val preferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        isDarkThemeEnabled = preferences.getBoolean("isDarkTheme", false)
        applyTheme(isDarkThemeEnabled)
    }

    fun switchTheme(isDarkTheme: Boolean) {
        isDarkThemeEnabled = isDarkTheme
        applyTheme(isDarkTheme)
        val preferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        preferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}