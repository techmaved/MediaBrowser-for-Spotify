package de.techmaved.mediabrowserforspotify.models.settings

import com.google.gson.annotations.SerializedName

abstract class Setting {
    abstract val value: String
    abstract val label: String
}

data class DefaultSetting(
    override val value: String,
    override val label: String,
): Setting()