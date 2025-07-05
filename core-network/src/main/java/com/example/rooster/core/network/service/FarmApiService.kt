package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.FarmDto
import com.example.rooster.core.network.dto.FarmAnalyticsDto
import com.example.rooster.core.network.dto.FowlRecordDto
import com.example.rooster.core.network.dto.HealthAlertDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Farm Management API Service
 * Connects to Python Flask backend for farm operations
 */
interface FarmApiService {
    
    // Farm Management Endpoints
    @GET("api/farms")
    suspend fun getFarms(): Response<List<FarmDto>>
    
    @GET("api/farms/{farmId}")
    suspend fun getFarmById(@Path("farmId") farmId: String): Response<FarmDto>
    
    @POST("api/farms")
    suspend fun createFarm(@Body farm: FarmDto): Response<FarmDto>
    
    @PUT("api/farms/{farmId}")
    suspend fun updateFarm(
        @Path("farmId") farmId: String,
        @Body farm: FarmDto
    ): Response<FarmDto>
    
    @DELETE("api/farms/{farmId}")
    suspend fun deleteFarm(@Path("farmId") farmId: String): Response<Unit>
    
    // Farm Analytics Endpoints
    @GET("api/farms/{farmId}/analytics")
    suspend fun getFarmAnalytics(@Path("farmId") farmId: String): Response<FarmAnalyticsDto>
    
    @GET("api/farms/{farmId}/analytics/summary")
    suspend fun getFarmAnalyticsSummary(
        @Path("farmId") farmId: String,
        @Query("period") period: String = "month"
    ): Response<FarmAnalyticsDto>
    
    // Fowl Management Endpoints
    @GET("api/farms/{farmId}/fowl")
    suspend fun getFowlRecords(@Path("farmId") farmId: String): Response<List<FowlRecordDto>>
    
    @GET("api/fowl/{fowlId}")
    suspend fun getFowlById(@Path("fowlId") fowlId: String): Response<FowlRecordDto>
    
    @POST("api/farms/{farmId}/fowl")
    suspend fun addFowlRecord(
        @Path("farmId") farmId: String,
        @Body fowlRecord: FowlRecordDto
    ): Response<FowlRecordDto>
    
    @PUT("api/fowl/{fowlId}")
    suspend fun updateFowlRecord(
        @Path("fowlId") fowlId: String,
        @Body fowlRecord: FowlRecordDto
    ): Response<FowlRecordDto>
    
    @DELETE("api/fowl/{fowlId}")
    suspend fun deleteFowlRecord(@Path("fowlId") fowlId: String): Response<Unit>
    
    // Health Management Endpoints
    @GET("api/farms/{farmId}/health/alerts")
    suspend fun getHealthAlerts(@Path("farmId") farmId: String): Response<List<HealthAlertDto>>
    
    @POST("api/farms/{farmId}/health/alerts")
    suspend fun createHealthAlert(
        @Path("farmId") farmId: String,
        @Body healthAlert: HealthAlertDto
    ): Response<HealthAlertDto>
    
    @PUT("api/health/alerts/{alertId}")
    suspend fun updateHealthAlert(
        @Path("alertId") alertId: String,
        @Body healthAlert: HealthAlertDto
    ): Response<HealthAlertDto>
    
    @DELETE("api/health/alerts/{alertId}")
    suspend fun deleteHealthAlert(@Path("alertId") alertId: String): Response<Unit>
    
    // Batch Operations for Offline Sync
    @POST("api/farms/{farmId}/fowl/batch")
    suspend fun batchCreateFowlRecords(
        @Path("farmId") farmId: String,
        @Body fowlRecords: List<FowlRecordDto>
    ): Response<List<FowlRecordDto>>
    
    @PUT("api/farms/{farmId}/fowl/batch")
    suspend fun batchUpdateFowlRecords(
        @Path("farmId") farmId: String,
        @Body fowlRecords: List<FowlRecordDto>
    ): Response<List<FowlRecordDto>>
    
    // Sync Endpoints for Offline-First Architecture
    @GET("api/farms/{farmId}/sync")
    suspend fun syncFarmData(
        @Path("farmId") farmId: String,
        @Query("lastSync") lastSyncTimestamp: Long
    ): Response<FarmSyncResponseDto>
    
    @POST("api/farms/{farmId}/sync")
    suspend fun uploadPendingChanges(
        @Path("farmId") farmId: String,
        @Body syncData: FarmSyncRequestDto
    ): Response<FarmSyncResponseDto>
}

/**
 * Data classes for sync operations
 */
@kotlinx.serialization.Serializable
data class FarmSyncRequestDto(
    val fowlRecords: List<FowlRecordDto> = emptyList(),
    val healthAlerts: List<HealthAlertDto> = emptyList(),
    val farmUpdates: FarmDto? = null,
    val lastSyncTimestamp: Long
)

@kotlinx.serialization.Serializable
data class FarmSyncResponseDto(
    val fowlRecords: List<FowlRecordDto> = emptyList(),
    val healthAlerts: List<HealthAlertDto> = emptyList(),
    val farmData: FarmDto? = null,
    val serverTimestamp: Long,
    val conflictResolutions: List<ConflictResolutionDto> = emptyList()
)

@kotlinx.serialization.Serializable
data class ConflictResolutionDto(
    val entityType: String, // "fowl", "alert", "farm"
    val entityId: String,
    val resolution: String, // "server_wins", "client_wins", "merged"
    val mergedData: String? = null // JSON string of merged data
)