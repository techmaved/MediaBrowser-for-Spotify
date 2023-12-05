package de.techmaved.spotifymediabrowser

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import de.techmaved.spotifymediabrowser.models.Model

class SpotifyAuth : Application() {
    lateinit var model: Model

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    companion object {
        lateinit var context: Context
    }
}