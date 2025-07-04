package com.rooster.farmerhome.data.local.datasource

import com.rooster.farmerhome.data.local.dao.FarmBasicInfoDao
import com.rooster.farmerhome.data.local.model.FarmBasicInfoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmBasicInfoLocalDataSourceImpl @Inject constructor(
    private val farmBasicInfoDao: FarmBasicInfoDao
) : FarmBasicInfoLocalDataSource {

    override fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfoEntity?> {
        return farmBasicInfoDao.getFarmBasicInfo(farmId)
    }

    override suspend fun insertFarmBasicInfo(farmInfo: FarmBasicInfoEntity) {
        farmBasicInfoDao.insertFarmBasicInfo(farmInfo)
    }

    override suspend fun deleteFarmBasicInfo(farmId: String) {
        farmBasicInfoDao.deleteFarmBasicInfo(farmId)
    }
}
