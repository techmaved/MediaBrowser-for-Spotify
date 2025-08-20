package de.techmaved.mediabrowserforspotify.models.settings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

const val preferredDeviceKey = "preferredDevice"

@Serializable
data class PreferredDevice(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String
)