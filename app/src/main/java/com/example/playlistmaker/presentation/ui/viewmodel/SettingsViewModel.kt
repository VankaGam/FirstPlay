package com.example.playlistmaker.presentation.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val searchTracksUseCase: SearchTracksUseCase) : ViewModel() {
    val tracksLiveData = MutableLiveData<List<Track>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()


    fun searchTracks(query: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val tracks = searchTracksUseCase.execute(query)
                if (tracks.isEmpty()) {
                    errorMessage.value = "No results found"
                }
                tracksLiveData.value = tracks
            } catch (e: Exception) {
                errorMessage.value = "An error occurred"
            } finally {
                isLoading.value = false
            }
        }
    }
}
