package com.example.streamtobluetoothplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.streamtobluetoothplayer.ui.theme.StreamToBluetoothPlayerTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import io.github.kaaes.spotify.webapi.core.models.Pager
import io.github.kaaes.spotify.webapi.core.models.SavedTrack
import io.github.kaaes.spotify.webapi.retrofit.v2.Spotify
import io.github.kaaes.spotify.webapi.retrofit.v2.SpotifyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {

    private val clientId = ""
    private val redirectUri = "http://localhost:8888/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var token: String? = null
    private var spotifyService: SpotifyService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = intent.getStringExtra("token").toString()
        spotifyService = Spotify.createAuthenticatedService(token)

        setContent {
            StreamToBluetoothPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            // val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            // val likedSongsURI = "spotify:user:$id:collection"
            // it.playerApi.play(playlistURI)
            // Subscribe to PlayerState

            spotifyService?.mySavedTracks?.enqueue(object : Callback<Pager<SavedTrack>> {
                override fun onResponse(
                    call: Call<Pager<SavedTrack>>,
                    response: Response<Pager<SavedTrack>>
                ) {
                    Log.d("response", response.body()?.items.toString())
                }

                override fun onFailure(call: Call<Pager<SavedTrack>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreamToBluetoothPlayerTheme {
        Greeting("Android")
    }
}