package com.example.playlistmaker.media.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.ui.viewmodel.FavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.TrackAdapter

class FavoritesFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackAdapter = TrackAdapter(
            tracks = emptyList(),
            onItemClick = { track ->
                val frag = PlayerFragment.newInstance(track)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, frag)
                    .addToBackStack(null)
                    .commit()
            },
            onFavoriteClick = { track ->
                favoritesViewModel.onFavoriteClicked(track)
            }
        )

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = trackAdapter

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.recyclerFavorites.visibility = View.GONE
                binding.imagePlaceholderFavorites.visibility = View.VISIBLE
                binding.textPlaceholderFavorites.visibility = View.VISIBLE
            } else {
                binding.imagePlaceholderFavorites.visibility = View.GONE
                binding.textPlaceholderFavorites.visibility = View.GONE
                binding.recyclerFavorites.visibility = View.VISIBLE
                trackAdapter.updateTracks(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}