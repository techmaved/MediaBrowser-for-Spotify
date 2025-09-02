package de.techmaved.mediabrowserforspotify.utils.database

import android.net.Uri
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromUri(value: Uri): String {
        return value.toString()
    }

    @TypeConverter
    fun toUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun fromDateAdded(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToDateAdded(date: Date?): Long? {
        return date?.time
    }
}