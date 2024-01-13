package de.techmaved.mediabrowserforspotify.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import de.techmaved.mediabrowserforspotify.R
import de.techmaved.mediabrowserforspotify.activities.MainActivity
import de.techmaved.mediabrowserforspotify.auth.SpotifyPkceLoginActivityImpl
import de.techmaved.mediabrowserforspotify.auth.pkceClassBackTo
import de.techmaved.mediabrowserforspotify.utils.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithContainer(
    activity: MainActivity?,
    isAuthenticated: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val store = Store(context)
    val userName = store.getUserName.collectAsState(initial = "")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        if (!isAuthenticated) {
                            Text(
                                text = stringResource(id = R.string.app_name)
                            )

                            return@TopAppBar
                        }

                        Text(
                            text = "Welcome ${userName.value}"
                        )
                    },
                    navigationIcon = {

                    },
                    actions = {
                        IconButton(onClick = {
                            pkceClassBackTo = MainActivity::class.java
                            activity?.startSpotifyClientPkceLoginActivity(
                                SpotifyPkceLoginActivityImpl::class.java)
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.AccountCircle,
                                contentDescription = "Login with Spotify"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                content()
            }
        }
    }
}