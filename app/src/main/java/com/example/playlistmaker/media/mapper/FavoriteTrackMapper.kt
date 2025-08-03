package com.example.playlistmaker.media.mapper

import com.example.playlistmaker.media.data.dp.FavoriteTrackEntity
import com.example.playlistmaker.search.domain.model.Track

class FavoriteTrackMapper {

    fun toEntity(track: Track): FavoriteTrackEntity {
        val year = track.releaseDate
            ?.substringBefore("-")
            ?.toIntOrNull()

        return FavoriteTrackEntity(
            trackId        = track.trackId.toString(),
            artworkUrl     = track.artworkUrl100,
            trackName      = track.trackName,
            artistName     = track.artistName,
            collectionName = track.collectionName,
            releaseYear    = year,
            genre          = track.primaryGenreName,
            country        = track.country,
            trackTimeMillis= track.trackTimeMillis,
            previewUrl     = track.previewUrl
        )
    }

    fun toDomain(entity: FavoriteTrackEntity): Track {
        val releaseDateString = entity.releaseYear?.toString()

        return Track(
            trackId           = entity.trackId.toInt(),
            trackName         = entity.trackName,
            artistName        = entity.artistName,
            trackTimeMillis   = entity.trackTimeMillis,
            artworkUrl100     = entity.artworkUrl ?: "",
            collectionName    = entity.collectionName,
            releaseDate       = releaseDateString,
            primaryGenreName  = entity.genre,
            country           = entity.country,
            previewUrl        = entity.previewUrl,
            isFavorite        = true  // !!!!!!
        )
    }
}