package de.techmaved.mediabrowserforspotify.models.settings

import kotlinx.serialization.Serializable

const val preferredDeviceKey = "preferredDevice"

@Serializable
data class PreferredDevice(
    override val value: String,
    override val label: String,
): Setting()