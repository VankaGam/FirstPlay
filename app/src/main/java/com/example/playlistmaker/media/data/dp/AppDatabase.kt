package com.example.playlistmaker.media.data.dp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteTrackEntity::class,
    PlaylistEntity::class, PlaylistTrackEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}