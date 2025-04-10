package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    suspend fun addTrack(track: Track)
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}