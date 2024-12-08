package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var supportButton: ImageButton
    private lateinit var termsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        themeSwitch = findViewById(R.id.controlp)
        backButton = findViewById(R.id.back)
        supportButton = findViewById(R.id.support)
        termsButton = findViewById(R.id.agreement)

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
        val shareButton: ImageButton = findViewById(R.id.vectorShare)
        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToSupport()
        }
        termsButton.setOnClickListener {
            openTermsOfService()
        }
    }

    private fun writeToSupport() {
        val emailAddress = "lumiamicrosoft@yandex.ru"
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val body = "Спасибо разработчикам и разработчицам за крутое приложение!"

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(this, "!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openTermsOfService() {
        val url = "https://yandex.ru/legal/practicum_offer/"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "!!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    private fun shareApp() {
        val message = "Курс по Андроид-разработке! \nhttps://practicum.yandex.ru/profile/android-developer-plus/"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением через"))
    }
}