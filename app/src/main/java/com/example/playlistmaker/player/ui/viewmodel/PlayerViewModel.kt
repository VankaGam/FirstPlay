package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    initialTrack: Track
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState(track = initialTrack))
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    init {
        interactor.isPlaying
            .onEach { playing ->
                _state.update { it.copy(isPlaying = playing) }
            }
            .launchIn(viewModelScope)

        interactor.position
            .onEach { pos ->
                _state.update { it.copy(position = pos) }
            }
            .launchIn(viewModelScope)

        favoritesInteractor.observeFavorites()
            .map { favs -> favs.any { it.trackId == initialTrack.trackId } }
            .distinctUntilChanged()
            .onEach { isFav ->
                _state.update { it.copy(isFavorite = isFav) }
            }
            .launchIn(viewModelScope)
    }

    fun prepare(track: Track) = interactor.prepare(track)
    fun playPause() = interactor.playPause()
    fun release() = interactor.release()

    override fun onCleared() = interactor.release()

    fun onFavoriteClicked() {
        val current = _state.value.track
        viewModelScope.launch {
            if (_state.value.isFavorite) {
                favoritesInteractor.removeFromFavorites(current)
            } else {
                favoritesInteractor.addToFavorites(current)
            }
            _state.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

}