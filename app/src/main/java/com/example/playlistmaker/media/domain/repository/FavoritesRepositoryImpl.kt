package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.data.dp.FavoriteTrackDao
import com.example.playlistmaker.media.mapper.FavoriteTrackMapper
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteTrackDao,
    private val mapper: FavoriteTrackMapper
) : FavoritesRepository {

    override suspend fun add(track: Track) {
        dao.addToFavorites(mapper.toEntity(track))
    }

    override suspend fun remove(track: Track) {
        dao.removeFromFavorites(mapper.toEntity(track))
    }

    override fun getAll(): Flow<List<Track>> =
        dao.getAllFavorites()
            .map { list -> list.map { mapper.toDomain(it) } }
}