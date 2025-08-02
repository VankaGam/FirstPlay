package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    fun setTrack(track: Track) {
        _currentTrack.value = track
    }

    fun onFavoriteClicked() {
        val track = _currentTrack.value ?: return
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.removeFromFavorites(track)
            } else {
                favoritesInteractor.addToFavorites(track)
            }
            track.isFavorite = !track.isFavorite
            _currentTrack.postValue(track)
        }
    }
}