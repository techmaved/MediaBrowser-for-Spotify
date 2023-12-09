package de.techmaved.mediabrowserforspotify.utils

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import de.techmaved.mediabrowserforspotify.activities.MainActivity
import de.techmaved.mediabrowserforspotify.auth.SpotifyPkceLoginActivityImpl
import de.techmaved.mediabrowserforspotify.models.Model
import kotlinx.coroutines.coroutineScope

suspend fun <T> guardValidSpotifyApi(
    alreadyTriedToReauthenticate: Boolean = false,
    block: suspend (api: SpotifyClientApi) -> T
): T? = coroutineScope {
        try {
            val token = Model.credentialStore.spotifyToken
                ?: throw SpotifyException.ReAuthenticationNeededException()
            val usesPkceAuth = token.refreshToken != null
            val api = (if (usesPkceAuth) Model.credentialStore.getSpotifyClientPkceApi()
            else Model.credentialStore.getSpotifyImplicitGrantApi())
                ?: throw SpotifyException.ReAuthenticationNeededException()

            block(api)
        } catch (e: SpotifyException) {
            e.printStackTrace()
            val usesPkceAuth = Model.credentialStore.spotifyToken?.refreshToken != null
            if (usesPkceAuth) {
                val api = Model.credentialStore.getSpotifyClientPkceApi()!!
                if (!alreadyTriedToReauthenticate) {
                    try {
                        api.refreshToken()
                        Model.credentialStore.spotifyToken = api.token
                        block(api)
                    } catch (e: SpotifyException.ReAuthenticationNeededException) {
                        e.printStackTrace()
                        return@coroutineScope guardValidSpotifyApi(
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        return@coroutineScope guardValidSpotifyApi(
                            alreadyTriedToReauthenticate = true,
                            block = block
                        )
                    }
                } else {
                    MainActivity().startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
                    null
                }
            }
            null
        }
}