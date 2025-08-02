package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.network.ApiService
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val apiService: ApiService
) : TrackRepository {
    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        try {
            val response = apiService.search(query)
            val tracks = response.results.mapNotNull { it.toDomain() }
            emit(tracks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}
