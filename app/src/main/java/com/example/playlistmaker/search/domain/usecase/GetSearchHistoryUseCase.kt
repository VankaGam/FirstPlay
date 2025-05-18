package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    fun execute(): List<Track> {
        return repository.getHistory()
    }
}