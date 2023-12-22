package de.techmaved.mediabrowserforspotify.models

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import de.techmaved.mediabrowserforspotify.BuildConfig
import de.techmaved.mediabrowserforspotify.MyApplication

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI,
            applicationContext = MyApplication.context
        )
    }
}