package de.techmaved.mediabrowserforspotify.ui.components

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Device
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import de.techmaved.mediabrowserforspotify.R
import de.techmaved.mediabrowserforspotify.models.settings.DefaultSetting
import de.techmaved.mediabrowserforspotify.models.settings.OrderOption
import de.techmaved.mediabrowserforspotify.models.settings.PreferredDevice
import de.techmaved.mediabrowserforspotify.models.settings.Setting
import de.techmaved.mediabrowserforspotify.models.settings.SortOption
import de.techmaved.mediabrowserforspotify.models.settings.orderBy
import de.techmaved.mediabrowserforspotify.models.settings.preferredDeviceKey
import de.techmaved.mediabrowserforspotify.models.settings.sortByKey
import de.techmaved.mediabrowserforspotify.ui.theme.Typography
import de.techmaved.mediabrowserforspotify.utils.SpotifyWebApiService
import de.techmaved.mediabrowserforspotify.utils.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Settings() {
    val context = LocalContext.current
    val store = Store(context)
    val scope = rememberCoroutineScope()

    val (devices, setDevices) = remember { mutableStateOf<List<Device>?>(null) }
    val (preferredDevice, setPreferredDevice) = remember { mutableStateOf<Setting?>(null) }

    val (sorting, setSorting) = remember { mutableStateOf<Setting?>(null) }
    val (order, setOrder) = remember { mutableStateOf(OrderOption("asc", "")) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                    PreferredDeviceSetting(
                        devices,
                        setDevices,
                        preferredDevice,
                        setPreferredDevice,
                        store
                    )

                    SortOptionSetting(
                        sorting,
                        setSorting,
                        order,
                        setOrder,
                        store
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                launch {
                                    val selected = preferredDevice
                                    if (selected == null) {
                                        return@launch
                                    }

                                    store.saveSetting(
                                        PreferredDevice(selected.value, selected.label),
                                        preferredDeviceKey
                                    )
                                }
                                launch {
                                    val selected = sorting
                                    if (selected == null) {
                                        return@launch
                                    }

                                    store.saveSetting(
                                        SortOption(selected.value, selected.label),
                                        sortByKey
                                    )
                                }
                                launch {
                                    store.saveSetting(
                                        order,
                                        orderBy
                                    )
                                }
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

@Composable
fun PreferredDeviceSetting(
    devices: List<Device>?,
    setDevices: (List<Device>?) -> Unit,
    preferredDevice: Setting?,
    setPreferredDevice: (Setting?) -> Unit,
    store: Store
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            withContext(Dispatchers.IO) {
                setDevices(SpotifyWebApiService().getDevices())

                store.getSetting<PreferredDevice>(preferredDeviceKey).take(1)
                    .collect { preferredDevice ->
                        setPreferredDevice(preferredDevice)
                    }
            }
        }
    }

    Setting(
        stringResource(R.string.preferred_device),
        devices?.map { PreferredDevice(it.id!!, it.name) },
        preferredDevice,
        setPreferredDevice
    ) {

    }
}

@Composable
fun SortOptionSetting(
    sorting: Setting?,
    setSorting: (Setting?) -> Unit,
    order: OrderOption,
    setOrder: (OrderOption) -> Unit,
    store: Store
) {
    val scope = rememberCoroutineScope()
    val sortOptions = listOf<Setting>(
        SortOption("title", stringResource(R.string.title)),
        SortOption("artist", stringResource(R.string.artist)),
        SortOption("album", stringResource(R.string.album)),
        SortOption("recently_added", stringResource(R.string.recently_added)),
        SortOption("duration", stringResource(R.string.duration))
    )

    LaunchedEffect(Unit) {
        scope.launch {
            withContext(Dispatchers.IO) {
                store.getSetting<SortOption>(sortByKey).take(1).collect { sortOption ->
                    setSorting(sortOption)
                }

                store.getSetting<OrderOption>(orderBy).take(1).collect { sortOption ->
                    setOrder(sortOption ?: OrderOption("asc", ""))
                }
            }
        }
    }

    Setting(
        stringResource(R.string.sort_by),
        sortOptions,
        sorting,
        setSorting
    ) {
        val (icon, setIcon) = remember { mutableStateOf<FontAwesome.Icon?>(null) }

        if (order.value == "asc") {
            setIcon(FontAwesome.Icon.faw_arrow_up)
        } else {
            setIcon(FontAwesome.Icon.faw_arrow_down)
        }

        IconButton(
            onClick = {
                if (order.value == "asc") {
                    setIcon(FontAwesome.Icon.faw_arrow_down)
                    setOrder(OrderOption("desc", ""))
                } else {
                    setIcon(FontAwesome.Icon.faw_arrow_up)
                    setOrder(OrderOption("asc", ""))
                }

            }
        ) {
            if (icon != null) {
                Image(
                    asset = icon,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    _options: List<Setting>?,
    selectedOption: Setting?,
    setSelectedOption: (Setting?) -> Unit,
    actionContent: @Composable() () -> Unit?
) {
    var options: List<Setting>? = _options

    if (options == null || options.isEmpty()) {
        // TODO: implement failed to fetch when actual failing

        options = listOf(DefaultSetting("", stringResource(R.string.no_selection)))
    }

    val currentOption = options.find { selectedOption?.value == it.value }

    var expanded by remember {
        mutableStateOf(false)
    }
    val selectedItem = remember {
        mutableStateOf(options[0])
    }

    if (currentOption != null) {
        selectedItem.value = currentOption
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .weight(1f),
                value = selectedItem.value.label,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            actionContent()
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
                            Text(item.label)
                        }
                    },
                    onClick = {
                        selectedItem.value = options[index]
                        setSelectedOption(selectedItem.value)

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
    settingKey: String,
    options: List<Setting>?,
    setting: Setting?,
    setSetting: (Setting?) -> Unit,
    content: @Composable() () -> Unit?
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(settingKey)
        Dropdown(options, setting, setSetting) {
            content()
        }
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