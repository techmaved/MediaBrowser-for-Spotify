package de.techmaved.mediabrowserforspotify.models.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val preferredDeviceKey = "preferredDevice"

@Serializable
data class PreferredDevice(
    @SerialName("id")
    override val value: String,

    @SerialName("name")
    override val label: String,
): Setting()