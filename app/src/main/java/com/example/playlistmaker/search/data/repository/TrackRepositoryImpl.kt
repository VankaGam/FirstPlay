package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.playlistmaker.search.data.network.TrackDto
import com.example.playlistmaker.search.data.repository.toDomain

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