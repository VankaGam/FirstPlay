package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.TrackRepository
import com.example.playlistmaker.domain.repository.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl : TrackRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.search(query).execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    val dtos = body?.results ?: emptyList()
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