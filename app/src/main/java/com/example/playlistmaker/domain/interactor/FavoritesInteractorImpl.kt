package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) = repository.add(track)
    override suspend fun removeFromFavorites(track: Track) = repository.remove(track)
    override fun observeFavorites(): Flow<List<Track>> = repository.getAll()
}