package de.techmaved.mediabrowserforspotify.entities

import android.net.Uri
import androidx.room.*
import java.util.Date

@Entity(tableName = "mediaItem")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uri") val uri: Uri,
    @ColumnInfo(name = "browsableUri") val browsableUri: Uri,
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "artist") val artist: String,
    @ColumnInfo(name = "album") val album: String? = null,
    @ColumnInfo(name = "date_added") val dateAdded: Date? = null,
    @ColumnInfo(name = "duration", defaultValue = "0") val duration: Int
)

@Entity(tableName = "browsable")
data class Browsable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uri") val uri: Uri,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String
)

data class BrowsableWithMediaItems(
    @Embedded val browsable: Browsable,
    @Relation(
        entity = MediaItem::class,
        parentColumn = "uri",
        entityColumn = "browsableUri"
    )
    val mediaItems: List<MediaItem>
)
