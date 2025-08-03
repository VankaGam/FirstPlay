package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    initialTrack: Track
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()
    private val _track = MutableLiveData<Track>(initialTrack)
    val track: LiveData<Track> = _track

    init {
        interactor.isPlaying
            .onEach { playing -> updateState(isPlaying = playing) }
            .launchIn(viewModelScope)

        interactor.position
            .onEach { pos -> updateState(position = pos) }
            .launchIn(viewModelScope)
    }

    fun prepare(track: Track) = interactor.prepare(track)
    fun playPause() = interactor.playPause()
    fun release() = interactor.release()

    override fun onCleared() = interactor.release()

    private fun updateState(
        isPlaying: Boolean = _state.value.isPlaying,
        position: Int = _state.value.position
    ) {
        _state.value = PlayerState(isPlaying, position)
    }
    fun onFavoriteClicked() {
        val current = _track.value ?: return
        viewModelScope.launch {
            if (current.isFavorite) {
                favoritesInteractor.removeFromFavorites(current)
                current.isFavorite = false
            } else {
                favoritesInteractor.addToFavorites(current)
                current.isFavorite = true
            }
            current.isFavorite = !current.isFavorite
            _track.postValue(current)
        }
    }

}