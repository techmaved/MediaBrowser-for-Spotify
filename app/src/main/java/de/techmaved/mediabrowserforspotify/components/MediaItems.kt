package de.techmaved.mediabrowserforspotify.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.techmaved.mediabrowserforspotify.ui.components.SelectionDialog
import de.techmaved.mediabrowserforspotify.utils.AppDatabase
import de.techmaved.mediabrowserforspotify.utils.MediaItemTree
import de.techmaved.mediabrowserforspotify.utils.SpotifyWebApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                text = "Add data from spotify put it in cache and build media library",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            val getSongsLoadingState = remember { mutableStateOf(false) }
            val getSongsButtonEnabledState = remember { mutableStateOf(true) }
            getSongsButtonEnabledState.value = countState.value == 0
            val scope = rememberCoroutineScope()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GetSongsButton(
                    isAuthenticated = isAuthenticated,
                    loadingState = getSongsLoadingState,
                    getSongsButtonEnabledState = getSongsButtonEnabledState,
                    context = context,
                    scope = scope,
                    countState = countState
                )

                SelectButton(
                    isAuthenticated = isAuthenticated,
                    getSongsLoadingState = getSongsLoadingState
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
                        MediaItemTree.initialize()
                        MediaItemTree.populateMediaTree()

                        CoroutineScope(Dispatchers.IO).launch {
                            val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()

                            mediaItemDao.insertAll(MediaItemTree.toBeSavedMediaItems)
                            val mediaItems = mediaItemDao.getAll()
                            MediaItemTree.buildFromCache(mediaItems)
                            countState.value = mediaItems.count()
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

    @Composable
    fun SelectButton(
        isAuthenticated: Boolean,
        getSongsLoadingState: MutableState<Boolean>
    ) {
        val dialogState = remember { mutableStateOf(false) }

        if (isAuthenticated) {
            OutlinedButton(
                onClick = {
                    dialogState.value = true
                }
            ) {
                Icon(imageVector = Icons.TwoTone.Edit, contentDescription = "Get songs")
            }
        }

        SelectionDialog(
            dialogState = dialogState,
            getSongsLoadingState = getSongsLoadingState
        )
    }
}

    data class ChipItem(
        val id: String,
        val name: String,
        val state: MutableState<Boolean>
    )

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun SelectionDialog(
        dialogState: MutableState<Boolean>,
        getSongsLoadingState: MutableState<Boolean>
    ) {
        val updateRecord = emptyMap<String, Boolean>().toMutableMap()
        val tes = mutableStateOf(emptyMap<String, List<ChipItem>>())

        if (dialogState.value) {
            val parentItemsLoadingState = remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    tes.value = SpotifyWebApiService().getParentItems()
                    parentItemsLoadingState.value = false
                }
            }

            Dialog(
                onDismissRequest = { dialogState.value = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (parentItemsLoadingState.value) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(0.3f),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp
                                )
                            }
                        } else {
                            LazyColumn {
                                tes.value.forEach { (type, chipItems) ->
                                    item {
                                        Text(type)

                                        FlowRow(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            chipItems.forEach {
                                                val selected = it.state
                                                FilterChip(
                                                    onClick = {
                                                        selected.value = !selected.value
                                                        updateRecord[type] = selected.value
                                                    },
                                                    label = {
                                                        Text(it.name)
                                                    },
                                                    selected = selected.value,
                                                    leadingIcon =
                                                    if (selected.value) {
                                                        {
                                                            Icon(
                                                                imageVector = Icons.Filled.Done,
                                                                contentDescription = "Done icon",
                                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                            )
                                                        }
                                                    } else {
                                                        null
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(5.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    dialogState.value = false
                                }
                            ) {
                                Text("Cancel")
                            }

                            TextButton(
                                onClick = {
                                    dialogState.value = false
                                    getSongsLoadingState.value = true
                                    // call stuff
                                }
                            ) {
                                Text("Update selected in cache")
                            }
                        }
                    }
                }
            }
        }
    }
}
