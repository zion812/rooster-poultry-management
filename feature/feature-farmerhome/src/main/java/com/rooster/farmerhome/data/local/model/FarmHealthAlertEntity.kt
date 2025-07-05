package com.rooster.farmerhome.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rooster.farmerhome.domain.model.AlertSeverity

@Entity(
    tableName = "farm_health_alert",
    indices = [Index(value = ["farmId"])] // Index for querying by farmId
)
data class FarmHealthAlertEntity(
    @PrimaryKey
    val id: String,
    val flockId: String?,
    val farmId: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity, // Room can store enums as strings
    val timestamp: Long,
    val recommendedAction: String?,
    @ColumnInfo(defaultValue = "0") // SQLite uses 0 for false, 1 for true by default for BOOLEAN
    val isRead: Boolean = false,
    val cacheTimestamp: Long // For cache staleness of the list itself, or individual item fetch time
)

// Mapper functions
fun FarmHealthAlertEntity.toDomain(): com.rooster.farmerhome.domain.model.FarmHealthAlert {
    return com.rooster.farmerhome.domain.model.FarmHealthAlert(
        id = id,
        flockId = flockId,
        farmId = farmId,
        title = title,
        description = description,
        severity = severity,
        timestamp = timestamp,
        recommendedAction = recommendedAction,
        isRead = isRead
    )
}

fun com.rooster.farmerhome.domain.model.FarmHealthAlert.toEntity(): FarmHealthAlertEntity {
    return FarmHealthAlertEntity(
        id = id,
        flockId = flockId,
        farmId = farmId,
        title = title,
        description = description,
        severity = severity,
        timestamp = timestamp,
        recommendedAction = recommendedAction,
        isRead = isRead,
        cacheTimestamp = System.currentTimeMillis()
    )
}
