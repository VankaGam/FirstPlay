package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    fun execute() {
        repository.clearHistory()
    }
}