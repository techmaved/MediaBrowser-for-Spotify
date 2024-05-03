package de.techmaved.mediabrowserforspotify.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.techmaved.mediabrowserforspotify.entities.Browsable
import de.techmaved.mediabrowserforspotify.entities.BrowsableWithMediaItems

@Dao
interface BrowsableDao {
    @Transaction
    @Query("SELECT * FROM browsable")
    fun getBrowsablesWithMediaItems(): List<BrowsableWithMediaItems>

    @Query("SELECT * FROM browsable")
    fun getAll(): List<Browsable>

    @Insert
    fun insertAll(browsables: List<Browsable>)

    @Insert
    fun inset(browsable: Browsable)

    @Query("DELETE FROM browsable")
    fun deleteAll()

    @Query("SELECT COUNT(uri) FROM browsable")
    fun getCount(): Int
}