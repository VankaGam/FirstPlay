package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var themeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        themeSwitch = findViewById(R.id.controlp)
        backButton = findViewById(R.id.back)
        val preferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkThemeEnabled = preferences.getBoolean("isDarkTheme", false)
        themeSwitch.isChecked = isDarkThemeEnabled
        updateTheme(isDarkThemeEnabled)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateTheme(isChecked)
            preferences.edit().putBoolean("isDarkTheme", isChecked).apply()
        }
        backButton.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}