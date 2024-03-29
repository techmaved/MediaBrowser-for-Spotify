package de.techmaved.mediabrowserforspotify.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.techmaved.mediabrowserforspotify.entities.MediaItem

@Dao
interface MediaDao {
    @Query("SELECT * FROM mediaItem")
    fun getAll(): List<MediaItem>

    @Insert
    fun insertAll(mediaItems: List<MediaItem>)

    @Insert
    fun inset(mediaItem: MediaItem)

    @Query("DELETE FROM mediaItem")
    fun deleteAll()
}