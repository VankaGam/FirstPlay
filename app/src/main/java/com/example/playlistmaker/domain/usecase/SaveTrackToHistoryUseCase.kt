package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SaveTrackToHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(track: Track) {
        repository.addTrack(track)
    }
}