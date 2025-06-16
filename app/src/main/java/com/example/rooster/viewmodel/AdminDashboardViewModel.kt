package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.ActionPriority
import com.example.rooster.DisputeMetrics
import com.example.rooster.RevenueMetrics
import com.example.rooster.VerificationAction
import com.example.rooster.VerificationMetrics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseCloud
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Enhanced AdminDashboardViewModel with real Parse backend integration
 * Implements comprehensive verification, revenue, and dispute management
 * following enterprise-grade patterns and error handling
 *
 * use context7 - Generate enhanced admin dashboard with Parse integration
 */
class AdminDashboardViewModel : ViewModel() {
    // State flows for dashboard data
    private val _verificationMetrics = MutableStateFlow<VerificationMetrics?>(null)
    val verificationMetrics: StateFlow<VerificationMetrics?> = _verificationMetrics.asStateFlow()

    private val _revenueMetrics = MutableStateFlow<RevenueMetrics?>(null)
    val revenueMetrics: StateFlow<RevenueMetrics?> = _revenueMetrics.asStateFlow()

    private val _disputeMetrics = MutableStateFlow<DisputeMetrics?>(null)
    val disputeMetrics: StateFlow<DisputeMetrics?> = _disputeMetrics.asStateFlow()

    private val _pendingActions = MutableStateFlow<List<VerificationAction>>(emptyList())
    val pendingActions: StateFlow<List<VerificationAction>> = _pendingActions.asStateFlow()

    private val _systemHealth = MutableStateFlow<SystemHealthMetrics?>(null)
    val systemHealth: StateFlow<SystemHealthMetrics?> = _systemHealth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Load all dashboard data with comprehensive error handling
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Load all metrics concurrently for better performance
                val verificationDeferred = async { loadVerificationMetrics() }
                val revenueDeferred = async { loadRevenueMetrics() }
                val disputeDeferred = async { loadDisputeMetrics() }
                val actionsDeferred = async { loadPendingActions() }
                val healthDeferred = async { loadSystemHealth() }

                _verificationMetrics.value = verificationDeferred.await()
                _revenueMetrics.value = revenueDeferred.await()
                _disputeMetrics.value = disputeDeferred.await()
                _pendingActions.value = actionsDeferred.await()
                _systemHealth.value = healthDeferred.await()

