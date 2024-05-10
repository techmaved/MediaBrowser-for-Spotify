package de.techmaved.mediabrowserforspotify.dao

import android.net.Uri
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

    @Query("SELECT COUNT(uri) FROM mediaItem")
    fun getCount(): Int

    @Query("DELETE FROM mediaItem WHERE browsableUri = :browsableUri")
    fun deleteBrowsableMediaItems(browsableUri: Uri)

    @Query("SELECT COUNT(uri) FROM mediaItem WHERE browsableUri = :browsableUri")
    fun getCountByBrowsable(browsableUri: Uri): Int
}