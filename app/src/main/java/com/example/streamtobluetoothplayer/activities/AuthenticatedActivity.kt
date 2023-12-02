package com.example.streamtobluetoothplayer.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.streamtobluetoothplayer.ui.theme.StreamToBluetoothPlayerTheme
import com.example.streamtobluetoothplayer.utils.AppDatabase
import com.example.streamtobluetoothplayer.utils.MediaItemTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticatedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StreamToBluetoothPlayerTheme {
                // A surface container using the 'background' color from the theme
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
/*
@Composable
fun TextWithButtons(countState: MutableState<Int>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                MediaItemTree.initialize()
                val mediaItemDao = AppDatabase.getDatabase(context).mediaDao()
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
    }
}

@Preview(showBackground = true)
@Composable
fun TextWithButtonsPreview() {
    StreamToBluetoothPlayerTheme {
        val count = remember { mutableStateOf(0) }
        TextWithButtons(count)
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
        Text("Current count of media items in database: ${countState.value}")
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

 */