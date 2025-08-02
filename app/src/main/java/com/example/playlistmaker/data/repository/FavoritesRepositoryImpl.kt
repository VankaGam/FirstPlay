package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.mapper.FavoriteTrackMapper
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
            .map { entities ->
                entities.map { mapper.toDomain(it) }
            }
}
