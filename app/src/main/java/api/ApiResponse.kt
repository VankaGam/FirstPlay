package api

import model.Track

data class ApiResponse(
    val resultCount: Int,
    val results: List<Track>
)
