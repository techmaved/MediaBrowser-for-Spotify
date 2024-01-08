package de.techmaved.mediabrowserforspotify.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import de.techmaved.mediabrowserforspotify.BuildConfig
import de.techmaved.mediabrowserforspotify.R

class Info {
    @Composable
    fun getInfo() {
        val context = LocalContext.current
        val repoUrl = stringResource(R.string.github_repository)
        val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl)) }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Version: ${BuildConfig.VERSION_NAME}")
            TextButton(
                onClick = {
                    context.startActivity(intent)
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        asset = FontAwesome.Icon.faw_github,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )
                    Text("source code")
                }
            }
        }
    }
}