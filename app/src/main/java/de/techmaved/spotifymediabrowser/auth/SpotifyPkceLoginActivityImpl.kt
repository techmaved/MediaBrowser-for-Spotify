package de.techmaved.spotifymediabrowser.auth

import android.app.Activity
import android.content.Intent
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import de.techmaved.spotifymediabrowser.BuildConfig
import de.techmaved.spotifymediabrowser.SpotifyAuth
import de.techmaved.spotifymediabrowser.activities.MainActivity

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = "spotifymediabrowser://auth"
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyAuth).model
        model.credentialStore.setSpotifyApi(api)
        val classBackTo = pkceClassBackTo ?: MainActivity::class.java
        startActivity(Intent(this, classBackTo))
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
    }
}
