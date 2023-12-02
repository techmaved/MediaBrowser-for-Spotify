package com.example.streamtobluetoothplayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.streamtobluetoothplayer.entities.MediaItem

@Dao
interface MediaDao {
    @Query("SELECT * FROM mediaitem")
    fun getAll(): List<MediaItem>

    @Insert
    fun insertAll(mediaItems: List<MediaItem>)

    @Query("DELETE FROM mediaitem")
    fun deleteAll()
}