                FirebaseCrashlytics.getInstance().log("Dashboard data loaded successfully")
            } catch (e: Exception) {
                val errorMsg = "డాష్‌బోర్డ్ డేటా లోడ్ చేయడంలో వైఫల్యం: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh dashboard data
     */
    fun refreshDashboard() {
        loadDashboardData()
    }

    /**
     * Approve a verification request with Parse backend integration
     */
    fun approveVerification(actionId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val params =
                        hashMapOf<String, Any>(
                            "verificationId" to actionId,
                            "action" to "approve",
                            "adminId" to getCurrentAdminId(),
                            "timestamp" to Date(),
                        )

                    // Call Parse Cloud function for verification approval
                    ParseCloud.callFunction<String>("approveVerification", params)
                }

                // Remove from pending actions
                val updatedActions = _pendingActions.value.filter { it.id != actionId }
                _pendingActions.value = updatedActions

                FirebaseCrashlytics.getInstance().log("Verification approved: $actionId")
            } catch (e: ParseException) {
                val errorMsg = "వెరిఫికేషన్ ఆమోదంలో వైఫల్యం: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * Reject a verification request with detailed reason
     */
    fun rejectVerification(
        actionId: String,
        reason: String = "",
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val params =
                        hashMapOf<String, Any>(
                            "verificationId" to actionId,
                            "action" to "reject",
                            "reason" to reason,
                            "adminId" to getCurrentAdminId(),
                            "timestamp" to Date(),
                        )

                    // Call Parse Cloud function for verification rejection
                    ParseCloud.callFunction<String>("rejectVerification", params)
                }

                // Remove from pending actions
                val updatedActions = _pendingActions.value.filter { it.id != actionId }
                _pendingActions.value = updatedActions

                FirebaseCrashlytics.getInstance()
                    .log("Verification rejected: $actionId, reason: $reason")
            } catch (e: ParseException) {
                val errorMsg = "వెరిఫికేషన్ తిరస్కరణలో వైఫల్యం: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * Send reminder for verification with FCM notification
     */
    fun sendReminder(actionId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val params =
                        hashMapOf<String, Any>(
                            "verificationId" to actionId,
                            "adminId" to getCurrentAdminId(),
                            "reminderType" to "verification_pending",
                        )

                    // Call Parse Cloud function to send reminder notification
                    ParseCloud.callFunction<String>("sendVerificationReminder", params)
                }

                FirebaseCrashlytics.getInstance().log("Reminder sent for verification: $actionId")
            } catch (e: ParseException) {
                val errorMsg = "రిమైండర్ పంపడంలో వైఫల్యం: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    // Private helper methods for loading data with Parse integration

    private suspend fun loadVerificationMetrics(): VerificationMetrics {
        return withContext(Dispatchers.IO) {
            try {
                val params =
                    hashMapOf<String, Any>(
                        "metricsType" to "verification",
                        "timeRange" to "current_month",
                    )

                val result =
                    ParseCloud.callFunction<Map<String, Any>>("getVerificationMetrics", params)

                VerificationMetrics(
                    queueSize = (result["queueSize"] as? Number)?.toInt() ?: 0,
                    averageTurnaroundTime =
                        (result["avgTurnaroundTime"] as? Number)?.toDouble()
                            ?: 0.0,
                    failureRate = (result["failureRate"] as? Number)?.toDouble() ?: 0.0,
                    successRate = (result["successRate"] as? Number)?.toDouble() ?: 0.0,
                    pendingDisputes = (result["pendingDisputes"] as? Number)?.toInt() ?: 0,
                )
            } catch (e: ParseException) {
                // Return fallback metrics if Parse call fails
                VerificationMetrics(
                    queueSize = 0,
                    averageTurnaroundTime = 0.0,
                    failureRate = 0.0,
                    successRate = 0.0,
                    pendingDisputes = 0,
                )
            }
        }
    }

    private suspend fun loadRevenueMetrics(): RevenueMetrics {
        return withContext(Dispatchers.IO) {
            try {
                val params =
                    hashMapOf<String, Any>(
                        "metricsType" to "revenue",
                        "includeRegionalBreakdown" to true,
                    )

                val result = ParseCloud.callFunction<Map<String, Any>>("getRevenueMetrics", params)

                @Suppress("UNCHECKED_CAST")
                val regionalData =
                    result["commissionsByRegion"] as? Map<String, Number> ?: emptyMap()
                val commissionsByRegion = regionalData.mapValues { it.value.toDouble() }

                RevenueMetrics(
                    dailyRevenue = (result["dailyRevenue"] as? Number)?.toDouble() ?: 0.0,
                    monthlyRevenue = (result["monthlyRevenue"] as? Number)?.toDouble() ?: 0.0,
                    annualRevenue = (result["annualRevenue"] as? Number)?.toDouble() ?: 0.0,
                    commissionsByRegion = commissionsByRegion,
                )
            } catch (e: ParseException) {
                // Return fallback metrics if Parse call fails
                RevenueMetrics(
                    dailyRevenue = 0.0,
                    monthlyRevenue = 0.0,
                    annualRevenue = 0.0,
                    commissionsByRegion = emptyMap(),
                )
            }
        }
    }

    private suspend fun loadDisputeMetrics(): DisputeMetrics {
        return withContext(Dispatchers.IO) {
            try {
                // Use Parse Cloud functions instead of direct queries to avoid compilation issues
                val params =
                    hashMapOf<String, Any>(
                        "metricsType" to "dispute",
                        "includeResolutionTime" to true,
                    )

                val result = ParseCloud.callFunction<Map<String, Any>>("getDisputeMetrics", params)

                DisputeMetrics(
                    openCases = (result["openCases"] as? Number)?.toInt() ?: 0,
                    averageResolutionTime =
                        (result["avgResolutionTime"] as? Number)?.toDouble()
                            ?: 0.0,
                    highRiskTransactions =
                        (result["highRiskTransactions"] as? Number)?.toInt()
                            ?: 0,
                    flaggedUsers = (result["flaggedUsers"] as? List<String>) ?: emptyList(),
                )
            } catch (e: ParseException) {
                // Return fallback metrics if Parse call fails
                DisputeMetrics(
                    openCases = 0,
                    averageResolutionTime = 0.0,
                    highRiskTransactions = 0,
                    flaggedUsers = emptyList(),
                )
            }
        }
    }

    private suspend fun loadPendingActions(): List<VerificationAction> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("VerificationRequest")
                query.whereEqualTo("status", "pending")
                query.orderByAscending("createdAt")
                val pendingRequests = query.find()

                pendingRequests.mapNotNull { request ->
                    try {
                        val daysPending =
                            ((Date().time - request.createdAt.time) / (1000 * 60 * 60 * 24)).toInt()
                        val priority =
                            when {
                                daysPending > 10 -> ActionPriority.URGENT
                                daysPending > 5 -> ActionPriority.HIGH
                                else -> ActionPriority.NORMAL
                            }

                        VerificationAction(
                            id = request.objectId,
                            userDisplayName =
                                request.getString("userDisplayName")
                                    ?: "అజ్ఞాత వినియోగదారు",
                            verificationType =
                                request.getString("verificationType")
                                    ?: "సాధారణ వెరిఫికేషన్",
                            daysPending = daysPending,
                            priority = priority,
                        )
                    } catch (e: Exception) {
                        null // Skip malformed entries
                    }
                }
            } catch (e: ParseException) {
                emptyList()
            }
        }
    }

    private suspend fun loadSystemHealth(): SystemHealthMetrics {
        return withContext(Dispatchers.IO) {
            try {
                val params =
                    hashMapOf<String, Any>(
                        "includePerformanceMetrics" to true,
                    )

                val result = ParseCloud.callFunction<Map<String, Any>>("getSystemHealth", params)

                SystemHealthMetrics(
                    activeUsers = (result["activeUsers"] as? Number)?.toInt() ?: 0,
                    databaseHealth = (result["databaseHealth"] as? Number)?.toDouble() ?: 100.0,
                    apiResponseTime = (result["avgResponseTime"] as? Number)?.toDouble() ?: 0.0,
                    errorRate = (result["errorRate"] as? Number)?.toDouble() ?: 0.0,
                    storageUsage = (result["storageUsage"] as? Number)?.toDouble() ?: 0.0,
                )
            } catch (e: ParseException) {
                SystemHealthMetrics(
                    activeUsers = 0,
                    databaseHealth = 100.0,
                    apiResponseTime = 0.0,
                    errorRate = 0.0,
                    storageUsage = 0.0,
                )
            }
        }
    }

    private suspend fun getHighRiskTransactionCount(): Int {
        return try {
            val query = ParseQuery.getQuery<ParseObject>("Transaction")
            query.whereGreaterThan("riskScore", 0.7) // High risk threshold
            query.count()
        } catch (e: ParseException) {
            0
        }
    }

    private fun getCurrentAdminId(): String {
        // TODO: Get actual admin ID from current session
        return "current_admin_id"
    }

    /**
     * System health metrics for comprehensive monitoring
     */
    data class SystemHealthMetrics(
        val activeUsers: Int,
        val databaseHealth: Double,
        val apiResponseTime: Double,
        val errorRate: Double,
        val storageUsage: Double,
    )
}
