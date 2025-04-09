package com.example.playlistmaker.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

class SearchViewModelFactory(private val searchTracksUseCase: SearchTracksUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchTracksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

