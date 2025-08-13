package com.example.playlistmaker.media.data.dp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_tracks")
    suspend fun getAll(): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :id")
    suspend fun deleteById(id: Long)
}