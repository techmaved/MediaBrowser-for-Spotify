package com.example.streamtobluetoothplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE


class SpotifyAuthActivity : ComponentActivity() {
    private val clientId = ""
    private val redirectUri = "http://localhost:8888/callback"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moveTaskToBack(true)

        val builder =
            AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)

        builder.setScopes(
            arrayOf(
                "user-library-read",
                "streaming"
            ))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("token", response.accessToken)
                    startActivity(intent)
                }
                AuthorizationResponse.Type.ERROR -> {}
                else -> {

                }
            }
        }
    }

}