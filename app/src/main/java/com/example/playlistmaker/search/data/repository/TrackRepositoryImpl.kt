package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.network.ApiService
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(private val apiService: ApiService) : TrackRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.search(query).execute()
                if (response.isSuccessful) {
                    val dtos = response.body()?.results ?: emptyList()
                    dtos.mapNotNull { it.toDomain() }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}