package com.example.playlistmaker.media.data.dp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey val trackId: String,
    val artworkUrl: String?,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseYear: Int?,
    val genre: String?,
    val country: String?,
    val trackTimeMillis: Long,
    val previewUrl: String?
)