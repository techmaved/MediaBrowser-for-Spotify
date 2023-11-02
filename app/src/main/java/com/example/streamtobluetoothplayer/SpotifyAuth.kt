package com.example.streamtobluetoothplayer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.streamtobluetoothplayer.models.Model

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