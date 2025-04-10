package com.example.playlistmaker.presentation.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val saveTrackToHistoryUseCase: SaveTrackToHistoryUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val tracks = MutableLiveData<List<Track>>()
    val history = MutableLiveData<List<Track>>()
    val isEmptyResult = MutableLiveData<Boolean>()

    // Поиск треков по запросу
    fun search(query: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val result = searchTracksUseCase.execute(query)
                isEmptyResult.value = result.isEmpty()
                tracks.value = result
            } catch (e: Exception) {
                errorMessage.value = "Ошибка: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Загрузка истории
    fun loadHistory() {
        viewModelScope.launch {
            history.value = getSearchHistoryUseCase.execute()
        }
    }

    // Сохранение трека в историю
    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            saveTrackToHistoryUseCase.execute(track)
            loadHistory()
        }
    }

    // Очистка истории
    fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase.execute()
            history.value = emptyList()
        }
    }
}