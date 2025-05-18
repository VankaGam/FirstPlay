package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase

class SearchViewModelFactory(
    private val searchTracks: SearchTracksUseCase,
    private val saveToHistory: SaveTrackToHistoryUseCase,
    private val loadHistory: GetSearchHistoryUseCase,
    private val clearHistory: ClearSearchHistoryUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                searchTracks, saveToHistory, loadHistory, clearHistory
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
