package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.model.FarmBasicInfoEntity
import kotlinx.coroutines.flow.Flow

interface FarmBasicInfoLocalDataSource {
    fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfoEntity?>
    suspend fun insertFarmBasicInfo(farmInfo: FarmBasicInfoEntity)
    suspend fun deleteFarmBasicInfo(farmId: String)
}
