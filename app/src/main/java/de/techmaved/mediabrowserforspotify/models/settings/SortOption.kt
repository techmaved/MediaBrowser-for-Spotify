package de.techmaved.mediabrowserforspotify.models.settings

import kotlinx.serialization.Serializable

const val sortByKey = "sortBy"
const val orderBy = "orderBy"

@Serializable
data class SortOption(
    override val value: String,
    override val label: String,
): Setting()



@Serializable
data class OrderOption(
    override val value: String,
    override val label: String,
): Setting()