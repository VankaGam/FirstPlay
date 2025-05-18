package com.example.playlistmaker.search.data.network

data class ApiResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)