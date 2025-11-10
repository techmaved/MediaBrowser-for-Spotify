package de.techmaved.mediabrowserforspotify.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.techmaved.mediabrowserforspotify.models.Model
import de.techmaved.mediabrowserforspotify.ui.Info
import de.techmaved.mediabrowserforspotify.ui.Main
import de.techmaved.mediabrowserforspotify.ui.Settings
import de.techmaved.mediabrowserforspotify.ui.components.AppBarWithContainer
import de.techmaved.mediabrowserforspotify.ui.components.Info
import de.techmaved.mediabrowserforspotify.ui.components.MediaItemsDatabaseCounter
import de.techmaved.mediabrowserforspotify.ui.components.MirrorSection
import de.techmaved.mediabrowserforspotify.ui.components.Settings
import de.techmaved.mediabrowserforspotify.ui.components.SpotifyDesign
import de.techmaved.mediabrowserforspotify.ui.components.TextWithButtons
import de.techmaved.mediabrowserforspotify.ui.theme.MediaBrowserForSpotifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = this
            MediaBrowserForSpotifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Main
                    ) {
                        composable<Main> {
                            Ui(
                                activity,
                                Model.credentialStore.spotifyToken != null,
                                isPackageInstalled(SpotifyDesign().spotifyPackage, applicationContext.packageManager),
                                navController
                            )
                        }

                        composable<Info> {
                            Info()
                        }

                        composable<Settings> {
                            Settings()
                        }
                    }
                }
            }
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

@Composable()
fun Ui(activity: MainActivity?, isAuthenticated: Boolean, isSpotifyInstalled: Boolean, navController: NavController) {
    val mediaItemCount = remember { mutableStateOf(0) }

    AppBarWithContainer(activity, isAuthenticated, navController) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MediaItemsDatabaseCounter(mediaItemCount)
            TextWithButtons(mediaItemCount, isAuthenticated)
            MirrorSection(isAuthenticated)
            SpotifyDesign().LinkToSpotify(isSpotifyInstalled, activity)
        }
    }
}