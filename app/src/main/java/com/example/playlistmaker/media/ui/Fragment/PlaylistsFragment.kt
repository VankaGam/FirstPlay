package com.example.playlistmaker.media.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.viewmodel.PlaylistViewModel

class PlaylistsFragment : Fragment() {

    private val playlistsViewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerPlaylists.visibility = View.GONE
        binding.imagePlaceholderPlaylists.visibility = View.VISIBLE
        binding.textPlaceholderPlaylists.visibility = View.VISIBLE

        binding.refreshButton.setOnClickListener {
            //логика создания плейлиста
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}