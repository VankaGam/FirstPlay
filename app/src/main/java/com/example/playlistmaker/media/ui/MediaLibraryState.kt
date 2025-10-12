package com.example.playlistmaker.media.ui

import com.example.playlistmaker.search.domain.model.Track

data class MediaLibraryState(
    val selectedTab: Int = 0,
    val favorites: List<Track> = emptyList(),
    val playlists: List<PlaylistUi> = emptyList(),
)

data class PlaylistUi(
    val id: Long,
    val title: String,
    val trackCount: Int,
    val coverUrl: String? = null
)