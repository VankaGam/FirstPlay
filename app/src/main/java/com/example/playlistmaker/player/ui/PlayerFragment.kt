package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.viewmodel.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(R.layout.fragment_player) {

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
    private val initialTrack: Track by lazy {
        requireArguments().getSerializable(ARG_TRACK) as Track
    }

    private val viewModel: PlayerViewModel by viewModel { parametersOf(initialTrack) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPlayerBinding.bind(view)
        viewModel.prepare(initialTrack)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { st: PlayerState ->
                binding.trackName.text = st.track.trackName
                binding.artistName.text = st.track.artistName
                binding.time.text = formatTime(st.track.trackTimeMillis.toInt())
                binding.albumName.text = st.track.collectionName ?: "—"
                binding.releaseYear.text = st.track.releaseDate?.substring(0, 4) ?: "—"
                binding.genreTrack.text = st.track.primaryGenreName ?: "—"
                binding.countryTrack.text = st.track.country ?: "—"
                Glide.with(this@PlayerFragment)
                    .load(st.track.getCoverArtwork())
                    .into(binding.coverArtwork)
                binding.playButton.setImageResource(
                    if (st.isPlaying) R.drawable.knob_pause else R.drawable.play_button
                )
                binding.currentTime.text = formatTime(st.position)
                binding.addToFavoritesButton.setImageResource(
                    if (st.isFavorite) R.drawable.button_fave_activ
                    else R.drawable.favorite_track
                )
            }
        }

        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.addToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
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