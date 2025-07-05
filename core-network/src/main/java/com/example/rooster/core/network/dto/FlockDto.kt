package com.example.rooster.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlockDto(
    @SerialName("flock_id") val flockId: String,
    @SerialName("farm_id") val farmId: String,
    @SerialName("breed") val breed: String,
    @SerialName("acquisition_date") val acquisitionDate: String, // YYYY-MM-DD
    @SerialName("initial_count") val initialCount: Int,
    @SerialName("current_count") val currentCount: Int,
    @SerialName("source_supplier") val sourceSupplier: String? = null,
    @SerialName("parent_flock_id_male") val parentFlockIdMale: String? = null,
    @SerialName("parent_flock_id_female") val parentFlockIdFemale: String? = null,
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class FlockInputDto(
    @SerialName("breed") val breed: String,
    @SerialName("acquisition_date") val acquisitionDate: String, // YYYY-MM-DD
    @SerialName("initial_count") val initialCount: Int,
    // current_count is managed by server, not part of input for creation
    @SerialName("source_supplier") val sourceSupplier: String? = null,
    @SerialName("parent_flock_id_male") val parentFlockIdMale: String? = null,
    @SerialName("parent_flock_id_female") val parentFlockIdFemale: String? = null,
    @SerialName("notes") val notes: String? = null,
    // For PUT operations, current_count might be updatable directly or via other records.
    // The OpenAPI spec for PUT /flocks/{flock_id} includes current_count as optional.
    @SerialName("current_count") val currentCount: Int? = null
)

@Serializable
data class FlockFamilyTreeNodeDto(
    @SerialName("id") val id: String,
    @SerialName("breed") val breed: String,
    @SerialName("acquisition_date") val acquisitionDate: String, // YYYY-MM-DD
    @SerialName("male_parent") val maleParent: FlockFamilyTreeNodeDto? = null,
    @SerialName("female_parent") val femaleParent: FlockFamilyTreeNodeDto? = null,
    @SerialName("error") val error: String? = null
)
