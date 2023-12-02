package com.example.streamtobluetoothplayer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.adamratzman.spotify.models.Token
import com.example.streamtobluetoothplayer.auth.SpotifyPkceLoginActivityImpl
import com.example.streamtobluetoothplayer.ui.theme.StreamToBluetoothPlayerTheme
import com.example.streamtobluetoothplayer.auth.pkceClassBackTo
import com.example.streamtobluetoothplayer.models.Model
import com.example.streamtobluetoothplayer.utils.AppDatabase
import com.example.streamtobluetoothplayer.utils.MediaItemTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = this
            StreamToBluetoothPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val count = remember { mutableStateOf(0) }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MediaItemsInDatabase(count)
                        TextWithButtons(count, activity, Model.credentialStore.spotifyToken)
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val mediaItemDao = AppDatabase.getDatabase(applicationContext).mediaDao()
            MediaItemTree.buildFromCache(mediaItemDao.getAll())
        }
    }

}

@Composable
fun TextWithButtons(countState: MutableState<Int>, activity: MainActivity? = null, token: Token?) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
                if (mediaItemDao.getAll().isNotEmpty()) {
                    return@launch
                }

                MediaItemTree.initialize()
                mediaItemDao.insertAll(MediaItemTree.toBeSavedMediaItems)
                val mediaItems = mediaItemDao.getAll()
                MediaItemTree.buildFromCache(mediaItems)
                countState.value = mediaItems.count()
            }
        }) {
            Text("Get Songs")
        }
        Text(
            text = "Get data from spotify put it in cache and build media library",
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
                mediaItemDao.deleteAll()
                countState.value = 0
            }
        }) {
            Text("Delete cache")
        }
        Text(
            text = "Click button down below to start spotify authentication for this app",
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            pkceClassBackTo = MainActivity::class.java
            activity?.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
        }) {
            Text("Start Spotify Authentication")
        }
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Authenticated")
            Checkbox(
                checked = token != null,
                onCheckedChange = {  },
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextWithButtonsPreview() {
    StreamToBluetoothPlayerTheme {
        val count = remember { mutableStateOf(0) }
        TextWithButtons(count, token = null)
    }
}

@Composable
fun MediaItemsInDatabase(countState: MutableState<Int>) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
            countState.value = mediaItemDao.getAll().count()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Current count of media items in database:")
        Text(countState.value.toString())
    }
}

@Composable
@Preview(showBackground = true)
fun MediaItemsInDatabasePreview() {
    StreamToBluetoothPlayerTheme {
        val count = remember { mutableStateOf(0) }
        MediaItemsInDatabase(count)
    }
}