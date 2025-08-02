package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.search.domain.model.Track

class FavoriteTrackMapper {

    fun toEntity(track: Track): FavoriteTrackEntity = FavoriteTrackEntity(
        trackId = track.trackId.toString(),
        artworkUrl = track.getCoverArtwork(),
        trackName = track.trackName,
        artistName = track.artistName,
        collectionName = track.collectionName,
        releaseYear = track.releaseDate
            ?.substringBefore("-")
            ?.toIntOrNull(),
        genre = track.primaryGenreName,
        country = track.country,
        trackTimeMillis = track.trackTimeMillis,
        previewUrl = track.previewUrl
    )

    fun toDomain(entity: FavoriteTrackEntity): Track = Track(
        trackId = entity.trackId.toInt(),
        trackName = entity.trackName,
        artistName = entity.artistName,
        trackTimeMillis = entity.trackTimeMillis,
        artworkUrl100 = entity.artworkUrl.orEmpty(),
        collectionName = entity.collectionName,
        releaseDate = entity.releaseYear?.toString(),
        primaryGenreName = entity.genre,
        country = entity.country,
        previewUrl = entity.previewUrl
    ).apply {
        isFavorite = true
    }
}
