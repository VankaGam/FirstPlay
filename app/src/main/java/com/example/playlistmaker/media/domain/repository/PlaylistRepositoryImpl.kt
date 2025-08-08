package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.data.dp.PlaylistDao
import com.example.playlistmaker.media.data.dp.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.mapper.toDomain
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun create(name: String, description: String?, coverPath: String?): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description?.ifBlank { null },
            coverPath = coverPath,
            trackIdsJson = "[]",
            trackCount = 0
        )
        return dao.insert(entity)
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return dao.observeAll().map { list -> list.map { it.toDomain(gson) } }
    }
}