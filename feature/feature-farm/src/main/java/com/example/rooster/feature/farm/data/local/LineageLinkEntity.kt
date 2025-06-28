package com.example.rooster.feature.farm.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// This entity represents a direct parent-child link.
// A composite primary key ensures uniqueness of a specific parent-child-type relationship.
@Entity(
    tableName = "lineage_links",
    primaryKeys = ["childFlockId", "parentFlockId", "relationshipType"],
    foreignKeys = [
        ForeignKey(
            entity = FlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["childFlockId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentFlockId"],
            onDelete = ForeignKey.CASCADE // Or SET_NULL/RESTRICT depending on desired behavior if a parent is deleted
        )
    ],
    indices = [
        Index(value = ["childFlockId"]),
        Index(value = ["parentFlockId"])
    ]
)
data class LineageLinkEntity(
    val childFlockId: String,
    val parentFlockId: String,
    val relationshipType: RelationshipType, // e.g., FATHER, MOTHER
    var needsSync: Boolean = true
)

enum class RelationshipType {
    FATHER,
    MOTHER
}
