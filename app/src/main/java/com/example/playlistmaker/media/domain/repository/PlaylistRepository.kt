package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun create(name: String, description: String?, coverPath: String?): Long
    fun observeAll(): Flow<List<Playlist>>
}