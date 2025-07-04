package com.rooster.farmerhome.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rooster.farmerhome.data.local.model.FarmBasicInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmBasicInfoDao {

    @Query("SELECT * FROM farm_basic_info WHERE farmId = :farmId")
    fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmBasicInfo(farmInfo: FarmBasicInfoEntity)

    @Query("DELETE FROM farm_basic_info WHERE farmId = :farmId")
    suspend fun deleteFarmBasicInfo(farmId: String)
}
