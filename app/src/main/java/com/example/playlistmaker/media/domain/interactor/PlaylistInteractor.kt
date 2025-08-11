package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistInteractor(
    private val repo: PlaylistRepository,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun create(name: String, description: String?, coverPath: String?): Long =
        withContext(io) { repo.create(name, description, coverPath) }

    fun observeAll(): Flow<List<Playlist>> = repo.observeAll()

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean =
        withContext(io) { repo.addTrackToPlaylist(playlistId, track) }

    suspend fun getById(id: Long): Playlist? = withContext(io) { repo.getById(id) }

    suspend fun getTracksByIds(ids: List<Long>): List<Track> = withContext(io) { repo.getTracksByIds(ids) }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) =
        withContext(io) { repo.removeTrackFromPlaylist(playlistId, trackId) }

    suspend fun deletePlaylist(id: Long) = withContext(io) { repo.deletePlaylist(id) }
}