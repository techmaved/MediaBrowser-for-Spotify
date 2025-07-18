package de.techmaved.mediabrowserforspotify.ui.components


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import de.techmaved.mediabrowserforspotify.BuildConfig
import de.techmaved.mediabrowserforspotify.R

@Composable
fun Info() {
    val context = LocalContext.current
    val repoUrl = stringResource(R.string.github_repository)
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl)) }
    val drawable = context.packageManager.getApplicationIcon(context.packageName)

    LibrariesContainer(
        modifier = Modifier.fillMaxWidth(),
        header = {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.app_name))

                        Row {
                            Image(
                                drawable.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(8.dp)
                            )
                        }

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
            }
        }
    )

}
