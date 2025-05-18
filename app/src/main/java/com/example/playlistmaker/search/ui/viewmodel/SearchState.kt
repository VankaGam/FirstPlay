package com.example.playlistmaker.search.ui.viewmodel

import com.example.playlistmaker.search.domain.model.Track

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val showHistory: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val history: List<Track> = emptyList()
)