package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(
    private val sharedPreferences: SharedPreferences
) {
    private val gson = Gson()
    private val key = "history"

    fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > 10) history.removeLast()
        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<Track>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(key, json).apply()
    }
}
