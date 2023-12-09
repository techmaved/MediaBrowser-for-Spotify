package de.techmaved.mediabrowserforspotify.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.techmaved.mediabrowserforspotify.dao.MediaDao
import de.techmaved.mediabrowserforspotify.entities.MediaItem

@Database(entities = [MediaItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, AppDatabase::class.java, "stream_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}