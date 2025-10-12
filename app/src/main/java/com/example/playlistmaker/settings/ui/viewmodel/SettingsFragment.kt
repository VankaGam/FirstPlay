package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.settingsjc.ui.SettingsScreen
import com.example.playlistmaker.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val dark by viewModel.isDarkMode.observeAsState(
                    initial = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                )
                AppTheme(darkTheme = dark == true) {
                    SettingsScreen(
                        darkThemeEnabled = dark == true,
                        onToggleDarkTheme = { enabled ->
                            viewModel.switchTheme(enabled)
                            requireActivity().recreate()
                        },
                        onShareClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    getString(R.string.android_development_course)
                                )
                            }
                            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
                        },
                        onSupportClick = {
                            val uri = Uri.parse("mailto:${getString(R.string.email)}")
                            val email = Intent(Intent.ACTION_SENDTO, uri).apply {
                                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                                putExtra(Intent.EXTRA_TEXT, getString(R.string.body))
                            }
                            startActivity(email)
                        },
                        onAgreementClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.android_ofter_url))
                            )
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}