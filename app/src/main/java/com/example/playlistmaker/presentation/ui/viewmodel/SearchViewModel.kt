package com.example.playlistmaker.presentation.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val saveTrackUseCase: AddToHistoryUseCase,
    private val getHistoryUseCase: GetSearchHistoryUseCase,
    private val clearHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val historyLiveData = MutableLiveData<List<Track>>()  // <-- история

    fun search(query: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            isEmptyResult.value = false
            try {
                val results = searchTracksUseCase(query)
                if (results.isEmpty()) {
                    isEmptyResult.value = true
                } else {
                    tracks.value = results
                }
            } catch (e: Exception) {
                errorMessage.value = "Ошибка: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            history.value = getSearchHistoryUseCase()
        }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            saveTrackToHistoryUseCase(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase()
            history.value = emptyList()
        }
    }
}