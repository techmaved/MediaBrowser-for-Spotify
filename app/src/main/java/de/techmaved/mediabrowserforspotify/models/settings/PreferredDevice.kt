package de.techmaved.mediabrowserforspotify.models.settings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

const val preferredDeviceKey = "preferredDevice"

@Serializable
data class PreferredDevice(
    @SerializedName("id")
    override val value: String,

    @SerializedName("name")
    override val label: String,
): Setting()