package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmHealthAlert
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Define a simple success response for actions like marking as read
data class GenericApiResponse(val success: Boolean, val message: String?, val error: String?)

interface FarmHealthAlertApiService {

    @GET("farm/health_alerts/{farm_id}") // Matches Flask GET route
    suspend fun getFarmHealthAlerts(
        @Path("farm_id") farmId: String
    ): Response<List<FarmHealthAlert>> // Expecting a list of alerts

    @POST("farm/health_alerts/{farm_id}/{alert_id}/read") // Matches Flask POST route
    suspend fun markAlertAsRead(
        @Path("farm_id") farmId: String,
        @Path("alert_id") alertId: String
    ): Response<GenericApiResponse> // Expecting a generic success/error response
}
