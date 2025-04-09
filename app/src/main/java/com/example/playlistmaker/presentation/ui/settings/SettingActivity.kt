package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels {
        val prefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
        SettingsViewModelFactory(prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.controlp)
        val backButton = findViewById<ImageButton>(R.id.back)
        val shareButton = findViewById<LinearLayout>(R.id.share)
        val supportButton = findViewById<LinearLayout>(R.id.support)
        val termsButton = findViewById<LinearLayout>(R.id.agreement)

        themeSwitcher.isChecked = viewModel.isDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val message = getString(R.string.android_development_course)
            val shareIt = getString(R.string.share_via)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, shareIt))
        }

        supportButton.setOnClickListener {
            val emailAddress = getString(R.string.email)
            val subject = getString(R.string.subject)
            val body = getString(R.string.body)

            val uriText = "mailto:$emailAddress?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(uriText)
            }

            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            } else {
                showNoEmailAppDialog()
            }
        }

        termsButton.setOnClickListener {
            val url = getString(R.string.android_ofter_url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                showNoBrowserDialog()
            }
        }
    }

    private fun showNoEmailAppDialog() {
        AlertDialog.Builder(this)
            .setTitle("Отсутствует приложение для почты")
            .setMessage("Установите приложение для почты из Play Market.")
            .setPositiveButton("Перейти в Play Market") { _, _ ->
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gm"))
                )
            }
            .setNegativeButton("ОК", null)
            .show()
    }

    private fun showNoBrowserDialog() {
        AlertDialog.Builder(this)
            .setTitle("Отсутствует браузер")
            .setMessage("Установите веб-браузер из Play Market.")
            .setPositiveButton("Перейти в Play Market") { _, _ ->
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.chrome"))
                )
            }
            .setNegativeButton("ОК", null)
            .show()
    }
}