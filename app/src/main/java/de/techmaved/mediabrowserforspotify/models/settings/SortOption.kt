package de.techmaved.mediabrowserforspotify.models.settings

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

const val sortByKey = "sortBy"
const val orderBy = "orderBy"

@Serializable
data class SortOption(
    @SerializedName("value")
    override val value: String,

    @SerializedName("label")
    override val label: String,
): Setting()



@Serializable
data class OrderOption(
    @SerializedName("value")
    override val value: String,

    @SerializedName("label")
    override val label: String,
): Setting()