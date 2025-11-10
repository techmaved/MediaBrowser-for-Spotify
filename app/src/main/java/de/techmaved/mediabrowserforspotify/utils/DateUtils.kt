package de.techmaved.mediabrowserforspotify.utils

import com.adamratzman.spotify.models.ReleaseDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getDate(date: String?): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    try {
        if (date != null) {
            return formatter.parse(date)
        }
    } catch (e: ParseException) {

    }

    return null;
}

fun releaseDateToDate(date: ReleaseDate?): Date? {
    if (date == null || date.month == null || date.day == null) {
        return null
    }

    val dateString = "${date.year}-${date.month}-${date.day}T00:00:00Z"

    return getDate(dateString)
}