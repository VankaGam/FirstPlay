package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    suspend fun addTrack(track: Track)
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}