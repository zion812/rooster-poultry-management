package com.example.rooster.feature.farm.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "flocks",
    indices = [
        Index(value = ["ownerId"]),
        Index(value = ["type"]),
        Index(value = ["fatherId"]),
        Index(value = ["motherId"])
    ]
)
data class FlockEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val fatherId: String?,
    val motherId: String?,
    val type: String,
    val name: String,
    val breed: String?,
    val weight: Float?,
    val certified: Boolean,
    val verified: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "mortality_records",
    foreignKeys = [
        ForeignKey(
            entity = FlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["fowlId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["fowlId"])]
)
data class MortalityEntity(
    @PrimaryKey val id: String,
    val fowlId: String,
    val cause: String,
    val description: String?,
    val weight: Float?,
    val photos: String?, // JSON array
    val recordedAt: Long,
    val createdAt: Long
)

@Entity(
    tableName = "vaccination_records",
    foreignKeys = [
        ForeignKey(
            entity = FlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["fowlId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["fowlId"])]
)
data class VaccinationEntity(
    @PrimaryKey val id: String,
    val fowlId: String,
    val vaccineName: String,
    val dosage: String?,
    val veterinarian: String?,
    val nextDueDate: Long?,
    val notes: String?,
    val photos: String?, // JSON array
    val recordedAt: Long,
    val createdAt: Long
)

@Entity(
    tableName = "sensor_data",
    indices = [
        Index(value = ["deviceId"]),
        Index(value = ["timestamp"])
    ]
)
data class SensorDataEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val temperature: Float?,
    val humidity: Float?,
    val airQuality: Float?,
    val lightLevel: Float?,
    val noiseLevel: Float?,
    val timestamp: Long,
    val createdAt: Long
)

@Entity(
    tableName = "update_records",
    foreignKeys = [
        ForeignKey(
            entity = FlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["fowlId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["fowlId"])]
)
data class UpdateEntity(
    @PrimaryKey val id: String,
    val fowlId: String,
    val updateType: String,
    val title: String,
    val description: String,
    val weight: Float?,
    val photos: String?, // JSON array
    val recordedAt: Long,
    val createdAt: Long
)

@Dao
interface FlockDao {
    @Query("SELECT * FROM flocks WHERE id = :id")
    fun getById(id: String): Flow<FlockEntity?>

    @Query("SELECT * FROM flocks WHERE ownerId = :ownerId")
    fun getByOwner(ownerId: String): Flow<List<FlockEntity>>

    @Query("SELECT * FROM flocks WHERE type = :type")
    fun getByType(type: String): Flow<List<FlockEntity>>

    @Query("SELECT * FROM flocks WHERE fatherId = :parentId OR motherId = :parentId")
    fun getOffspring(parentId: String): Flow<List<FlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FlockEntity)

    @Update
    suspend fun update(entity: FlockEntity)

    @Delete
    suspend fun delete(entity: FlockEntity)

    @Query("DELETE FROM flocks WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface MortalityDao {
    @Query("SELECT * FROM mortality_records WHERE fowlId = :fowlId ORDER BY recordedAt DESC")
    fun getMortalityForFowl(fowlId: String): Flow<List<MortalityEntity>>

    @Query("SELECT * FROM mortality_records ORDER BY recordedAt DESC")
    fun getAllMortality(): Flow<List<MortalityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MortalityEntity)

    @Delete
    suspend fun delete(entity: MortalityEntity)

    @Query("DELETE FROM mortality_records WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface VaccinationDao {
    @Query("SELECT * FROM vaccination_records WHERE fowlId = :fowlId ORDER BY recordedAt DESC")
    fun getVaccinationForFowl(fowlId: String): Flow<List<VaccinationEntity>>

    @Query("SELECT * FROM vaccination_records WHERE nextDueDate <= :date")
    fun getUpcomingVaccinations(date: Long): Flow<List<VaccinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VaccinationEntity)

    @Delete
    suspend fun delete(entity: VaccinationEntity)

    @Query("DELETE FROM vaccination_records WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface SensorDataDao {
    @Query("SELECT * FROM sensor_data WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT :limit")
    fun getByDevice(deviceId: String, limit: Int = 100): Flow<List<SensorDataEntity>>

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT :limit")
    fun getAll(limit: Int = 1000): Flow<List<SensorDataEntity>>

    @Query("SELECT * FROM sensor_data WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getByTimeRange(startTime: Long, endTime: Long): Flow<List<SensorDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SensorDataEntity)

    @Query("DELETE FROM sensor_data WHERE timestamp < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Long)
}

@Dao
interface UpdateDao {
    @Query("SELECT * FROM update_records WHERE fowlId = :fowlId ORDER BY recordedAt DESC")
    fun getUpdatesForFowl(fowlId: String): Flow<List<UpdateEntity>>

    @Query("SELECT * FROM update_records ORDER BY recordedAt DESC LIMIT :limit")
    fun getRecentUpdates(limit: Int = 50): Flow<List<UpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UpdateEntity)

    @Delete
    suspend fun delete(entity: UpdateEntity)

    @Query("DELETE FROM update_records WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Database(
    entities = [
        FlockEntity::class,
        MortalityEntity::class,
        VaccinationEntity::class,
        SensorDataEntity::class,
        UpdateEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FarmDatabase : RoomDatabase() {
    abstract fun flockDao(): FlockDao
    abstract fun mortalityDao(): MortalityDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun sensorDataDao(): SensorDataDao
    abstract fun updateDao(): UpdateDao
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
}
