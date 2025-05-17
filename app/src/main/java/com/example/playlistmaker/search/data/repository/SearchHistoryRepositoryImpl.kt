package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryRepository {
    private val storage = SearchHistoryStorage(sharedPreferences)

    override fun saveTrack(track: Track) = storage.saveTrack(track)
    override fun getHistory(): List<Track> = storage.getHistory()
    override fun clearHistory() = storage.clearHistory()
    override suspend fun addTrack(track: Track) = saveTrack(track)
}