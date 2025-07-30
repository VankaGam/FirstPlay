package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCase(
    private val repository: TrackRepository
) {
    operator fun invoke(query: String): Flow<List<Track>> =
        repository.searchTracks(query)
}