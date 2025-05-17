package com.example.playlistmaker.domain.repository

import android.content.Context
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.domain.model.Track

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {
    private val storage = SearchHistoryStorage(context)

    override fun saveTrack(track: Track) {
        storage.saveTrack(track)
    }

    override fun getHistory(): List<Track> {
        return storage.getHistory()
    }

    override fun clearHistory() {
        storage.clearHistory()
    }

    override suspend fun addTrack(track: Track) {
        saveTrack(track)
    }
}