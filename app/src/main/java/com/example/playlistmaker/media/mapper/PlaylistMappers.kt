package com.example.playlistmaker.media.mapper

import com.example.playlistmaker.media.data.dp.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val typeToken = object : TypeToken<List<Long>>() {}.type

fun PlaylistEntity.toDomain(gson: Gson): Playlist {
    val ids: List<Long> = try {
        gson.fromJson(trackIdsJson, typeToken) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    return Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = ids,
        trackCount = trackCount
    )
}

fun Playlist.toEntity(gson: Gson): PlaylistEntity {
    val json = gson.toJson(trackIds)
    return PlaylistEntity(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIdsJson = json,
        trackCount = trackCount
    )
}