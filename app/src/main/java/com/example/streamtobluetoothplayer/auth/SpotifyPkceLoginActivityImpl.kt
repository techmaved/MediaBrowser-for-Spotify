package com.example.streamtobluetoothplayer.auth

import android.app.Activity
import android.content.Intent
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.example.streamtobluetoothplayer.SpotifyAuth
import com.example.streamtobluetoothplayer.activities.AuthenticatedActivity
import com.example.streamtobluetoothplayer.utils.SpotifyWebApiService

internal var pkceClassBackTo: Class<out Activity>? = null

class SpotifyPkceLoginActivityImpl : AbstractSpotifyPkceLoginActivity() {
    override val clientId = ""
    override val redirectUri = "streamtobluetoothplayer://auth"
    override val scopes = SpotifyScope.values().toList()

    override fun onSuccess(api: SpotifyClientApi) {
        val model = (application as SpotifyAuth).model
        model.credentialStore.setSpotifyApi(api)
        SpotifyWebApiService.initialize()
        val classBackTo = pkceClassBackTo ?: AuthenticatedActivity::class.java
        startActivity(Intent(this, classBackTo))
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        pkceClassBackTo = null
    }
}
