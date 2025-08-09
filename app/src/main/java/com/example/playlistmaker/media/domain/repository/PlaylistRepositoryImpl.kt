package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.data.dp.PlaylistDao
import com.example.playlistmaker.media.data.dp.PlaylistEntity
import com.example.playlistmaker.media.data.dp.PlaylistTrackDao
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.mapper.toDomain
import com.example.playlistmaker.media.mapper.toPlaylistTrackEntity
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,          // <-- имя выровняли
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson,
) : PlaylistRepository {

    override suspend fun create(name: String, description: String?, coverPath: String?): Long {
        val entity = PlaylistEntity(
            name = name,
            description = description?.ifBlank { null },
            coverPath = coverPath,
            trackIdsJson = "[]",
            trackCount = 0
        )
        return playlistDao.insert(entity)          // <-- обращаемся к playlistDao
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return playlistDao.observeAll().map { list -> list.map { it.toDomain(gson) } }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        val entity = playlistDao.getById(playlistId) ?: return false

        val type = object : TypeToken<List<Long>>() {}.type
        val ids: MutableList<Long> =
            (gson.fromJson(entity.trackIdsJson, type) as? List<Long> ?: emptyList()).toMutableList()

        val trackId = track.trackId.toLong()
        if (ids.contains(trackId)) return false

        // 1) обновляем плейлист в БД
        ids.add(trackId)
        val updated = entity.copy(
            trackIdsJson = gson.toJson(ids),
            trackCount = ids.size
        )
        playlistDao.update(updated)

        // 2) сохраняем сам трек (IGNORE исключит дубль)
        playlistTrackDao.insert(track.toPlaylistTrackEntity())

        return true
    }
}