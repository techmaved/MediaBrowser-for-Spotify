package de.techmaved.spotifymediabrowser.models

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import de.techmaved.spotifymediabrowser.SpotifyAuth
import de.techmaved.spotifymediabrowser.Credentials

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = Credentials.CLIENT_ID,
            redirectUri = "spotifymediabrowser://auth",
            applicationContext = SpotifyAuth.context
        )
    }
}