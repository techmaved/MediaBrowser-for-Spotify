package de.techmaved.spotifymediabrowser.models

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import de.techmaved.spotifymediabrowser.BuildConfig
import de.techmaved.spotifymediabrowser.SpotifyAuth

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            redirectUri = "spotifymediabrowser://auth",
            applicationContext = SpotifyAuth.context
        )
    }
}