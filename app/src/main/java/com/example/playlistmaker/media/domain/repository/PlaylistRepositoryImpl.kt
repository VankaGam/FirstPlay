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
    private val playlistDao: PlaylistDao,
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
        playlistTrackDao.insert(track.toPlaylistTrackEntity())

        return true
    }

    override suspend fun getById(id: Long): Playlist? =
        playlistDao.getById(id)?.toDomain(gson)

    override suspend fun getTracksByIds(ids: List<Long>): List<Track> {
        if (ids.isEmpty()) return emptyList()
        val entities = playlistTrackDao.getByIds(ids)
        val byId = entities.associateBy { it.trackId }
        return ids.asReversed().mapNotNull { byId[it]?.toDomain() }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        val entity = playlistDao.getById(playlistId) ?: return

        // распарсим список id из JSON
        val type = object : TypeToken<List<Long>>() {}.type
        val ids = (gson.fromJson<List<Long>>(entity.trackIdsJson, type) ?: emptyList()).toMutableList()

        // если этот id есть — удаляем и обновляем плейлист
        if (ids.remove(trackId)) {
            playlistDao.update(entity.copy(
                trackIdsJson = gson.toJson(ids),
                trackCount = ids.size
            ))

            // проверим, используется ли этот трек в каких-то других плейлистах
            val allPlaylists = playlistDao.getAll()
            val usedSomewhere = allPlaylists.any { pl ->
                val list = gson.fromJson<List<Long>>(pl.trackIdsJson, type) ?: emptyList()
                list.contains(trackId)
            }
            if (!usedSomewhere) {
                playlistTrackDao.deleteById(trackId)
            }
        }
    }

    override suspend fun deletePlaylist(id: Long) {
        // удаляем сам плейлист
        playlistDao.deleteById(id)

        // соберём множество id, которые ещё используются в оставшихся плейлистах
        val type = object : TypeToken<List<Long>>() {}.type
        val used = playlistDao.getAll().flatMap { e ->
            gson.fromJson<List<Long>>(e.trackIdsJson, type) ?: emptyList()
        }.toSet()

        // удалим записи треков, которые больше нигде не встречаются
        playlistTrackDao.getAll().forEach { if (it.trackId !in used) playlistTrackDao.deleteById(it.trackId) }
    }

}