package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.network.TrackDto
import com.example.playlistmaker.search.domain.model.Track

fun TrackDto.toDomain(): Track? {
    if (
        trackId == null ||
        trackName == null ||
        artistName == null ||
        artworkUrl100 == null ||
        previewUrl == null
    ) return null

    return Track(
        trackId = trackId,
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