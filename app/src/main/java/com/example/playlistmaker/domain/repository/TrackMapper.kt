package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

fun TrackDto.toDomain(): Track? {
    if (trackName == null || artistName == null || artworkUrl100 == null || previewUrl == null) {
        return null
    }
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis ?: 0,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}