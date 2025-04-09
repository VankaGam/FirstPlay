package com.example.playlistmaker.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {
    fun getFormattedTrackTime(): String {
        val seconds = (trackTimeMillis / 1000) % 60
        val minutes = (trackTimeMillis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}