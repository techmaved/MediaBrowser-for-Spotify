package de.techmaved.mediabrowserforspotify.models.settings

abstract class Setting {
    abstract val value: String
    abstract val label: String
}

data class DefaultSetting(
    override val value: String,
    override val label: String,
): Setting()