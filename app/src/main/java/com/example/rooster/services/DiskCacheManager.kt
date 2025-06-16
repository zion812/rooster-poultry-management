// use context7
package com.example.rooster.services

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Layer 2 disk cache manager with SQLite backend
 * Provides offline persistence for cached data
 */
@Singleton
class DiskCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val database: CacheDatabase by lazy {
        Room.databaseBuilder(
            context,
            CacheDatabase::class.java,
            "cache_database"
        ).build()
    }
    
    /**
     * Store data in disk cache with TTL
     */
    suspend fun <T> put(key: String, data: T, ttlMinutes: Long) = withContext(Dispatchers.IO) {
        try {
            val serializedData = data.toString() // Simple serialization for now
            val expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(ttlMinutes)
            
            val cacheEntry = DiskCacheEntry(
                key = key,
                data = serializedData,
                expiryTime = expiryTime,
                createdAt = System.currentTimeMillis()
            )
            
            database.cacheDao().insertOrUpdate(cacheEntry)
        } catch (e: Exception) {
            // Silent fail for caching - don't impact user experience
        }
    }
    
    /**
     * Retrieve data from disk cache (simplified for now)
     */
    suspend fun <T> get(key: String): T? = withContext(Dispatchers.IO) {
        try {
            val entry = database.cacheDao().get(key)
            if (entry != null && entry.expiryTime > System.currentTimeMillis()) {
                // For now, return null - proper deserialization would need more complex setup
                null
            } else {
                // Clean up expired entry
                entry?.let { database.cacheDao().delete(it) }
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if key exists in cache
     */
    suspend fun exists(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val entry = database.cacheDao().get(key)
            entry != null && entry.expiryTime > System.currentTimeMillis()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Remove specific cache entry
     */
    suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        try {
            database.cacheDao().deleteByKey(key)
        } catch (e: Exception) {
            // Silent fail
        }
    }
    
    /**
     * Clear all cached data
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        try {
            database.cacheDao().deleteAll()
        } catch (e: Exception) {
            // Silent fail
        }
    }
    
    /**
     * Clean up expired entries
     */
    suspend fun cleanupExpired() = withContext(Dispatchers.IO) {
        try {
            database.cacheDao().deleteExpired(System.currentTimeMillis())
        } catch (e: Exception) {
            // Silent fail
        }
    }
    
    /**
     * Get cache size in bytes
     */
    suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        try {
            database.cacheDao().getTotalSize()
        } catch (e: Exception) {
            0L
        }
    }
}

/**
 * Room entity for disk cache storage
 */
@Entity(tableName = "disk_cache")
data class DiskCacheEntry(
    @PrimaryKey val key: String,
    val data: String,
    val expiryTime: Long,
    val createdAt: Long
)

/**
 * DAO for cache operations
 */
@Dao
interface CacheDao {
    @Query("SELECT * FROM disk_cache WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): DiskCacheEntry?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DiskCacheEntry)
    
    @Delete
    suspend fun delete(entry: DiskCacheEntry)
    
    @Query("DELETE FROM disk_cache WHERE `key` = :key")
    suspend fun deleteByKey(key: String)
    
    @Query("DELETE FROM disk_cache")
    suspend fun deleteAll()
    
    @Query("DELETE FROM disk_cache WHERE expiryTime < :currentTime")
    suspend fun deleteExpired(currentTime: Long)
    
    @Query("SELECT SUM(LENGTH(data)) FROM disk_cache")
    suspend fun getTotalSize(): Long
}

/**
 * Room database for cache storage
 */
@Database(
    entities = [DiskCacheEntry::class],
    version = 1,
    exportSchema = false
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}
