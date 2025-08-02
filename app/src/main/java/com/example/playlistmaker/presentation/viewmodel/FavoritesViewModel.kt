package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favoritesState = MutableLiveData<List<Track>>()
    val favoritesState: LiveData<List<Track>> = _favoritesState

    init {
        viewModelScope.launch {
            favoritesInteractor.observeFavorites().collectLatest { list ->
                _favoritesState.postValue(list)
            }
        }
    }
}