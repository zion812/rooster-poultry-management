package com.rooster.farmerhome.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_basic_info")
data class FarmBasicInfoEntity(
    @PrimaryKey
    val farmId: String,
    val farmName: String,
    val location: String,
    val ownerName: String,
    val activeFlockCount: Int,
    val totalCapacity: Int,
    val lastHealthCheckDate: String?,
    val timestamp: Long // For cache staleness
)

// Mapper functions
fun FarmBasicInfoEntity.toDomain(): com.rooster.farmerhome.domain.model.FarmBasicInfo {
    return com.rooster.farmerhome.domain.model.FarmBasicInfo(
        farmId = farmId,
        farmName = farmName,
        location = location,
        ownerName = ownerName,
        activeFlockCount = activeFlockCount,
        totalCapacity = totalCapacity,
        lastHealthCheckDate = lastHealthCheckDate
    )
}

fun com.rooster.farmerhome.domain.model.FarmBasicInfo.toEntity(): FarmBasicInfoEntity {
    return FarmBasicInfoEntity(
        farmId = farmId,
        farmName = farmName,
        location = location,
        ownerName = ownerName,
        activeFlockCount = activeFlockCount,
        totalCapacity = totalCapacity,
        lastHealthCheckDate = lastHealthCheckDate,
        timestamp = System.currentTimeMillis()
    )
}
