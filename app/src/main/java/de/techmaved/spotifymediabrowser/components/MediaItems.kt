package de.techmaved.spotifymediabrowser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.techmaved.spotifymediabrowser.utils.AppDatabase
import de.techmaved.spotifymediabrowser.utils.MediaItemTree
import de.techmaved.spotifymediabrowser.utils.SpotifyWebApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaItems {
    @Composable
    fun TextWithButtons(countState: MutableState<Int>, isAuthenticated: Boolean) {
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
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Divider()
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
}