package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository

class SearchTracksUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}