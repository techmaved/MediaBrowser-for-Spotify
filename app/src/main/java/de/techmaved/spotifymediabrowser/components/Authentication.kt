package de.techmaved.spotifymediabrowser.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import de.techmaved.spotifymediabrowser.activities.MainActivity
import de.techmaved.spotifymediabrowser.auth.SpotifyPkceLoginActivityImpl
import de.techmaved.spotifymediabrowser.auth.pkceClassBackTo

class Authentication {
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

}