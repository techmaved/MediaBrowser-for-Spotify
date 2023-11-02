package com.example.streamtobluetoothplayer.models

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.example.streamtobluetoothplayer.SpotifyAuth

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = "",
            redirectUri = "streamtobluetoothplayer://auth",
            applicationContext = SpotifyAuth.context
        )
    }
}