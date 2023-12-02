package com.example.streamtobluetoothplayer.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.streamtobluetoothplayer.dao.MediaDao
import com.example.streamtobluetoothplayer.entities.MediaItem

@Database(entities = [MediaItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context,AppDatabase::class.java, "stream_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}