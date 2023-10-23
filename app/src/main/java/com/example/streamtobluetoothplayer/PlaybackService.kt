package com.example.streamtobluetoothplayer

import android.app.Service
import android.content.Intent
import android.media.session.MediaSession
import android.os.IBinder

class PlaybackService : MediaLibraryService() {
    var mediaLibrarySession: MediaLibrarySession? = null
    var callback: MediaLibrarySession.Callback = object : MediaLibrarySession.Callback {...}

    // If desired, validate the controller before returning the media library session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    // Create your player and media library session in the onCreate lifecycle event
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback).build()
    }

    // Remember to release the player and media library session in onDestroy
    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }
}
