package de.techmaved.mediabrowserforspotify.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import de.techmaved.mediabrowserforspotify.ui.theme.SpotifyGreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.techmaved.mediabrowserforspotify.R
import de.techmaved.mediabrowserforspotify.activities.MainActivity

class SpotifyDesign {
    val spotifyPackage = "com.spotify.music"

    @Composable
    fun LinkToSpotify(isSpotifyInstalled: Boolean, activity: MainActivity?) {

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    if (activity == null) {
                        return@Button
                    }

                    if (isSpotifyInstalled) {
                        activity.startActivity(
                            activity.packageManager.getLaunchIntentForPackage(spotifyPackage)
                        )
                        return@Button
                    }

                    try {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$spotifyPackage")))
                    } catch (e: ActivityNotFoundException) {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$spotifyPackage")))
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpotifyGreen
                )
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.spotify_icon_white),
                        contentDescription = stringResource(id = R.string.spotify_logo),
                        modifier = Modifier.size(30.dp)
                    )

                    if (isSpotifyInstalled) {
                        val text = stringResource(id = R.string.open_spotify)
                        Text(text = text, color = Color.White)
                    } else {
                        val text = stringResource(id = R.string.get_spotify)
                        Text(text = text, color = Color.White)
                    }
                }
            }
            Divider()
        }
    }
}