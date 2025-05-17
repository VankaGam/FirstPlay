package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SaveTrackToHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(track: Track) {
        repository.addTrack(track)
    }
}