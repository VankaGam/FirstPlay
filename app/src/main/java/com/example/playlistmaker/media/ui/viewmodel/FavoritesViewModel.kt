package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Loaded(val tracks: List<Track>) : FavoritesState()
}

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    val state: LiveData<FavoritesState> = _state
    val favorites: LiveData<List<Track>> =
        favoritesInteractor.observeFavorites()
            .asLiveData()


    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            favoritesInteractor.removeFromFavorites(track)
        }
    }

    init {
        viewModelScope.launch {
            favoritesInteractor.observeFavorites().collect { list ->
                if (list.isEmpty()) {
                    _state.postValue(FavoritesState.Empty)
                } else {
                    _state.postValue(FavoritesState.Loaded(list))
                }
            }
        }
    }
}