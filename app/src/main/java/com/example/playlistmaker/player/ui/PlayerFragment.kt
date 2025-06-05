package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    companion object {
        private const val ARG_TRACK = "track"
        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TRACK, track)
                }
            }
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = requireArguments().getSerializable(ARG_TRACK) as Track
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track?.let { t ->
            updateUI(t)
            viewModel.prepare(t)
        }

        lifecycleScope.launch {
            viewModel.state.collectLatest { st ->
                binding.playButton.setImageResource(
                    if (st.isPlaying) R.drawable.knob_pause else R.drawable.play_button
                )
                binding.currentTime.text = formatTime(st.position)
                // binding.seekBar.progress = st.position  (если есть seekBar)
            }
        }

        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun updateUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.time.text = formatTime(track.trackTimeMillis.toInt())
        binding.albumName.text = track.collectionName ?: "—"
        binding.releaseYear.text = track.releaseDate?.substring(0, 4) ?: "—"
        binding.genreTrack.text = track.primaryGenreName ?: "—"
        binding.countryTrack.text = track.country ?: "—"
        Glide.with(this)
            .load(track.getCoverArtwork())
            .into(binding.coverArtwork)
    }

    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}