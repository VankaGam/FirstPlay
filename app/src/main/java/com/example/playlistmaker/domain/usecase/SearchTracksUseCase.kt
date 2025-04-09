package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

class SearchTracksUseCase(private val trackRepository: TrackRepository) {
    suspend fun execute(query: String): List<Track> {
        return trackRepository.searchTracks(query)
    }
}