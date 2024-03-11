package de.techmaved.mediabrowserforspotify.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import de.techmaved.mediabrowserforspotify.utils.AppDatabase
import de.techmaved.mediabrowserforspotify.utils.MediaItemTree
import de.techmaved.mediabrowserforspotify.utils.SpotifyWebApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TextWithButtons(countState: MutableState<Int>, isAuthenticated: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add data from spotify put it in cache and build media library",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        val loadingState = remember { mutableStateOf(false) }
        val getSongsButtonEnabledState = remember { mutableStateOf(true) }
        getSongsButtonEnabledState.value = countState.value == 0
        val scope = rememberCoroutineScope()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GetSongsButton(
                isAuthenticated = isAuthenticated,
                loadingState = loadingState,
                getSongsButtonEnabledState = getSongsButtonEnabledState,
                context = context,
                scope = scope,
                countState = countState
            )

            DeleteCacheButton(
                context = context,
                countState = countState,
                getSongsButtonEnabledState = getSongsButtonEnabledState
            )
        }
    }
}

@Composable
fun GetSongsButton(
    isAuthenticated: Boolean,
    loadingState: MutableState<Boolean>,
    getSongsButtonEnabledState: MutableState<Boolean>,
    context: Context,
    scope: CoroutineScope,
    countState: MutableState<Int>
) {
    if (isAuthenticated) {
        OutlinedButton(
            onClick = {
                loadingState.value = true
                getSongsButtonEnabledState.value = false

                scope.launch {
                    val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()

                    MediaItemTree.initialize()
                    MediaItemTree.populateMediaTree().collect {
                        CoroutineScope(Dispatchers.IO).launch {
                            mediaItemDao.inset(it)
                            countState.value++
                        }
                    }

                    loadingState.value = false
                }
            },
            enabled = getSongsButtonEnabledState.value
        ) {
            if (loadingState.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(imageVector = Icons.TwoTone.Add, contentDescription = "Get songs")
            }
        }
    }
}

@Composable
fun DeleteCacheButton(
    context: Context,
    countState: MutableState<Int>,
    getSongsButtonEnabledState: MutableState<Boolean>
) {
    OutlinedButton(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
                mediaItemDao.deleteAll()
                countState.value = 0
                getSongsButtonEnabledState.value = true
            }
        },
        enabled = countState.value > 0,
    ) {
        Icon(imageVector = Icons.TwoTone.Delete, contentDescription = "Delete cache")
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
            text = {
                Text(
                    "Due to spotify limiting the liked songs in context you need to create a mirror of that" +
                            "when you create or sync this mirror playlist gets created or updated with all you music"
                )
            },
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
