package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun create(name: String, description: String?, coverPath: String?): Long
    fun observeAll(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
    suspend fun getById(id: Long): Playlist?
    suspend fun getTracksByIds(ids: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun deletePlaylist(id: Long)
}