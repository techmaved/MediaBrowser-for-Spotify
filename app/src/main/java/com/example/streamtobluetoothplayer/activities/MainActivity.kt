package com.example.streamtobluetoothplayer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.example.streamtobluetoothplayer.auth.SpotifyPkceLoginActivityImpl
import com.example.streamtobluetoothplayer.ui.theme.StreamToBluetoothPlayerTheme
import com.example.streamtobluetoothplayer.auth.pkceClassBackTo
import com.example.streamtobluetoothplayer.models.Model
import com.example.streamtobluetoothplayer.utils.AppDatabase
import com.example.streamtobluetoothplayer.utils.MediaItemTree
import com.example.streamtobluetoothplayer.utils.SpotifyWebApiService
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
                    Ui(activity, Model.credentialStore.spotifyToken != null)
                }
            }
        }
    }

}

@Composable()
fun Ui(activity: MainActivity?, isAuthenticated: Boolean) {
    val mediaItemCount = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SpotifyAuthSection(isAuthenticated, activity)
        MediaItemsInDatabase(mediaItemCount)
        TextWithButtons(mediaItemCount, activity, isAuthenticated)
        MirrorSection(isAuthenticated)
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    Ui(null, true)
}

@Composable
fun SpotifyAuthSection(isAuthenticated: Boolean, activity: MainActivity?) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Authenticated")
            Checkbox(
                checked = isAuthenticated,
                onCheckedChange = {  },
                enabled = false
            )
        }

        Text(
            text = "Click button down below to start spotify authentication for this app",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        Button(onClick = {
            pkceClassBackTo = MainActivity::class.java
            activity?.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
        }) {
            Text("Start Spotify Authentication")
        }
        Divider()
    }
}


@Composable
fun TextWithButtons(countState: MutableState<Int>, activity: MainActivity? = null, isAuthenticated: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Get data from spotify put it in cache and build media library",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        var loading by remember { mutableStateOf(false) }
        var isGetSongsButtonEnabled by remember { mutableStateOf(true) }
        isGetSongsButtonEnabled = countState.value == 0

        val scope = rememberCoroutineScope()

        if (isAuthenticated) {
            Button(onClick = {
                loading = true
                isGetSongsButtonEnabled = false

                scope.launch {
                    MediaItemTree.initialize()
                    MediaItemTree.populateMediaTree()

                    CoroutineScope(Dispatchers.IO).launch {
                        val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()

                        mediaItemDao.insertAll(MediaItemTree.toBeSavedMediaItems)
                        val mediaItems = mediaItemDao.getAll()
                        MediaItemTree.buildFromCache(mediaItems)
                        countState.value = mediaItems.count()
                    }
                    loading = false
                }
            }, enabled = isGetSongsButtonEnabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            color = MaterialTheme.colorScheme.inversePrimary,
                            strokeWidth = 2.dp
                        )
                    }

                    Text("Get Songs")
                }
            }
        }

        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
                mediaItemDao.deleteAll()
                countState.value = 0
                isGetSongsButtonEnabled = true
            }
        }, enabled = countState.value > 0) {
            Text("Delete cache")
        }
    }
}

@Composable
fun MirrorSection(isAuthenticated: Boolean) {
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (isAuthenticated) {
        Text(
            text = "Create or sync mirror of liked songs",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                loading = true
                scope.launch {
                    SpotifyWebApiService().handleMirror()
                    loading = false
                }
            }, enabled = !loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            color = MaterialTheme.colorScheme.inversePrimary,
                            strokeWidth = 2.dp
                        )
                    }

                    Text("Create/sync mirror")
                }
            }

            LikedSongsHelpDialog()
        }
    }
}

@Composable
fun LikedSongsHelpDialog() {
    val showDialog = remember { mutableStateOf(false) }

    ElevatedButton(
        onClick = { showDialog.value = !showDialog.value }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
            Text("Info")
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Futher explanation") },
            text = { Text("Due to spotify limiting the liked songs in context you need to create a mirror of that" +
                    "when you create or sync this mirror playlist gets created or updated with all you music") },
            confirmButton = {
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            },
        )
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
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Current count of media items in database:")
        Text(countState.value.toString())
        Divider()
    }
}
