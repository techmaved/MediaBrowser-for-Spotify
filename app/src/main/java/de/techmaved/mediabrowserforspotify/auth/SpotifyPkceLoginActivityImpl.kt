package de.techmaved.mediabrowserforspotify.auth

import android.app.Activity
import android.content.Intent
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import de.techmaved.mediabrowserforspotify.BuildConfig
import de.techmaved.mediabrowserforspotify.MyApplication
import de.techmaved.mediabrowserforspotify.activities.MainActivity
import de.techmaved.mediabrowserforspotify.utils.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as MyApplication).model
        model.credentialStore.setSpotifyApi(api)

        CoroutineScope(Dispatchers.IO).launch {
            val store = Store(applicationContext)
            store.saveUsername(api.getUserId())
        }

        val classBackTo = pkceClassBackTo ?: MainActivity::class.java
        startActivity(Intent(this, classBackTo))
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
    }
}
