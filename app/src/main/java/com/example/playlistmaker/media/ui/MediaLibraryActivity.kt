package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.media.adapter.MediaLibraryPagerAdapter
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {

    private var _binding: ActivityMediaLibraryBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val STATE_KEY_SELECTED_TAB = "STATE_SELECTED_TAB"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = MediaLibraryPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Плейлисты"
                else -> "Избранные треки"
            }
        }.attach()

        if (savedInstanceState != null) {
            val savedTab = savedInstanceState.getInt(STATE_KEY_SELECTED_TAB, 0)
            binding.viewPager.currentItem = savedTab
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_KEY_SELECTED_TAB, binding.viewPager.currentItem)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}