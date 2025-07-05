package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.AlertCheckResponseDto
import com.example.rooster.core.network.dto.EnvironmentRecordDto
import com.example.rooster.core.network.dto.EnvironmentRecordInputDto
import com.example.rooster.core.network.dto.FeedConsumptionRecordDto
import com.example.rooster.core.network.dto.FeedConsumptionRecordInputDto
import com.example.rooster.core.network.dto.GrowthRecordDto
import com.example.rooster.core.network.dto.GrowthRecordInputDto
import com.example.rooster.core.network.dto.HealthRecordDto
import com.example.rooster.core.network.dto.HealthRecordInputDto
import com.example.rooster.core.network.dto.ProductionRecordDto
import com.example.rooster.core.network.dto.ProductionRecordInputDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackingApiService {

    // --- Health Records ---
    @POST("flocks/{flock_id}/health")
    suspend fun addHealthRecord(
        @Path("flock_id") flockId: String,
        @Body healthRecordInputDto: HealthRecordInputDto
    ): Response<HealthRecordDto> // Server returns specific type, handled by sealed class deserialization

    @GET("flocks/{flock_id}/health")
    suspend fun getHealthRecordsForFlock(
        @Path("flock_id") flockId: String,
        @Query("record_type") recordType: String? = null // e.g., "Disease Incident", "Vaccination"
    ): Response<List<HealthRecordDto>> // List of potentially mixed health record types

    @GET("health-records/{record_id}")
    suspend fun getHealthRecordById(@Path("record_id") recordId: String): Response<HealthRecordDto>

    @PUT("health-records/{record_id}")
    suspend fun updateHealthRecord(
        @Path("record_id") recordId: String,
        @Body healthRecordInputDto: HealthRecordInputDto // Server will pick relevant fields based on existing record type
    ): Response<HealthRecordDto>

    @DELETE("health-records/{record_id}")
    suspend fun deleteHealthRecord(@Path("record_id") recordId: String): Response<Unit>

    // --- Production Records ---
    @POST("flocks/{flock_id}/production")
    suspend fun addProductionRecord(
        @Path("flock_id") flockId: String,
        @Body productionRecordInputDto: ProductionRecordInputDto
    ): Response<ProductionRecordDto>

    @GET("flocks/{flock_id}/production")
    suspend fun getProductionRecordsForFlock(
        @Path("flock_id") flockId: String,
        @Query("start_date") startDate: String? = null, // YYYY-MM-DD
        @Query("end_date") endDate: String? = null      // YYYY-MM-DD
    ): Response<List<ProductionRecordDto>>

    @GET("production-records/{record_id}")
    suspend fun getProductionRecordById(@Path("record_id") recordId: String): Response<ProductionRecordDto>

    @PUT("production-records/{record_id}")
    suspend fun updateProductionRecord(
        @Path("record_id") recordId: String,
        @Body productionRecordInputDto: ProductionRecordInputDto
    ): Response<ProductionRecordDto>

    @DELETE("production-records/{record_id}")
    suspend fun deleteProductionRecord(@Path("record_id") recordId: String): Response<Unit>

    // --- Feed Consumption Records ---
    @POST("flocks/{flock_id}/feed")
    suspend fun addFeedConsumptionRecord(
        @Path("flock_id") flockId: String,
        @Body feedConsumptionRecordInputDto: FeedConsumptionRecordInputDto
    ): Response<FeedConsumptionRecordDto>

    @GET("flocks/{flock_id}/feed")
    suspend fun getFeedConsumptionRecordsForFlock(
        @Path("flock_id") flockId: String,
        @Query("start_date") startDate: String? = null, // YYYY-MM-DD
        @Query("end_date") endDate: String? = null      // YYYY-MM-DD
    ): Response<List<FeedConsumptionRecordDto>>

    @GET("feed-records/{record_id}")
    suspend fun getFeedConsumptionRecordById(@Path("record_id") recordId: String): Response<FeedConsumptionRecordDto>

    @PUT("feed-records/{record_id}")
    suspend fun updateFeedConsumptionRecord(
        @Path("record_id") recordId: String,
        @Body feedConsumptionRecordInputDto: FeedConsumptionRecordInputDto
    ): Response<FeedConsumptionRecordDto>

    @DELETE("feed-records/{record_id}")
    suspend fun deleteFeedConsumptionRecord(@Path("record_id") recordId: String): Response<Unit>

    // --- Growth Records ---
    @POST("flocks/{flock_id}/growth")
    suspend fun addGrowthRecord(
        @Path("flock_id") flockId: String,
        @Body growthRecordInputDto: GrowthRecordInputDto
    ): Response<GrowthRecordDto>

    @GET("flocks/{flock_id}/growth")
    suspend fun getGrowthRecordsForFlock(
        @Path("flock_id") flockId: String,
        @Query("start_date") startDate: String? = null, // YYYY-MM-DD
        @Query("end_date") endDate: String? = null      // YYYY-MM-DD
    ): Response<List<GrowthRecordDto>>

    @GET("growth-records/{record_id}")
    suspend fun getGrowthRecordById(@Path("record_id") recordId: String): Response<GrowthRecordDto>

    @PUT("growth-records/{record_id}")
    suspend fun updateGrowthRecord(
        @Path("record_id") recordId: String,
        @Body growthRecordInputDto: GrowthRecordInputDto
    ): Response<GrowthRecordDto>

    @DELETE("growth-records/{record_id}")
    suspend fun deleteGrowthRecord(@Path("record_id") recordId: String): Response<Unit>

    // --- Environment Records ---
    @POST("flocks/{flock_id}/environment")
    suspend fun addEnvironmentRecord(
        @Path("flock_id") flockId: String,
        @Body environmentRecordInputDto: EnvironmentRecordInputDto
    ): Response<EnvironmentRecordDto>

    @GET("flocks/{flock_id}/environment")
    suspend fun getEnvironmentRecordsForFlock(
        @Path("flock_id") flockId: String,
        @Query("start_date") startDate: String? = null, // ISO 8601 DateTime
        @Query("end_date") endDate: String? = null      // ISO 8601 DateTime
    ): Response<List<EnvironmentRecordDto>>

    @GET("environment-records/{record_id}")
    suspend fun getEnvironmentRecordById(@Path("record_id") recordId: String): Response<EnvironmentRecordDto>

    @PUT("environment-records/{record_id}")
    suspend fun updateEnvironmentRecord(
        @Path("record_id") recordId: String,
        @Body environmentRecordInputDto: EnvironmentRecordInputDto
    ): Response<EnvironmentRecordDto>

    @DELETE("environment-records/{record_id}")
    suspend fun deleteEnvironmentRecord(@Path("record_id") recordId: String): Response<Unit>

    // --- Alerts ---
    @GET("flocks/{flock_id}/health/alerts/mortality")
    suspend fun checkMortalityAlert(
        @Path("flock_id") flockId: String,
        @Query("period_days") periodDays: Int? = null,
        @Query("threshold_deaths") thresholdDeaths: Int? = null
    ): Response<AlertCheckResponseDto>

    @GET("flocks/{flock_id}/health/alerts/disease")
    suspend fun checkDiseaseAlert(
        @Path("flock_id") flockId: String,
        @Query("disease_name") diseaseName: String,
        @Query("period_days") periodDays: Int? = null,
        @Query("min_incidents") minIncidents: Int? = null
    ): Response<AlertCheckResponseDto>
}
