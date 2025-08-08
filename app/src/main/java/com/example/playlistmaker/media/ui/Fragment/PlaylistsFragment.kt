package com.example.playlistmaker.media.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.media.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.launch

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistsAdapter
    private val viewModel: PlaylistsViewModel by viewModel()

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

        // начальные состояния заглушек
        binding.recyclerPlaylists.visibility = View.GONE
        binding.imagePlaceholderPlaylists.visibility = View.VISIBLE
        binding.textPlaceholderPlaylists.visibility = View.VISIBLE

        // кнопка "Новый плейлист"
        binding.refreshButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibrary_to_createPlaylist)
        }

        // Recycler + Adapter
        adapter = PlaylistsAdapter { playlist ->
            // пока переход на экран плейлиста не нужен по ТЗ
        }
        binding.recyclerPlaylists.adapter = adapter
        binding.recyclerPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        // Подписка на Flow из VM
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { list ->
                    if (list.isEmpty()) {
                        binding.recyclerPlaylists.visibility = View.GONE
                        binding.imagePlaceholderPlaylists.visibility = View.VISIBLE
                        binding.textPlaceholderPlaylists.visibility = View.VISIBLE
                    } else {
                        binding.recyclerPlaylists.visibility = View.VISIBLE
                        binding.imagePlaceholderPlaylists.visibility = View.GONE
                        binding.textPlaceholderPlaylists.visibility = View.GONE
                        adapter.submitList(list)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}