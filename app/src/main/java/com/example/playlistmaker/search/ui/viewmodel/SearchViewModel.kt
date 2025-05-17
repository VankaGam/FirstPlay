package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val saveTrackToHistoryUseCase: SaveTrackToHistoryUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel(){

    var tracks: List<Track> = emptyList()
    var history: List<Track> = emptyList()
    var isLoading = false
    var isEmptyResult = false
    var isError = false

    suspend fun search(query: String) {
        isLoading = true
        isError = false
        isEmptyResult = false
        tracks = emptyList()

        try {
            val result = withContext(Dispatchers.IO) {
                searchTracksUseCase.invoke(query)
            }

            isLoading = false
            if (result.isEmpty()) {
                isEmptyResult = true
            } else {
                tracks = result
            }
        } catch (e: Exception) {
            isLoading = false
            isError = true
        }
    }

    fun loadHistory() {
        history = getSearchHistoryUseCase.execute()
    }

    fun clearHistory() {
        clearSearchHistoryUseCase.execute()
        history = emptyList()
    }

    fun saveTrackToHistory(track: Track, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            saveTrackToHistoryUseCase(track)
            loadHistory()
            onSaved()
        }
    }

    fun saveToHistory(track: Track) {
        viewModelScope.launch {
            saveTrackToHistoryUseCase.invoke(track)
        }
    }
}