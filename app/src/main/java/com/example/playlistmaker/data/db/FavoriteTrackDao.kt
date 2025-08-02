package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: FavoriteTrackEntity): Long

    @Delete
    suspend fun removeFromFavorites(track: FavoriteTrackEntity): Int

    @Query("SELECT * FROM favorite_tracks ORDER BY rowid DESC")
    fun getAllFavorites(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    fun getFavoriteIds(): Flow<List<String>>
}