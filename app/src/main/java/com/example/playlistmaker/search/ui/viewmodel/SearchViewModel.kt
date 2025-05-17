package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.*
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchTracks: SearchTracksUseCase,
    private val saveToHistory: SaveTrackToHistoryUseCase,
    private val loadHistoryUseCase: GetSearchHistoryUseCase,
    private val clearHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData(SearchState())
    val state: LiveData<SearchState> = _state

    fun search(query: String) {
        viewModelScope.launch {
            _state.value = _state.value!!.copy(
                query = query,
                isLoading = true,
                isError = false,
                isEmpty = false,
                showHistory = false,
                tracks = emptyList()
            )

            try {
                val result = withContext(Dispatchers.IO) { searchTracks(query) }
                _state.value = _state.value!!.copy(
                    isLoading = false,
                    tracks = result,
                    isEmpty = result.isEmpty()
                )
            } catch (e: Exception) {
                _state.value = _state.value!!.copy(
                    isLoading = false,
                    isError = true
                )
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val hist = loadHistoryUseCase.execute()
            _state.postValue(_state.value!!.copy(
                query = "",
                history = hist,
                showHistory = hist.isNotEmpty()
            ))
        }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            clearHistoryUseCase.execute()
            _state.postValue(_state.value!!.copy(
                query = "",
                history = emptyList(),
                showHistory = false
            ))
        }
    }

    fun saveTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            saveToHistory(track)
            val hist = loadHistoryUseCase.execute()
            _state.postValue(_state.value!!.copy(
                history = hist,
                showHistory = hist.isNotEmpty()
            ))
        }
    }
}

