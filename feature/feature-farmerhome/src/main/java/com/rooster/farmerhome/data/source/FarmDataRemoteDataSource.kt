package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmBasicInfo
import kotlinx.coroutines.flow.Flow

interface FarmDataRemoteDataSource {
    fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfo?>
    // This would interact with an API wrapping the Python FarmRepository
}
