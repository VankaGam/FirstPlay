package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage
) : SearchHistoryRepository {

    override suspend fun addTrack(track: Track) {
        storage.saveTrack(track)
    }

    override fun saveTrack(track: Track) = storage.saveTrack(track)

    override fun getHistory(): List<Track> = storage.getHistory()

    override fun clearHistory() = storage.clearHistory()
}