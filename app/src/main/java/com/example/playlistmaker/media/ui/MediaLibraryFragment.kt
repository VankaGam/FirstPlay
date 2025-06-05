package com.example.playlistmaker.media.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmaker.media.ui.adapter.MediaLibraryPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : Fragment() {

    companion object {
        fun newInstance(): MediaLibraryFragment = MediaLibraryFragment()
    }

    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!

    private val STATE_KEY_SELECTED_TAB = "STATE_SELECTED_TAB"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaLibraryPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_favorites)
                else -> getString(R.string.tab_playlists)
            }
        }.attach()

        if (savedInstanceState != null) {
            val savedTab = savedInstanceState.getInt(STATE_KEY_SELECTED_TAB, 0)
            binding.viewPager.currentItem = savedTab
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.viewPager?.let { vp ->
            outState.putInt(STATE_KEY_SELECTED_TAB, vp.currentItem)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}