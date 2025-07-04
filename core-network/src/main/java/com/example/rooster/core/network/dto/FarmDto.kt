package com.example.rooster.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FarmDto(
    @SerialName("farm_id") val farmId: String,
    @SerialName("name") val name: String,
    @SerialName("location") val location: String,
    @SerialName("owner") val owner: String,
    @SerialName("capacity") val capacity: Int,
    @SerialName("established_date") val establishedDate: String? = null, // YYYY-MM-DD
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class FarmInputDto(
    @SerialName("name") val name: String,
    @SerialName("location") val location: String,
    @SerialName("owner") val owner: String,
    @SerialName("capacity") val capacity: Int,
    @SerialName("established_date") val establishedDate: String? = null, // YYYY-MM-DD
    @SerialName("notes") val notes: String? = null
)
