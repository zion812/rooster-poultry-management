// use context7
package com.example.rooster.services

import com.example.rooster.services.ReactiveDataFetcher
import com.example.rooster.services.SmartCacheManager
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced fowl data fetcher using Phase 2A smart caching and reactive patterns
 * Demonstrates integration of SmartCacheManager and ReactiveDataFetcher
 */
@Singleton
class EnhancedFowlDataFetcher @Inject constructor(
    private val smartCacheManager: SmartCacheManager,
    private val reactiveDataFetcher: ReactiveDataFetcher
) {
    
    companion object {
        private const val FOWL_CACHE_TTL = 60L // 1 hour for fowl data
        private const val HEALTH_CACHE_TTL = 30L // 30 minutes for health records
    }
    
    /**
     * Get reactive fowl list with smart caching
     */
    fun getFowlListReactive(farmerId: String? = null): Flow<List<FowlData>> {
        val cacheKey = if (farmerId != null) "fowl_list_$farmerId" else "fowl_list_all"
        
        return reactiveDataFetcher.getReactiveData(
            key = cacheKey,
            parser = { parseObject -> 
                FowlData(
                    id = parseObject.objectId,
                    name = parseObject.getString("name") ?: "",
                    breed = parseObject.getString("breed") ?: "",
                    age = parseObject.getInt("age"),
                    gender = parseObject.getString("gender") ?: "",
                    healthStatus = parseObject.getString("healthStatus") ?: "Unknown",
                    ownerId = parseObject.getString("ownerId") ?: "",
                    lastHealthCheck = parseObject.getDate("lastHealthCheck"),
                    isVerified = parseObject.getBoolean("isVerified"),
                    imageUrl = parseObject.getParseFile("image")?.url
                )
            },
            networkFetcher = {
                val query = ParseQuery.getQuery<ParseObject>("Fowl")
                farmerId?.let { query.whereEqualTo("ownerId", it) }
                query.whereEqualTo("isActive", true)
                query.orderByDescending("createdAt")
                query.findInBackground { objects, _ -> objects ?: emptyList() }
                query.find() // Synchronous call for demonstration
            }
        )
    }
    
    /**
     * Get single fowl with reactive updates
     */
    fun getFowlReactive(fowlId: String): Flow<FowlData?> {
        return reactiveDataFetcher.getReactiveItem(
            key = "fowl_$fowlId",
            parser = { parseObject ->
                FowlData(
                    id = parseObject.objectId,
                    name = parseObject.getString("name") ?: "",
                    breed = parseObject.getString("breed") ?: "",
                    age = parseObject.getInt("age"),
                    gender = parseObject.getString("gender") ?: "",
                    healthStatus = parseObject.getString("healthStatus") ?: "Unknown",
                    ownerId = parseObject.getString("ownerId") ?: "",
                    lastHealthCheck = parseObject.getDate("lastHealthCheck"),
                    isVerified = parseObject.getBoolean("isVerified"),
                    imageUrl = parseObject.getParseFile("image")?.url
                )
            },
            networkFetcher = {
                val query = ParseQuery.getQuery<ParseObject>("Fowl")
                query.get(fowlId)
            }
        )
    }
    
    /**
     * Get health records with predictive prefetching
     */
    suspend fun getFowlHealthRecords(fowlId: String): List<HealthRecord> {
        return smartCacheManager.getCachedData(
            key = "health_records_$fowlId",
            ttlMinutes = HEALTH_CACHE_TTL
        ) {
            // Network fetch
            val query = ParseQuery.getQuery<ParseObject>("HealthRecord")
            query.whereEqualTo("fowlId", fowlId)
            query.orderByDescending("date")
            
            val results = query.find()
            results.map { healthObject ->
                HealthRecord(
                    id = healthObject.objectId,
                    fowlId = healthObject.getString("fowlId") ?: "",
                    date = healthObject.getDate("date"),
                    type = healthObject.getString("type") ?: "",
                    notes = healthObject.getString("notes") ?: "",
                    veterinarianId = healthObject.getString("veterinarianId"),
                    medications = healthObject.getList<String>("medications") ?: emptyList(),
                    cost = healthObject.getDouble("cost"),
                    followUpRequired = healthObject.getBoolean("followUpRequired")
                )
            }
        }.also {
            // Trigger related data prefetching
            smartCacheManager.prefetchRelatedData("health_records_$fowlId")
        }
    }
    
    /**
     * Get vaccination records with smart caching
     */
    suspend fun getFowlVaccinations(fowlId: String): List<VaccinationRecord> {
        return smartCacheManager.getCachedData(
            key = "vaccinations_$fowlId",
            ttlMinutes = HEALTH_CACHE_TTL
        ) {
            val query = ParseQuery.getQuery<ParseObject>("Vaccination")
            query.whereEqualTo("fowlId", fowlId)
            query.orderByDescending("date")
            
            val results = query.find()
            results.map { vaccinationObject ->
                VaccinationRecord(
                    id = vaccinationObject.objectId,
                    fowlId = vaccinationObject.getString("fowlId") ?: "",
                    vaccineName = vaccinationObject.getString("vaccineName") ?: "",
                    date = vaccinationObject.getDate("date"),
                    nextDueDate = vaccinationObject.getDate("nextDueDate"),
                    administeredBy = vaccinationObject.getString("administeredBy") ?: "",
                    batchNumber = vaccinationObject.getString("batchNumber"),
                    notes = vaccinationObject.getString("notes") ?: "",
                    sideEffects = vaccinationObject.getString("sideEffects"),
                    cost = vaccinationObject.getDouble("cost")
                )
            }
        }
    }
    
    /**
     * Submit fowl data change with offline support
     */
    suspend fun updateFowl(fowlData: FowlData): Flow<SubmissionResult> {
        return reactiveDataFetcher.submitDataChange(
            operation = DataOperation.UPDATE_FOWL,
            data = fowlData,
            networkSubmitter = { data ->
                val query = ParseQuery.getQuery<ParseObject>("Fowl")
                val fowlObject = query.get(data.id)
                
                fowlObject.put("name", data.name)
                fowlObject.put("breed", data.breed)
                fowlObject.put("age", data.age)
                fowlObject.put("gender", data.gender)
                fowlObject.put("healthStatus", data.healthStatus)
                
                fowlObject.save()
                fowlObject
            }
        )
    }
    
    /**
     * Get fowl by breed with caching
     */
    suspend fun getFowlByBreed(breed: String): List<FowlData> {
        return smartCacheManager.getCachedData(
            key = "fowl_breed_$breed",
            ttlMinutes = FOWL_CACHE_TTL
        ) {
            val query = ParseQuery.getQuery<ParseObject>("Fowl")
            query.whereEqualTo("breed", breed)
            query.whereEqualTo("isActive", true)
            query.orderByDescending("createdAt")
            
            val results = query.find()
            results.map { parseObject ->
                FowlData(
                    id = parseObject.objectId,
                    name = parseObject.getString("name") ?: "",
                    breed = parseObject.getString("breed") ?: "",
                    age = parseObject.getInt("age"),
                    gender = parseObject.getString("gender") ?: "",
                    healthStatus = parseObject.getString("healthStatus") ?: "Unknown",
                    ownerId = parseObject.getString("ownerId") ?: "",
                    lastHealthCheck = parseObject.getDate("lastHealthCheck"),
                    isVerified = parseObject.getBoolean("isVerified"),
                    imageUrl = parseObject.getParseFile("image")?.url
                )
            }
        }
    }
    
    /**
     * Get cache statistics for monitoring
     */
    fun getCacheStatistics(): CacheStatistics {
        return smartCacheManager.getCacheStatistics()
    }
    
    /**
     * Enable rural optimization mode
     */
    fun enableRuralMode() {
        smartCacheManager.enableRuralOptimization()
    }
}

/**
 * Enhanced fowl data model with caching support
 */
data class FowlData(
    val id: String,
    val name: String,
    val breed: String,
    val age: Int,
    val gender: String,
    val healthStatus: String,
    val ownerId: String,
    val lastHealthCheck: java.util.Date?,
    val isVerified: Boolean,
    val imageUrl: String?
)

/**
 * Health record data model
 */
data class HealthRecord(
    val id: String,
    val fowlId: String,
    val date: java.util.Date?,
    val type: String,
    val notes: String,
    val veterinarianId: String?,
    val medications: List<String>,
    val cost: Double,
    val followUpRequired: Boolean
)

/**
 * Vaccination record data model  
 */
data class VaccinationRecord(
    val id: String,
    val fowlId: String,
    val vaccineName: String,
    val date: java.util.Date?,
    val nextDueDate: java.util.Date?,
    val administeredBy: String,
    val batchNumber: String?,
    val notes: String,
    val sideEffects: String?,
    val cost: Double
)