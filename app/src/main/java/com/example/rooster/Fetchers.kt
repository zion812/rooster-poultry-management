package com.example.rooster

import android.content.Context
import android.net.TrafficStats
import com.parse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// Helper function to safely parse dates from String?
private fun parseDate(dateString: String?): Date? =
    dateString?.let {
        try {
            // Attempt to parse ISO 8601 format, common in JSON
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(it)
        } catch (e: ParseException) {
            try {
                // Fallback for simpler date format if needed
                SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(it)
            } catch (e2: ParseException) {
                null // Add other formats or return null if parsing fails
            }
        }
    }

// Fetch notifications/alerts for the current user
fun fetchNotifications(
    onResult: (List<ParseObject>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("Notification")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) onError(e.localizedMessage) else onResult(objects ?: emptyList())
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch bids for a given listing
fun fetchBids(
    listingId: String,
    onResult: (List<ParseObject>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("Bid")
        query.whereEqualTo("listingId", listingId)
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) onError(e.localizedMessage) else onResult(objects ?: emptyList())
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch chat messages for a group or P2P chat
fun fetchChatMessages(
    chatId: String,
    onResult: (List<ParseObject>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("ChatMessage")
        query.whereEqualTo("chatId", chatId)
        query.orderByAscending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) onError(e.localizedMessage) else onResult(objects ?: emptyList())
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch health/medication records for a fowl
fun fetchHealthRecords(
    fowlId: String,
    onResult: (List<ParseObject>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("HealthRecord")
        query.whereEqualTo("fowlId", fowlId)
        query.orderByDescending("date")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) onError(e.localizedMessage) else onResult(objects ?: emptyList())
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch promotions for the Explore or Market screens
fun fetchPromotions(
    onResult: (List<ParseObject>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("Promotion")
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) onError(e.localizedMessage) else onResult(objects ?: emptyList())
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// ===============================
// VET CONSULTATIONS FETCHERS
// ===============================

// Fetch available veterinarians for consultation
fun fetchAvailableVets(
    region: String? = null,
    onResult: (List<VetProfile>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("VetProfile")
        query.whereEqualTo("isAvailable", true)
        query.whereEqualTo("isVerified", true)
        region?.let { query.whereEqualTo("region", it) }
        query.orderByDescending("rating")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val vets =
                    objects?.map { vetObject ->
                        VetProfile(
                            vetId = vetObject.objectId,
                            name = vetObject.getString("name") ?: "Unknown Vet",
                            qualification = vetObject.getString("qualification") ?: "",
                            experience = vetObject.getInt("experienceYears"),
                            specialization = vetObject.getList<String>("specialization") ?: emptyList(),
                            rating = vetObject.getDouble("rating"),
                            consultationFee = vetObject.getDouble("consultationFee"),
                            isAvailable = vetObject.getBoolean("isAvailable"),
                            languages = vetObject.getList<String>("languages") ?: emptyList(),
                            region = vetObject.getString("region") ?: "",
                            profilePhotoUrl = vetObject.getParseFile("profilePhoto")?.url,
                        )
                    } ?: emptyList()
                onResult(vets)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch consultation history for current user
fun fetchConsultationHistory(
    onResult: (List<VetConsultation>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("VetConsultation")
        query.whereEqualTo("farmer", ParseUser.getCurrentUser())
        query.include("vet")
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val consultations =
                    objects?.map { consultObject ->
                        val vetObject = consultObject.getParseObject("vet")
                        VetConsultation(
                            consultationId = consultObject.objectId,
                            farmerId = consultObject.getParseUser("farmer")?.objectId ?: "",
                            vetId = vetObject?.objectId ?: "",
                            vetName = vetObject?.getString("name") ?: "Unknown Vet",
                            type =
                                ConsultationType.valueOf(
                                    consultObject.getString("type") ?: "AI_CHAT",
                                ),
                            status =
                                ConsultationStatus.valueOf(
                                    consultObject.getString("status") ?: "PENDING",
                                ),
                            symptoms = consultObject.getString("symptoms") ?: "",
                            aiResponse = consultObject.getString("aiResponse"),
                            vetNotes = consultObject.getString("vetNotes"),
                            scheduledTime = consultObject.getDate("scheduledTime"),
                            completedTime = consultObject.getDate("completedTime"),
                            cost = consultObject.getDouble("cost"),
                            rating = consultObject.getDouble("rating"),
                            createdAt = consultObject.createdAt ?: Date(),
                        )
                    } ?: emptyList()
                onResult(consultations)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch AI chat responses for symptoms
fun fetchAIHealthTips(
    symptoms: String,
    fowlType: String,
    onResult: (List<AIHealthTip>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        // Call Parse Cloud Function for AI health tips
        val params =
            hashMapOf<String, Any>(
                "symptoms" to symptoms,
                "fowlType" to fowlType,
                "language" to "telugu", // Default to Telugu for rural users
            )

        ParseCloud.callFunctionInBackground<List<Map<String, Any>>>(
            "getAIHealthTips",
            params,
        ) { response, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val healthTips =
                    response?.map { tipMap ->
                        AIHealthTip(
                            tipId = tipMap["tipId"] as? String ?: "",
                            title = tipMap["title"] as? String ?: "",
                            description = tipMap["description"] as? String ?: "",
                            severity =
                                HealthSeverity.valueOf(
                                    tipMap["severity"] as? String ?: "LOW",
                                ),
                            recommendedAction = tipMap["recommendedAction"] as? String ?: "",
                            confidence = (tipMap["confidence"] as? Number)?.toDouble() ?: 0.0,
                            language = tipMap["language"] as? String ?: "telugu",
                            references =
                                (tipMap["references"] as? List<*>)?.mapNotNull { it as? String }
                                    ?: emptyList(),
                        )
                    } ?: emptyList()
                onResult(healthTips)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// ===============================
// PAYMENT INTEGRATION FETCHERS
// ===============================

// Fetch payment history for user
fun fetchPaymentHistory(
    onResult: (List<PaymentTransaction>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("PaymentTransaction")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val transactions =
                    objects?.map { transactionObject ->
                        PaymentTransaction(
                            transactionId = transactionObject.objectId,
                            razorpayPaymentId = transactionObject.getString("razorpayPaymentId"),
                            orderId = transactionObject.getString("orderId") ?: "",
                            amount = transactionObject.getDouble("amount"),
                            currency = transactionObject.getString("currency") ?: "INR",
                            status =
                                TransactionStatus.valueOf(
                                    transactionObject.getString("status") ?: "PENDING",
                                ),
                            method =
                                PaymentMethod.valueOf(
                                    transactionObject.getString("method") ?: "COD",
                                ),
                            description = transactionObject.getString("description") ?: "",
                            listingId = transactionObject.getString("listingId"),
                            sellerId = transactionObject.getString("sellerId"),
                            buyerId = transactionObject.getString("buyerId"),
                            createdAt = transactionObject.createdAt ?: Date(),
                            completedAt = transactionObject.getDate("completedAt"),
                            failureReason = transactionObject.getString("failureReason"),
                        )
                    } ?: emptyList()
                onResult(transactions)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch marketplace revenue analytics for sellers
fun fetchRevenueAnalytics(
    sellerId: String? = null,
    onResult: (RevenueAnalytics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val params = hashMapOf<String, Any>()
        sellerId?.let { params["sellerId"] = it }

        ParseCloud.callFunctionInBackground<Map<String, Any>>(
            "getRevenueAnalytics",
            params,
        ) { response, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val analytics =
                    response?.let { data ->
                        RevenueAnalytics(
                            totalRevenue = (data["totalRevenue"] as? Number)?.toDouble() ?: 0.0,
                            monthlyRevenue = (data["monthlyRevenue"] as? Number)?.toDouble() ?: 0.0,
                            totalTransactions = (data["totalTransactions"] as? Number)?.toInt() ?: 0,
                            successfulTransactions =
                                (data["successfulTransactions"] as? Number)?.toInt()
                                    ?: 0,
                            averageOrderValue =
                                (data["averageOrderValue"] as? Number)?.toDouble()
                                    ?: 0.0,
                            topSellingCategory = data["topSellingCategory"] as? String ?: "",
                            recentTransactions =
                                (data["recentTransactions"] as? List<Map<String, Any>>)?.map { txnMap ->
                                    PaymentTransaction(
                                        transactionId = txnMap["transactionId"] as? String ?: "",
                                        razorpayPaymentId = txnMap["razorpayPaymentId"] as? String,
                                        orderId = txnMap["orderId"] as? String ?: "",
                                        amount = (txnMap["amount"] as? Number)?.toDouble() ?: 0.0,
                                        currency = txnMap["currency"] as? String ?: "INR",
                                        status =
                                            TransactionStatus.valueOf(
                                                txnMap["status"] as? String ?: "PENDING",
                                            ),
                                        method =
                                            PaymentMethod.valueOf(
                                                txnMap["method"] as? String ?: "COD",
                                            ),
                                        description = txnMap["description"] as? String ?: "",
                                        listingId = txnMap["listingId"] as? String,
                                        sellerId = txnMap["sellerId"] as? String,
                                        buyerId = txnMap["buyerId"] as? String,
                                        createdAt =
                                            parseDate(txnMap["createdAt"] as? String)
                                                ?: Date(),
                                        // Default to now if parsing fails
                                        completedAt = parseDate(txnMap["completedAt"] as? String),
                                        failureReason = txnMap["failureReason"] as? String,
                                    )
                                } ?: emptyList(),
                        )
                    } ?: RevenueAnalytics()
                onResult(analytics)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// ===============================
// EDUCATIONAL CONTENT FETCHERS
// ===============================

// Fetch educational resources created by veterinarians
fun fetchEducationalResources(
    category: String? = null,
    language: String = "telugu",
    onResult: (List<EducationalResource>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("EducationalResource")
        query.whereEqualTo("isApproved", true)
        category?.let { query.whereEqualTo("category", it) }
        query.whereEqualTo("language", language)
        query.include("author")
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val resources =
                    objects?.map { resourceObject ->
                        val authorObject = resourceObject.getParseUser("author")
                        EducationalResource(
                            resourceId = resourceObject.objectId,
                            title = resourceObject.getString("title") ?: "",
                            description = resourceObject.getString("description") ?: "",
                            type =
                                ResourceType.valueOf(
                                    resourceObject.getString("type") ?: "ARTICLE",
                                ),
                            contentUrl = resourceObject.getParseFile("content")?.url ?: "",
                            thumbnailUrl = resourceObject.getParseFile("thumbnail")?.url,
                            authorId = authorObject?.objectId ?: "",
                            authorName = authorObject?.getString("name") ?: "Unknown Author",
                            authorType =
                                AuthorType.valueOf(
                                    resourceObject.getString("authorType") ?: "VET",
                                ),
                            category = resourceObject.getString("category") ?: "",
                            estimatedDataUsage = resourceObject.getLong("estimatedDataUsage"),
                            duration = resourceObject.getInt("durationMinutes"),
                            language = resourceObject.getString("language") ?: "telugu",
                            tags = resourceObject.getList<String>("tags") ?: emptyList(),
                            viewCount = resourceObject.getInt("viewCount"),
                            likeCount = resourceObject.getInt("likeCount"),
                            downloadCount = resourceObject.getInt("downloadCount"),
                            createdAt = resourceObject.createdAt ?: Date(),
                            updatedAt = resourceObject.updatedAt ?: Date(),
                        )
                    } ?: emptyList()
                onResult(resources)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch content engagement analytics
fun fetchContentEngagement(
    resourceId: String,
    onResult: (ContentEngagement) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val params = hashMapOf<String, Any>("resourceId" to resourceId)

        ParseCloud.callFunctionInBackground<Map<String, Any>>(
            "getContentEngagement",
            params,
        ) { response, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val engagement =
                    response?.let { data ->
                        ContentEngagement(
                            resourceId = resourceId,
                            totalViews = (data["totalViews"] as? Number)?.toInt() ?: 0,
                            uniqueViewers = (data["uniqueViewers"] as? Number)?.toInt() ?: 0,
                            averageWatchTime = (data["averageWatchTime"] as? Number)?.toDouble() ?: 0.0,
                            completionRate = (data["completionRate"] as? Number)?.toDouble() ?: 0.0,
                            likeCount = (data["likeCount"] as? Number)?.toInt() ?: 0,
                            shareCount = (data["shareCount"] as? Number)?.toInt() ?: 0,
                            downloadCount = (data["downloadCount"] as? Number)?.toInt() ?: 0,
                            commentCount = (data["commentCount"] as? Number)?.toInt() ?: 0,
                            rating = (data["rating"] as? Number)?.toDouble() ?: 0.0,
                        )
                    } ?: ContentEngagement(resourceId = resourceId)
                onResult(engagement)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// ===============================
// IOT INTEGRATION FETCHERS
// ===============================

// Fetch IoT sensor data for farm monitoring
fun fetchSensorData(
    farmId: String,
    sensorType: String? = null,
    hoursBack: Int = 24,
    onResult: (List<SensorReading>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("SensorData")
        query.whereEqualTo("farmId", farmId)
        sensorType?.let { query.whereEqualTo("sensorType", it) }

        // Filter for recent data
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hoursBack)
        query.whereGreaterThan("timestamp", calendar.time)

        query.orderByDescending("timestamp")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val sensorReadings =
                    objects?.map { sensorObject ->
                        SensorReading(
                            readingId = sensorObject.objectId,
                            farmId = sensorObject.getString("farmId") ?: "",
                            sensorType =
                                SensorType.valueOf(
                                    sensorObject.getString("sensorType") ?: "TEMPERATURE",
                                ),
                            value = sensorObject.getDouble("value"),
                            unit = sensorObject.getString("unit") ?: "",
                            location = sensorObject.getString("location") ?: "",
                            deviceId = sensorObject.getString("deviceId") ?: "",
                            alertLevel =
                                AlertLevel.valueOf(
                                    sensorObject.getString("alertLevel") ?: "NORMAL",
                                ),
                            timestamp = sensorObject.getDate("timestamp") ?: Date(),
                            batteryLevel = sensorObject.getDouble("batteryLevel"),
                            signalStrength = sensorObject.getDouble("signalStrength"),
                        )
                    } ?: emptyList()
                onResult(sensorReadings)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch IoT device status for farm
fun fetchDeviceStatus(
    farmId: String,
    onResult: (List<IoTDevice>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("IoTDevice")
        query.whereEqualTo("farmId", farmId)
        query.orderByAscending("deviceName")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val devices =
                    objects?.map { deviceObject ->
                        IoTDevice(
                            deviceId = deviceObject.objectId,
                            deviceName = deviceObject.getString("deviceName") ?: "",
                            deviceType =
                                IoTDeviceType.valueOf(
                                    deviceObject.getString("deviceType") ?: "SENSOR",
                                ),
                            farmId = deviceObject.getString("farmId") ?: "",
                            location = deviceObject.getString("location") ?: "",
                            isOnline = deviceObject.getBoolean("isOnline"),
                            batteryLevel = deviceObject.getDouble("batteryLevel"),
                            signalStrength = deviceObject.getDouble("signalStrength"),
                            lastSeen = deviceObject.getDate("lastSeen") ?: Date(),
                            firmwareVersion = deviceObject.getString("firmwareVersion") ?: "",
                            configuration =
                                deviceObject.getJSONObject("configuration")?.toString()
                                    ?: "{}",
                        )
                    } ?: emptyList()
                onResult(devices)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch IoT alerts and notifications
fun fetchIoTAlerts(
    farmId: String,
    onResult: (List<IoTAlert>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("IoTAlert")
        query.whereEqualTo("farmId", farmId)
        query.whereEqualTo("isActive", true)
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val alerts =
                    objects?.map { alertObject ->
                        IoTAlert(
                            alertId = alertObject.objectId,
                            farmId = alertObject.getString("farmId") ?: "",
                            deviceId = alertObject.getString("deviceId") ?: "",
                            alertType =
                                IoTAlertType.valueOf(
                                    alertObject.getString("alertType") ?: "THRESHOLD_EXCEEDED",
                                ),
                            severity =
                                AlertLevel.valueOf(
                                    alertObject.getString("severity") ?: "MEDIUM",
                                ),
                            title = alertObject.getString("title") ?: "",
                            description = alertObject.getString("description") ?: "",
                            value = alertObject.getDouble("value"),
                            threshold = alertObject.getDouble("threshold"),
                            isActive = alertObject.getBoolean("isActive"),
                            isAcknowledged = alertObject.getBoolean("isAcknowledged"),
                            createdAt = alertObject.createdAt ?: Date(),
                            acknowledgedAt = alertObject.getDate("acknowledgedAt"),
                        )
                    } ?: emptyList()
                onResult(alerts)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// ===============================
// ANALYTICS & REPORTING FETCHERS
// ===============================

// Fetch comprehensive farm analytics
suspend fun fetchFarmAnalytics(
    farmerId: String? = null,
    context: Context,
    onResult: (FarmAnalytics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    withContext(Dispatchers.IO) {
        try {
            val params = hashMapOf<String, Any>()
            farmerId?.let { params["farmerId"] = it }

            ParseCloud.callFunctionInBackground<Map<String, Any>>(
                "getFarmAnalytics",
                params,
            ) { response, e ->
                setLoading(false)
                if (e != null) {
                    onError(e.localizedMessage)
                } else {
                    val analytics =
                        response?.let { data ->
                            FarmAnalytics(
                                totalFowl = (data["totalFowl"] as? Number)?.toInt() ?: 0,
                                activeFowl = (data["activeFowl"] as? Number)?.toInt() ?: 0,
                                avgHealthScore = (data["avgHealthScore"] as? Number)?.toDouble() ?: 0.0,
                                mortalityRate = (data["mortalityRate"] as? Number)?.toDouble() ?: 0.0,
                                feedEfficiency = (data["feedEfficiency"] as? Number)?.toDouble() ?: 0.0,
                                monthlyRevenue = (data["monthlyRevenue"] as? Number)?.toDouble() ?: 0.0,
                                yearlyRevenue = (data["yearlyRevenue"] as? Number)?.toDouble() ?: 0.0,
                                totalSales = (data["totalSales"] as? Number)?.toInt() ?: 0,
                                avgSalePrice = (data["avgSalePrice"] as? Number)?.toDouble() ?: 0.0,
                                breedPerformance =
                                    (data["breedPerformance"] as? List<Map<String, Any>>)?.map { breedMap ->
                                        BreedPerformance(
                                            breedName = breedMap["breedName"] as? String ?: "",
                                            count = (breedMap["count"] as? Number)?.toInt() ?: 0,
                                            avgPrice = (breedMap["avgPrice"] as? Number)?.toDouble() ?: 0.0,
                                            healthScore =
                                                (breedMap["healthScore"] as? Number)?.toDouble()
                                                    ?: 0.0,
                                            growthRate =
                                                (breedMap["growthRate"] as? Number)?.toDouble()
                                                    ?: 0.0,
                                        )
                                    } ?: emptyList(),
                                monthlyTrends =
                                    (data["monthlyTrends"] as? List<Map<String, Any>>)?.map { trendMap ->
                                        MonthlyTrend(
                                            month = trendMap["month"] as? String ?: "",
                                            fowlCount = (trendMap["fowlCount"] as? Number)?.toInt() ?: 0,
                                            revenue = (trendMap["revenue"] as? Number)?.toDouble() ?: 0.0,
                                            expenses = (trendMap["expenses"] as? Number)?.toDouble() ?: 0.0,
                                            profit = (trendMap["profit"] as? Number)?.toDouble() ?: 0.0,
                                        )
                                    } ?: emptyList(),
                                lastUpdated = Date(),
                            )
                        } ?: FarmAnalytics()
                    onResult(analytics)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                setLoading(false)
                onError(e.localizedMessage)
            }
        }
    }
}

// ===============================
// DATA USAGE TRACKING FETCHERS
// ===============================

// Get current session data usage
fun getSessionDataUsage(): Long {
    return try {
        TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
    } catch (e: Exception) {
        0L
    }
}

// Get monthly data usage from preferences
fun getMonthlyDataUsage(context: Context): Long {
    return try {
        val prefs = context.getSharedPreferences("data_usage", Context.MODE_PRIVATE)
        prefs.getLong("monthly_usage", 0L)
    } catch (e: Exception) {
        0L
    }
}

// Fetch data usage analytics for user
fun fetchDataUsageAnalytics(
    context: Context,
    onResult: (DataUsageAnalytics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val prefs = context.getSharedPreferences("data_usage", Context.MODE_PRIVATE)

        val analytics =
            DataUsageAnalytics(
                sessionUsage = getSessionDataUsage(),
                dailyUsage = prefs.getLong("daily_usage", 0L),
                weeklyUsage = prefs.getLong("weekly_usage", 0L),
                monthlyUsage = prefs.getLong("monthly_usage", 0L),
                totalAppUsage = prefs.getLong("total_app_usage", 0L),
                imageDataUsage = prefs.getLong("image_data_usage", 0L),
                videoDataUsage = prefs.getLong("video_data_usage", 0L),
                chatDataUsage = prefs.getLong("chat_data_usage", 0L),
                marketplaceDataUsage = prefs.getLong("marketplace_data_usage", 0L),
                lastResetDate = Date(prefs.getLong("last_reset_date", System.currentTimeMillis())),
                warningThreshold = prefs.getLong("warning_threshold", 50 * 1024 * 1024), // 50MB default
                monthlyLimit = prefs.getLong("monthly_limit", 1024 * 1024 * 1024), // 1GB default
            )

        onResult(analytics)
        setLoading(false)
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Record data usage for specific feature
fun recordDataUsage(
    context: Context,
    feature: String,
    bytesUsed: Long,
    onComplete: (() -> Unit)? = null,
) {
    try {
        val prefs = context.getSharedPreferences("data_usage", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Update feature-specific usage
        val currentUsage = prefs.getLong("${feature}_data_usage", 0L)
        editor.putLong("${feature}_data_usage", currentUsage + bytesUsed)

        // Update total usage
        val totalUsage = prefs.getLong("total_app_usage", 0L)
        editor.putLong("total_app_usage", totalUsage + bytesUsed)

        // Update session usage
        val sessionUsage = prefs.getLong("session_usage", 0L)
        editor.putLong("session_usage", sessionUsage + bytesUsed)

        editor.apply()
        onComplete?.invoke()
    } catch (e: Exception) {
        // Silent fail for data tracking
    }
}

// ===============================
// MESSAGING & COMMUNICATION FETCHERS
// ===============================

// Fetch personal messages for current user
fun fetchPersonalMessages(
    onResult: (List<PersonalMessage>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val currentUser = ParseUser.getCurrentUser()
        val senderQuery =
            ParseQuery.getQuery<ParseObject>("PersonalMessage").apply {
                whereEqualTo("sender", currentUser)
            }
        val receiverQuery =
            ParseQuery.getQuery<ParseObject>("PersonalMessage").apply {
                whereEqualTo("receiver", currentUser)
            }
        val combined =
            ParseQuery.or(listOf(senderQuery, receiverQuery)).apply {
                include("sender")
                include("receiver")
                orderByDescending("createdAt")
                limit = 100
            }
        combined.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val messages =
                    objects?.mapNotNull { obj ->
                        try {
                            val senderUser = obj.getParseUser("sender")
                            val receiverUser = obj.getParseUser("receiver")
                            PersonalMessage(
                                messageId = obj.objectId,
                                senderId = senderUser?.objectId,
                                senderName = senderUser?.username,
                                receiverId = receiverUser?.objectId,
                                receiverName = receiverUser?.username,
                                content = obj.getString("content") ?: "",
                                messageType =
                                    MessageType.valueOf(
                                        obj.getString("messageType") ?: "TEXT",
                                    ),
                                isRead = obj.getBoolean("isRead"),
                                createdAt = obj.createdAt ?: Date(),
                                imageUrl = obj.getParseFile("image")?.url,
                                audioUrl = obj.getParseFile("audio")?.url,
                            )
                        } catch (_: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(messages)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch group messages for user's groups
fun fetchGroupMessages(
    onResult: (List<GroupMessage>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val groupQuery =
            ParseQuery.getQuery<ParseObject>("GroupChat").apply {
                whereEqualTo("members", ParseUser.getCurrentUser())
            }
        groupQuery.findInBackground { groups, ge ->
            if (ge != null) {
                setLoading(false)
                onError(ge.localizedMessage)
            } else {
                val ids = groups?.map { it.objectId } ?: emptyList()
                if (ids.isEmpty()) {
                    setLoading(false)
                    onResult(emptyList())
                    return@findInBackground
                }
                val msgQuery =
                    ParseQuery.getQuery<ParseObject>("GroupMessage").apply {
                        whereContainedIn("groupId", ids)
                        include("sender")
                        orderByDescending("createdAt")
                        limit = 200
                    }
                msgQuery.findInBackground { objs, e2 ->
                    setLoading(false)
                    if (e2 != null) {
                        onError(e2.localizedMessage)
                    } else {
                        val msgs =
                            objs?.mapNotNull { obj ->
                                try {
                                    val su = obj.getParseUser("sender")
                                    GroupMessage(
                                        messageId = obj.objectId,
                                        groupId = obj.getString("groupId") ?: "",
                                        senderId = su?.objectId,
                                        senderName = su?.username,
                                        content = obj.getString("content") ?: "",
                                        messageType =
                                            MessageType.valueOf(
                                                obj.getString("messageType") ?: "TEXT",
                                            ),
                                        createdAt = obj.createdAt ?: Date(),
                                        imageUrl = obj.getParseFile("image")?.url,
                                        audioUrl = obj.getParseFile("audio")?.url,
                                        isSystemMessage = obj.getBoolean("isSystemMessage"),
                                    )
                                } catch (_: Exception) {
                                    null
                                }
                            } ?: emptyList()
                        onResult(msgs)
                    }
                }
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch community announcements and public messages
fun fetchCommunityMessages(
    onResult: (List<CommunityMessage>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        ParseQuery.getQuery<ParseObject>("CommunityMessage").apply {
            whereEqualTo("isPublic", true)
            include("author")
            orderByDescending("createdAt")
            limit = 50
        }.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val items =
                    objects?.mapNotNull { obj ->
                        try {
                            val au = obj.getParseUser("author")
                            CommunityMessage(
                                messageId = obj.objectId,
                                authorId = au?.objectId,
                                authorName = au?.username,
                                title = obj.getString("title") ?: "",
                                content = obj.getString("content") ?: "",
                                category = obj.getString("category") ?: "",
                                priority =
                                    AnnouncementPriority.valueOf(
                                        obj.getString("priority") ?: "NORMAL",
                                    ),
                                isSticky = obj.getBoolean("isSticky"),
                                createdAt = obj.createdAt ?: Date(),
                                imageUrl = obj.getParseFile("image")?.url,
                                viewCount = obj.getInt("viewCount"),
                                likeCount = obj.getInt("likeCount"),
                            )
                        } catch (_: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(items)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// MARKETPLACE FETCHERS
// ===============================

// Fetch user's cart items
fun fetchCartItems(
    onResult: (List<CartItem>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("CartItem")
        query.whereEqualTo("user", ParseUser.getCurrentUser())
        query.include("listing")
        query.include("listing.owner")
        query.orderByDescending("createdAt")
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val cartItems =
                    objects?.mapNotNull { cartObject ->
                        try {
                            val listingObject = cartObject.getParseObject("listing")
                            val ownerUser = listingObject?.getParseUser("owner")

                            listingObject?.let {
                                CartItem(
                                    objectId = cartObject.objectId, // Corrected from itemId
                                    listingId = listingObject.objectId,
                                    title = listingObject.getString("title") ?: "",
                                    price =
                                        listingObject.getString("price")?.toDoubleOrNull()
                                            ?: 0.0,
                                    quantity = cartObject.getInt("quantity"),
                                    sellerId = ownerUser?.objectId ?: "",
                                    sellerName =
                                        try {
                                            ownerUser?.fetchIfNeeded()
                                            ownerUser?.username ?: "Unknown Seller"
                                        } catch (e: Exception) {
                                            "Unknown Seller"
                                        },
                                    imageUrl = listingObject.getParseFile("image")?.url,
                                    addedAt = cartObject.createdAt ?: Date(),
                                    isAvailable = listingObject.getBoolean("isActive"),
                                )
                            }
                        } catch (e: Exception) {
                            null // Skip malformed items
                        }
                    } ?: emptyList()
                onResult(cartItems)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

// Fetch community posts with enhanced data
fun fetchCommunityPosts(
    onResult: (List<CommunityPost>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("CommunityPost")
        query.include("author")
        query.orderByDescending("createdAt")
        query.limit = 50 // Load recent posts
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val posts =
                    objects?.mapNotNull { postObject ->
                        try {
                            val authorObject = postObject.getParseUser("author")
                            CommunityPost(
                                objectId = postObject.objectId,
                                authorId = authorObject?.objectId ?: "",
                                authorName = authorObject?.username ?: "Unknown Author",
                                content = postObject.getString("content") ?: "",
                                imageUrl = postObject.getParseFile("image")?.url,
                                likeCount = postObject.getInt("likeCount"),
                                commentCount = postObject.getInt("commentCount"),
                                shareCount = postObject.getInt("shareCount"),
                                createdAt = postObject.createdAt ?: Date(),
                                tags = postObject.getList<String>("tags") ?: emptyList(),
                                location = postObject.getString("location"),
                                isVerified = postObject.getBoolean("isVerified"),
                            )
                        } catch (e: Exception) {
                            null // Skip malformed posts
                        }
                    } ?: emptyList()
                onResult(posts)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}

/*
// Fetch active auctions with bidding information
fun fetchActiveAuctions(
    onResult: (List<AuctionListing>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("AuctionListing")
        query.whereEqualTo("isActive", true)
        query.whereGreaterThan("endTime", Date()) // Only active auctions
        query.include("seller")
        query.orderByAscending("endTime") // Ending soon first

        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val auctions = objects?.mapNotNull { auctionObject ->
                    try {
                        val sellerUser = auctionObject.getParseUser("seller")
                        AuctionListing(
                            auctionId = auctionObject.objectId,
                            title = auctionObject.getString("title") ?: "",
                            description = auctionObject.getString("description") ?: "",
                            startingPrice = auctionObject.getDouble("startingPrice"),
                            currentBid = auctionObject.getDouble("currentBid"),
                            minimumIncrement = auctionObject.getDouble("minimumIncrement"),
                            startTime = auctionObject.getDate("startTime") ?: Date(),
                            endTime = auctionObject.getDate("endTime") ?: Date(),
                            sellerId = sellerUser?.objectId ?: "",
                            sellerName = try {
                                sellerUser?.username ?: "Unknown"
                            } catch (e: Exception) {
                                "Unknown"
                            },
                            fowlId = auctionObject.getString("fowlId") ?: "",
                            bidCount = auctionObject.getInt("bidCount"),
                            isReserveSet = auctionObject.getBoolean("isReserveSet"),
                            reservePrice = auctionObject.getDouble("reservePrice"),
                            imageUrls = auctionObject.getList<String>("imageUrls") ?: emptyList(),
                            status = AuctionStatus.valueOf(
                                auctionObject.getString("status") ?: "ACTIVE"
                            ),
                            location = auctionObject.getString("location") ?: "",
                            category = auctionObject.getString("category") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                onResult(auctions)
            }
        }
    } catch (e: Exception) {
        setLoading(false)
        onError(e.localizedMessage)
    }
}
*/
