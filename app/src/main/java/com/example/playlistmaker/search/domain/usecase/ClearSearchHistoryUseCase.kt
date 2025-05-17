package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    fun execute() {
        repository.clearHistory()
    }
}