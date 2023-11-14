package com.example.streamtobluetoothplayer.models

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.example.streamtobluetoothplayer.SpotifyAuth
import com.example.streamtobluetoothplayer.Credentials

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = Credentials.CLIENT_ID,
            redirectUri = "streamtobluetoothplayer://auth",
            applicationContext = SpotifyAuth.context
        )
    }
}