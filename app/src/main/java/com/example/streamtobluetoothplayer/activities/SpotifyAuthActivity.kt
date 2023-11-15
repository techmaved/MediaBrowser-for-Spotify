package com.example.streamtobluetoothplayer.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.streamtobluetoothplayer.Credentials
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE


class SpotifyAuthActivity : ComponentActivity() {
    private val clientId = Credentials.CLIENT_ID
    private val redirectUri = "http://localhost:8888/callback"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moveTaskToBack(true)

        val builder =
            AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)

        builder.setScopes(
            arrayOf(
                "user-library-read",
                "streaming",
                "playlist-read-private",
            )
        )
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    //val intent = Intent(this, MainActivity::class.java)
                    //intent.putExtra("token", response.accessToken)
                    //startActivity(intent)
                }

                AuthorizationResponse.Type.ERROR -> {}
                else -> {
                    Log.d("error", "error")
                }
            }
        }
    }

}