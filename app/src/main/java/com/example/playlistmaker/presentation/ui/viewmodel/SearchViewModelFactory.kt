package com.example.playlistmaker.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

class SearchViewModelFactory(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val saveTrackToHistoryUseCase: SaveTrackToHistoryUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            searchTracksUseCase,
            saveTrackToHistoryUseCase,
            getSearchHistoryUseCase,
            clearSearchHistoryUseCase
        ) as T
    }
}

