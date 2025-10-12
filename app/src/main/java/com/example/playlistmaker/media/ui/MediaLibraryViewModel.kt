package com.example.playlistmaker.media.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaLibraryViewModel(
    private val getFavoritesOnce: suspend () -> List<Track>,
    private val getPlaylistsOnce: suspend () -> List<PlaylistUi>,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaLibraryState())
    val state: StateFlow<MediaLibraryState> = _state.asStateFlow()

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun reload() {
        viewModelScope.launch {
            val favDef = async { runCatching { getFavoritesOnce() }.getOrElse { emptyList() } }
            val plsDef = async { runCatching { getPlaylistsOnce() }.getOrElse { emptyList() } }
            _state.update {
                it.copy(
                    favorites = favDef.await(),
                    playlists  = plsDef.await()
                )
            }
        }
    }
}