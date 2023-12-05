package de.techmaved.spotifymediabrowser.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession

class PlayerActivity : ComponentActivity() {
    var player: ExoPlayer? = null
    var mediaSession: MediaSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player!!).build()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
    }
}