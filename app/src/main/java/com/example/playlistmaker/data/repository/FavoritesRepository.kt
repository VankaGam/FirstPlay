package com.example.playlistmaker.data.repository

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun add(track: Track)
    suspend fun remove(track: Track)
    fun getAll(): Flow<List<Track>>
}