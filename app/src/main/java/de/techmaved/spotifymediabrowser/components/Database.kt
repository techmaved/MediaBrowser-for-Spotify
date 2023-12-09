package de.techmaved.spotifymediabrowser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.techmaved.spotifymediabrowser.utils.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Database {
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
}