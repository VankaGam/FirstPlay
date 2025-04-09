package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}