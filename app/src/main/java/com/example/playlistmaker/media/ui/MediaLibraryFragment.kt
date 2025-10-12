package com.example.playlistmaker.media.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val vm: MediaLibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by vm.state.collectAsStateWithLifecycle()

                AppTheme {
                    MediaLibraryScreen(
                        state = state,
                        onTabSelect = vm::onTabSelected,
                        onTrackClick = { track: Track ->
                            findNavController().navigate(
                                R.id.playerFragment,
                                Bundle().apply { putSerializable("track", track) }
                            )
                        },
                        onPlaylistClick = { playlistUi ->
                            findNavController().navigate(
                                R.id.action_mediaLibrary_to_playlistWork,
                                bundleOf("playlistId" to playlistUi.id)
                            )
                        },
                        onCreatePlaylistClick = {
                            findNavController().navigate(R.id.action_mediaLibrary_to_createPlaylist)
                        },
                        emptyFavoritesImage = R.drawable.error_search,
                        emptyPlaylistsImage = R.drawable.error_search
                    )
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        vm.reload()
    }
}