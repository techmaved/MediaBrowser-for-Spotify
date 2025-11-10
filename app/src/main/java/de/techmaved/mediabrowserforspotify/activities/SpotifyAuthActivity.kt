package de.techmaved.mediabrowserforspotify.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE
import de.techmaved.mediabrowserforspotify.BuildConfig

class SpotifyAuthActivity : ComponentActivity() {
    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "http://127.0.0.1:8888/callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moveTaskToBack(true)

        val builder =
            AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.CODE, redirectUri)

        builder.setScopes(
            arrayOf(
                "streaming",
            )
        )
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                AuthorizationResponse.Type.ERROR -> {}
                else -> {
                    Log.d("error", "error")
                }
            }
        }
    }

}