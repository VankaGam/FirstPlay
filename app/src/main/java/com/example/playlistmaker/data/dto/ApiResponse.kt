package com.example.playlistmaker.data.dto

data class ApiResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)