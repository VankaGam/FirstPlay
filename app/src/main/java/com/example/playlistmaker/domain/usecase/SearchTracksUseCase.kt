package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

class SearchTracksUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}