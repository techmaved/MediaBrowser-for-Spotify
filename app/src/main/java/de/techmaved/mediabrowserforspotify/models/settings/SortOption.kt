package de.techmaved.mediabrowserforspotify.models.settings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

const val sortByKey = "sortBy"

@Serializable
data class SortOption(
    @SerializedName("value")
    override val value: String,

    @SerializedName("label")
    override val label: String,
): Setting()