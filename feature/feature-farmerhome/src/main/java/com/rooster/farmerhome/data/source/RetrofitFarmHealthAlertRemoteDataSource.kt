package com.rooster.farmerhome.data.source

import com.rooster.farmerhome.domain.model.FarmHealthAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RetrofitFarmHealthAlertRemoteDataSource @Inject constructor(
    private val healthAlertApiService: FarmHealthAlertApiService
) : FarmHealthAlertRemoteDataSource {

    override fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>> = flow {
        try {
            val response = healthAlertApiService.getFarmHealthAlerts(farmId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(it)
                } ?: run {
                    // Successful response but empty body, could mean no alerts or an issue
                    emit(emptyList()) // Assuming empty list if body is null for alerts
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                // Throw an exception to be caught by ViewModel's .catch
                throw Exception("API Error fetching health alerts: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            // Rethrow to be handled by the ViewModel
            throw e
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun markAlertAsRead(alertId: String): Result<Unit> {
        // The API requires farmId as well, but the current interface only takes alertId.
        // This is a mismatch. For now, I'll assume a placeholder farmId or that this
        // function might need to be re-thought if a global farmId isn't available here.
        // Let's assume for now we need to pass a farmId.
        // This implies the calling repository/usecase needs to know the farmId for the alert.
        // This is a common scenario if alerts are displayed in a context where farmId is known.
        // If not, the API or this method signature would need adjustment.
        // For this example, I will throw an error to highlight this design consideration.
        // A better approach: the repository calls this with both farmId and alertId.
        // I will modify the FarmHealthAlertRemoteDataSource interface and its mock.

        // This method will be updated after interface modification.
        // For now, returning failure to highlight the need for farmId.
        return Result.failure(NotImplementedError("markAlertAsRead needs farmId. Interface requires update."))
    }

    // New method signature to align with API (assuming this change is propagated)
    suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit> {
        return try {
            val response = healthAlertApiService.markAlertAsRead(farmId, alertId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorMsg = response.body()?.error ?: response.errorBody()?.string() ?: "Failed to mark alert as read"
                Result.failure(Exception("API Error: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
