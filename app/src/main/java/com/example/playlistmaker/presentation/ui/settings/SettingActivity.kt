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
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.presentation.ui.viewmodel.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this, SettingsViewModelFactory(applicationContext))
            .get(SettingsViewModel::class.java)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.controlp)
        val backButton = findViewById<ImageButton>(R.id.back)
        val shareButton = findViewById<LinearLayout>(R.id.share)
        val supportButton = findViewById<LinearLayout>(R.id.support)
        val termsButton = findViewById<LinearLayout>(R.id.agreement)

        themeSwitcher.isChecked = viewModel.isDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            recreate()
        }

        backButton.setOnClickListener { finish() }

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.android_development_course))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }

        supportButton.setOnClickListener {
            val uri = Uri.parse("mailto:${getString(R.string.email)}")
            val emailIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.body))
            }
            startActivity(emailIntent)
        }

        termsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.android_ofter_url)))
            startActivity(intent)
        }
    }
}