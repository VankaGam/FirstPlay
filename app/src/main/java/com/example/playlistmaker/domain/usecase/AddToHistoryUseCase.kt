package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository


class AddToHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    fun execute(track: Track) {
        repository.saveTrack(track)
    }
}