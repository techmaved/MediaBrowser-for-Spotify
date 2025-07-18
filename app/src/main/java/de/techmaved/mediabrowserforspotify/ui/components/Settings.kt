package de.techmaved.mediabrowserforspotify.ui.components

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Device
import de.techmaved.mediabrowserforspotify.R
import de.techmaved.mediabrowserforspotify.models.settings.PreferredDevice
import de.techmaved.mediabrowserforspotify.models.settings.preferredDeviceKey
import de.techmaved.mediabrowserforspotify.ui.theme.Typography
import de.techmaved.mediabrowserforspotify.utils.SpotifyWebApiService
import de.techmaved.mediabrowserforspotify.utils.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Settings() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        val (devices, setDevices) = remember { mutableStateOf<List<Device>?>(null) }
        val (preferredDevice, setPreferredDevice) = remember { mutableStateOf<PreferredDevice?>(null) }
        val context = LocalContext.current
        val store = Store(context)
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    setDevices(SpotifyWebApiService().getDevices())

                    store.getSetting<PreferredDevice>(preferredDeviceKey).collect { preferredDevice ->
                        setPreferredDevice(preferredDevice)
                    }
                }
            }
        }

        Column {
            Column(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = stringResource(R.string.settings), fontSize = Typography.headlineLarge.fontSize)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Setting(
                        stringResource(R.string.preferred_device),
                        devices,
                        preferredDevice,
                        setPreferredDevice
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = {
                            val selected = preferredDevice

                            if (selected == null) {
                                return@Button
                            }

                            scope.launch {
                                store.saveSetting(
                                    PreferredDevice(selected.id, selected.name),
                                    preferredDeviceKey
                                )
                            }

                            showToast(context, R.string.saved)
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(_options: List<Device>?, preferredDevice: PreferredDevice?, setPreferredDevice: (PreferredDevice?) -> Unit) {
    if (_options == null) {
        Text(stringResource(R.string.failed_to_fetch))

        return
    }

    val options: List<Device> = _options
    val currentOption = options.find { preferredDevice?.id == it.id }

    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mutableStateOf(currentOption ?: options[0])
    }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                value = selectedItem.name,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.name)
                        }
                    },
                    onClick = {
                        selectedItem = options[index]
                        setPreferredDevice(PreferredDevice(selectedItem.id, selectedItem.name))

                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun Setting(
    setting: String,
    options: List<Device>?,
    preferredDevice: PreferredDevice?,
    setPreferredDevice: (PreferredDevice?) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(setting)
        Dropdown(options, preferredDevice, setPreferredDevice)
    }
}

@Composable
@Preview()
fun Preview() {
    Settings()
}

fun showToast(context: Context, message: Int) {
    if (Build.VERSION.SDK_INT < 31) {
        return
    }

    val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    toast.show()
}