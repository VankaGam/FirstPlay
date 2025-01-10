package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.URLEncoder

class SettingActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var themeSwitch: SwitchCompat
    private lateinit var supportButton: LinearLayout
    private lateinit var termsButton: LinearLayout

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
        val shareButton: LinearLayout = findViewById(R.id.share)
        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToSupport()
        }
        termsButton.setOnClickListener {
            openService()
        }
    }

    private fun writeToSupport() {
        val emailAddress = getString(R.string.email).trim()
        val subject = getString(R.string.subject).trim()
        val body = getString(R.string.body).trim()

        Log.d("SettingActivity", "Email: $emailAddress\nSubject: $subject\nBody: $body")
        val uriText = "mailto:$emailAddress" +
                "?subject=$subject&body=$body"
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(uriText)
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            Log.d("SettingActivity", "Starting email activity")
            Log.d("SettingActivity", "Email: $emailAddress, Subject: $subject, Body: $body")
            startActivity(emailIntent)
        } else {
            Log.e("SettingActivity", "No email app available")
            showNoEmailAppDialog()
        }
    }

    private fun openService() {
        val url = getString(R.string.android_ofter_url)
        Log.d("SettingActivity", "Opening URL: $url")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(packageManager) != null) {
            Log.d("SettingActivity", "Starting web browser")
            startActivity(intent)
        } else {
            Log.e("SettingActivity", "No browser app available")

            showNoBrowserDialog()
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
        val message = getString(R.string.android_development_course)
        val shareIt = getString(R.string.share_via)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, shareIt))
    }
    private fun showNoEmailAppDialog() {
        AlertDialog.Builder(this)
            .setTitle("Отсутствует приложение для почты")
            .setMessage("Установите приложение для почты из Play Market.")
            .setPositiveButton("Перейти в Play Market") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=com.google.android.gm")
                }
                startActivity(intent)
            }
            .setNegativeButton("ОК", null)
            .show()
    }

    private fun showNoBrowserDialog() {
        AlertDialog.Builder(this)
            .setTitle("Отсутствует браузер")
            .setMessage("Установите веб-браузер из Play Market.")
            .setPositiveButton("Перейти в Play Market") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=com.android.chrome")
                }
                startActivity(intent)
            }
            .setNegativeButton("ОК", null)
            .show()
    }
}