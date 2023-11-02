package com.example.streamtobluetoothplayer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.example.streamtobluetoothplayer.auth.SpotifyPkceLoginActivityImpl
import com.example.streamtobluetoothplayer.ui.theme.StreamToBluetoothPlayerTheme
import com.example.streamtobluetoothplayer.models.Model
import com.example.streamtobluetoothplayer.auth.pkceClassBackTo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        pkceClassBackTo = AuthenticatedActivity::class.java
        startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
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