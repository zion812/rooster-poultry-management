package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.FlockDto
import com.example.rooster.core.network.dto.FlockInputDto
import com.example.rooster.core.network.dto.FlockFamilyTreeNodeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FlockApiService {

    @GET("farms/{farm_id}/flocks")
    suspend fun getFlocksForFarm(
        @Path("farm_id") farmId: String,
        @Query("q") searchQuery: String? = null
    ): Response<List<FlockDto>>

    @POST("farms/{farm_id}/flocks")
    suspend fun createFlock(
        @Path("farm_id") farmId: String,
        @Body flockInputDto: FlockInputDto
    ): Response<FlockDto>

    @GET("flocks/{flock_id}")
    suspend fun getFlockById(@Path("flock_id") flockId: String): Response<FlockDto>

    @PUT("flocks/{flock_id}")
    suspend fun updateFlock(
        @Path("flock_id") flockId: String,
        @Body flockInputDto: FlockInputDto // Includes optional current_count for PUT
    ): Response<FlockDto>

    @DELETE("flocks/{flock_id}")
    suspend fun deleteFlock(@Path("flock_id") flockId: String): Response<Unit>

    @GET("flocks/{flock_id}/family_tree")
    suspend fun getFlockFamilyTree(
        @Path("flock_id") flockId: String,
        @Query("max_depth") maxDepth: Int? = null
    ): Response<FlockFamilyTreeNodeDto>
}
