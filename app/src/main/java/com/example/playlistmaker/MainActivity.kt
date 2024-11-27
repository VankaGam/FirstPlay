package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences: SharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isDarkThemeEnabled = preferences.getBoolean("isDarkTheme", false)
        updateTheme(isDarkThemeEnabled)
        setContentView(R.layout.activity_main)
        val displayButton1 = findViewById<LinearLayout>(R.id.setting)
        val displayButton2 = findViewById<LinearLayout>(R.id.poisk)
        val displayButton3 = findViewById<LinearLayout>(R.id.media)
        displayButton1.setOnClickListener {
            val displayIntent1 = Intent(this, SettingActivity::class.java)
            startActivity(displayIntent1)
        }
        displayButton2.setOnClickListener {
            val displayIntent2 = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent2)
        }
        displayButton3.setOnClickListener {
            val displayIntent3 = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayIntent3)
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
