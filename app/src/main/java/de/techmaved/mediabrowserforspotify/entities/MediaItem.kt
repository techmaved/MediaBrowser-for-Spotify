package de.techmaved.mediabrowserforspotify.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "id") val mediaId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "context") val context: String,
    @ColumnInfo(name = "parent") val parent: String,
    @ColumnInfo(name = "browsable") val isBrowsable: Boolean?
)