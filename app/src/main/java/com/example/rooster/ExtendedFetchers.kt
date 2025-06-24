package com.example.rooster

import android.util.Log
import com.example.rooster.models.*
import com.parse.*
import com.parse.ParseCloud
import java.util.*

// Add these imports near the top after existing Auction imports:

// Helper function to safely parse dates from String? (Copied from Fetchers.kt for standalone use if needed)
private fun parseDate(dateString: String?): Date? =
    dateString?.let {
        try {
            // Attempt to parse ISO 8601 format, common in JSON
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(it)
        } catch (e: java.text.ParseException) {
            try {
                // Fallback for simpler date format if needed
                java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(it)
            } catch (e2: java.text.ParseException) {
                null // Add other formats or return null if parsing fails
            }
        }
    }

// ===============================
// SEARCH & DISCOVERY FETCHER
// ===============================

fun fetchSearchResults(
    query: String,
    filters: Map<String, Any>? = null, // Optional filters
    onResult: (List<SearchResult>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val params = hashMapOf<String, Any>("query" to query)
        filters?.let { params["filters"] = it }
        // Optional: Add category to params if it's a top-level filter and not in the map
        // params["category"] = filters?.get("category") ?: SearchCategory.ALL.name

        ParseCloud.callFunctionInBackground<List<Map<String, Any>>>(
            "performSearch", // Assuming this is your Cloud Function name
            params,
        ) { response, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val results =
                    response?.mapNotNull { item ->
                        try {
                            SearchResult(
                                id = item["id"] as? String ?: "",
                                type =
                                    SearchResultType.valueOf(
                                        item["type"] as? String ?: SearchResultType.FOWL.name,
                                    ),
                                title = item["title"] as? String ?: "",
                                subtitle = item["subtitle"] as? String ?: "",
                                description = item["description"] as? String ?: "",
                                price = (item["price"] as? Number)?.toInt() ?: 0,
                                location = item["location"] as? String ?: "",
                                imageUrl = item["imageUrl"] as? String ?: "",
                                relevanceScore = (item["relevanceScore"] as? Number)?.toFloat() ?: 0f,
                                timestamp =
                                    parseDate(item["timestamp"] as? String)
                                        ?: Date(),
                                // Use your existing parseDate or a new one
                                tags = item["tags"] as? List<String> ?: emptyList(),
                            )
                        } catch (ex: IllegalArgumentException) {
                            // Log or handle invalid SearchResultType from backend
                            onError("Invalid search result type from server: ${item["type"]}")
                            null
                        } catch (ex: Exception) {
                            onError("Error parsing search result: ${ex.localizedMessage}")
                            null
                        }
                    } ?: emptyList()
                onResult(results)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// AUCTION & BIDDING FETCHERS
// ===============================

fun fetchActiveAuctions(
    onResult: (List<AuctionListing>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query = ParseQuery.getQuery<ParseObject>("AuctionListing")
        query.whereEqualTo("isActive", true)
        query.whereGreaterThanOrEqualTo("endTime", Date()) // Active or very recently ended
        query.include("seller")
        query.orderByAscending("endTime") // Ending soonest first
        query.limit = 50 // Limit for performance

        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val auctions =
                    objects?.mapNotNull { obj ->
                        try {
                            val seller = obj.getParseUser("seller")
                            AuctionListing(
                                auctionId = obj.objectId,
                                title = obj.getString("title") ?: "",
                                description = obj.getString("description") ?: "",
                                startingPrice = obj.getDouble("startingPrice"),
                                currentBid = obj.getDouble("currentBid"),
                                minimumIncrement = obj.getDouble("minimumIncrement"),
                                startTime = obj.getDate("startTime") ?: Date(),
                                endTime = obj.getDate("endTime") ?: Date(),
                                sellerId = seller?.objectId ?: "",
                                sellerName = seller?.username ?: "Unknown Seller",
                                fowlId = obj.getString("fowlId") ?: "",
                                bidCount = obj.getInt("bidCount"),
                                isReserveSet = obj.getBoolean("isReserveSet"),
                                reservePrice = obj.getDouble("reservePrice"), // Default to 0.0 if not set
                                imageUrls = obj.getList<String>("imageUrls") ?: emptyList(),
                                status =
                                    AuctionStatus.valueOf(
                                        obj.getString("status") ?: AuctionStatus.ACTIVE.name,
                                    ),
                                location = obj.getString("location") ?: "",
                                category = obj.getString("category") ?: "",
                                // Defaulting new fields from ComprehensiveDataModels
                                customDurationHours = obj.getInt("customDurationHours"), // Default to 0 if not present
                                minimumBidPrice = obj.getDouble("minimumBidPrice"), // Default to 0.0
                                requiresBidderDeposit = obj.getBoolean("requiresBidderDeposit"),
                                bidderDepositPercentage = obj.getDouble("bidderDepositPercentage"), // Default to 0.0
                                allowsProxyBidding = obj.getBoolean("allowsProxyBidding"),
                                sellerBidMonitoring =
                                    BidMonitoringCategory.valueOf(
                                        obj.getString("sellerBidMonitoring")
                                            ?: BidMonitoringCategory.ALL_BIDS.name,
                                    ),
                                autoExtendOnLastMinuteBid = obj.getBoolean("autoExtendOnLastMinuteBid"),
                                extensionMinutes = obj.getInt("extensionMinutes"), // Default to 0
                                buyNowPrice = obj.getDouble("buyNowPrice"), // Nullable, defaults to null if not present
                                watchers = obj.getInt("watchers"), // Default to 0
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid auction status or bid monitoring category: ${ex.message}")
                            null
                        } catch (ex: Exception) {
                            onError("Error parsing auction: ${ex.localizedMessage}")
                            null
                        }
                    } ?: emptyList()
                onResult(auctions)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Enhanced auction bids fetcher
fun fetchEnhancedAuctionBids(
    auctionId: String,
    onResult: (List<EnhancedAuctionBid>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("Bid").apply {
                whereEqualTo("listingId", auctionId)
                include("bidder")
                orderByDescending("bidAmount") // Highest bids first
                limit = 100
            }

        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val bids =
                    objects?.mapNotNull { obj ->
                        try {
                            val bidderUser = obj.getParseUser("bidder")
                            EnhancedAuctionBid(
                                bidId = obj.objectId,
                                auctionId = obj.getString("listingId") ?: "",
                                bidderId = bidderUser?.objectId ?: "",
                                bidderName = bidderUser?.username ?: "Unknown Bidder",
                                bidAmount = obj.getDouble("bidAmount"),
                                bidTime = obj.createdAt ?: Date(),
                                isWinning = obj.getBoolean("isWinning"),
                                isProxyBid = obj.getBoolean("isProxyBid"),
                                proxyMaxAmount = obj.getDouble("proxyMaxAmount"),
                                depositAmount = obj.getDouble("depositAmount"),
                                depositStatus =
                                    obj.getString("depositStatus")
                                        ?.let { DepositStatus.valueOf(it) },
                                bidStatus =
                                    BidStatus.valueOf(
                                        obj.getString("bidStatus") ?: BidStatus.ACTIVE.name,
                                    ),
                                bidMessage = obj.getString("bidMessage"),
                                bidderRating = obj.getDouble("bidderRating"),
                                previousBidCount = obj.getInt("previousBidCount"),
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid enum value: ${ex.message}")
                            null
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(bids)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// EVENTS & COMPETITIONS FETCHER
// ===============================

fun fetchUpcomingEvents(
    region: String? = null, // Optional region filter
    onResult: (List<ParseEvent>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("Event") // Assuming "Event" is the Parse class name
        query.whereGreaterThanOrEqualTo("eventDate", Date()) // Fetch current and future events
        region?.let { query.whereEqualTo("region", it) }
        query.orderByAscending("eventDate")
        query.include("organizer") // Include organizer details if they are pointers
        query.limit = 50 // Limit results for performance

        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val events =
                    objects?.mapNotNull { obj ->
                        try {
                            val organizer = obj.getParseUser("organizer") // Get ParseUser object
                            ParseEvent(
                                eventId = obj.objectId,
                                title = obj.getString("title") ?: "",
                                description = obj.getString("description") ?: "",
                                eventType =
                                    EventType.valueOf(
                                        obj.getString("eventType") ?: EventType.COMPETITION.name,
                                    ),
                                eventDate = obj.getDate("eventDate") ?: Date(),
                                location = obj.getString("location") ?: "",
                                region = obj.getString("region") ?: "",
                                organizerId = organizer?.objectId ?: "",
                                organizerName =
                                    organizer?.username
                                        ?: "Unknown Organizer",
                                // Use username
                                maxParticipants = obj.getInt("maxParticipants"),
                                currentParticipants = obj.getInt("currentParticipants"),
                                entryFee = obj.getDouble("entryFee"),
                                prizeMoney = obj.getDouble("prizeMoney"),
                                registrationDeadline = obj.getDate("registrationDeadline"),
                                isRegistrationOpen = obj.getBoolean("isRegistrationOpen"),
                                category = obj.getString("category") ?: "",
                                imageUrl = obj.getParseFile("image")?.url,
                                createdAt = obj.createdAt ?: Date(),
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid event type from server: ${obj.getString("eventType")}")
                            null
                        } catch (ex: Exception) {
                            onError("Error parsing event: ${ex.localizedMessage}")
                            null
                        }
                    } ?: emptyList()
                onResult(events)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// HIGH-LEVEL DASHBOARD ENHANCED FETCHER
// ===============================
fun fetchHighLevelDashboardData(
    onResult: (HighLevelDashboardData) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        // Fetch all dashboard components concurrently using individual fetchers
        fetchDashboardOverviewStats(
            onResult = { overviewStats ->
                fetchDashboardPerformanceMetrics(
                    onResult = { performanceMetrics ->
                        fetchDashboardTraceabilityMetrics(
                            onResult = { traceabilityMetrics ->
                                fetchDashboardAnalyticsMetrics(
                                    onResult = { analyticsMetrics ->
                                        fetchDashboardFraudAlerts(
                                            onResult = { fraudAlerts ->
                                                fetchDashboardFarmVerifications(
                                                    onResult = { farmVerifications ->
                                                        fetchDashboardUserVerifications(
                                                            onResult = { userVerifications ->
                                                                fetchDashboardTopFarmers(
                                                                    onResult = { topFarmers ->
                                                                        fetchDashboardRecentActivities(
                                                                            onResult = { recentActivities ->
                                                                                fetchDashboardSystemHealth(
                                                                                    onResult = { systemHealth ->
                                                                                        // Combine all data
                                                                                        val dashboardData =
                                                                                            HighLevelDashboardData(
                                                                                                overviewStats = overviewStats,
                                                                                                performanceMetrics = performanceMetrics,
                                                                                                traceabilityMetrics = traceabilityMetrics,
                                                                                                analyticsMetrics = analyticsMetrics,
                                                                                                fraudAlerts = fraudAlerts,
                                                                                                farmVerifications = farmVerifications,
                                                                                                userVerifications = userVerifications,
                                                                                                topFarmers = topFarmers,
                                                                                                recentActivities = recentActivities,
                                                                                                systemHealth = systemHealth,
                                                                                            )
                                                                                        setLoading(
                                                                                            false,
                                                                                        )
                                                                                        onResult(
                                                                                            dashboardData,
                                                                                        )
                                                                                    },
                                                                                    onError = { error ->
                                                                                        setLoading(
                                                                                            false,
                                                                                        )
                                                                                        onError("System Health fetch failed: $error")
                                                                                    },
                                                                                    setLoading = { /* already handled */ },
                                                                                )
                                                                            },
                                                                            onError = { error ->
                                                                                setLoading(false)
                                                                                onError("Recent Activities fetch failed: $error")
                                                                            },
                                                                            setLoading = { /* already handled */ },
                                                                        )
                                                                    },
                                                                    onError = { error ->
                                                                        setLoading(false)
                                                                        onError("Top Farmers fetch failed: $error")
                                                                    },
                                                                    setLoading = { /* already handled */ },
                                                                )
                                                            },
                                                            onError = { error ->
                                                                setLoading(false)
                                                                onError("User Verifications fetch failed: $error")
                                                            },
                                                            setLoading = { /* already handled */ },
                                                        )
                                                    },
                                                    onError = { error ->
                                                        setLoading(false)
                                                        onError("Farm Verifications fetch failed: $error")
                                                    },
                                                    setLoading = { /* already handled */ },
                                                )
                                            },
                                            onError = { error ->
                                                setLoading(false)
                                                onError("Fraud Alerts fetch failed: $error")
                                            },
                                            setLoading = { /* already handled */ },
                                        )
                                    },
                                    onError = { error ->
                                        setLoading(false)
                                        onError("Analytics Metrics fetch failed: $error")
                                    },
                                    setLoading = { /* already handled */ },
                                )
                            },
                            onError = { error ->
                                setLoading(false)
                                onError("Traceability Metrics fetch failed: $error")
                            },
                            setLoading = { /* already handled */ },
                        )
                    },
                    onError = { error ->
                        setLoading(false)
                        onError("Performance Metrics fetch failed: $error")
                    },
                    setLoading = { /* already handled */ },
                )
            },
            onError = { error ->
                setLoading(false)
                onError("Overview Stats fetch failed: $error")
            },
            setLoading = { /* already handled */ },
        )
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Individual dashboard component fetchers
private fun fetchDashboardOverviewStats(
    onResult: (OverviewStats) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        // Fetch total users
        val userQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
        userQuery.countInBackground { totalUsers, userError ->
            if (userError != null) {
                onError("Failed to fetch user count: ${userError.localizedMessage}")
                return@countInBackground
            }

            // Fetch active farmers (users with role 'farmer' and recent activity)
            val farmerQuery =
                ParseQuery.getQuery<ParseUser>(ParseUser::class.java).apply {
                    whereEqualTo("role", "farmer")
                    whereGreaterThan(
                        "lastActiveAt",
                        Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
                    ) // Active in last 30 days
                }
            farmerQuery.countInBackground { activeFarmers, farmerError ->
                if (farmerError != null) {
                    onError("Failed to fetch farmer count: ${farmerError.localizedMessage}")
                    return@countInBackground
                }

                // Fetch total fowl
                val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
                fowlQuery.countInBackground { totalFowl, fowlError ->
                    if (fowlError != null) {
                        onError("Failed to fetch fowl count: ${fowlError.localizedMessage}")
                        return@countInBackground
                    }

                    // Fetch marketplace items
                    val listingQuery =
                        ParseQuery.getQuery<ParseObject>("Listing").apply {
                            whereEqualTo("isActive", true)
                        }
                    listingQuery.countInBackground { marketplaceItems, listingError ->
                        if (listingError != null) {
                            onError("Failed to fetch marketplace items: ${listingError.localizedMessage}")
                            return@countInBackground
                        }

                        onResult(
                            OverviewStats(
                                totalUsers = totalUsers,
                                activeFarmers = activeFarmers,
                                totalFowl = totalFowl,
                                marketplaceItems = marketplaceItems,
                            ),
                        )
                    }
                }
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardPerformanceMetrics(
    onResult: (DashboardMetrics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val yesterday = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        val weekAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)

        // Fetch daily active users
        val dauQuery =
            ParseQuery.getQuery<ParseUser>(ParseUser::class.java).apply {
                whereGreaterThan("lastActiveAt", yesterday)
            }
        dauQuery.countInBackground { dailyActiveUsers, dauError ->
            if (dauError != null) {
                onError("Failed to fetch DAU: ${dauError.localizedMessage}")
                return@countInBackground
            }

            // Fetch marketplace sales (last 30 days)
            val salesQuery =
                ParseQuery.getQuery<ParseObject>("PaymentTransaction").apply {
                    whereEqualTo("status", "SUCCESS")
                    whereGreaterThan(
                        "createdAt",
                        Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
                    )
                }
            salesQuery.findInBackground { transactions, salesError ->
                if (salesError != null) {
                    onError("Failed to fetch sales: ${salesError.localizedMessage}")
                    return@findInBackground
                }

                val totalSales = transactions?.sumOf { it.getDouble("amount") } ?: 0.0

                // Calculate session metrics (simplified)
                val avgSessionMinutes = 25 // This would require session tracking implementation

                onResult(
                    DashboardMetrics(
                        dailyActiveUsers = dailyActiveUsers,
                        dauTrend = 1.0, // Would need historical data to calculate
                        avgSessionMinutes = avgSessionMinutes.toDouble(),
                        sessionTrend = 0.0,
                        marketplaceSales = totalSales,
                        salesTrend = 1.0,
                    ),
                )
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardTraceabilityMetrics(
    onResult: (TraceabilityMetrics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        // Fetch active transfers
        val activeTransferQuery =
            ParseQuery.getQuery<ParseObject>("FowlTransfer").apply {
                whereEqualTo("status", "ACTIVE")
            }
        activeTransferQuery.countInBackground { activeTransfers, activeError ->
            if (activeError != null) {
                onError("Failed to fetch active transfers: ${activeError.localizedMessage}")
                return@countInBackground
            }

            // Fetch completed transfers
            val completedTransferQuery =
                ParseQuery.getQuery<ParseObject>("FowlTransfer").apply {
                    whereEqualTo("status", "COMPLETED")
                }
            completedTransferQuery.countInBackground { completedTransfers, completedError ->
                if (completedError != null) {
                    onError("Failed to fetch completed transfers: ${completedError.localizedMessage}")
                    return@countInBackground
                }

                // Fetch pending verifications
                val verificationQuery =
                    ParseQuery.getQuery<ParseObject>("TransferVerification").apply {
                        whereEqualTo("status", "PENDING")
                    }
                verificationQuery.countInBackground { pendingVerifications, verificationError ->
                    if (verificationError != null) {
                        onError("Failed to fetch verifications: ${verificationError.localizedMessage}")
                        return@countInBackground
                    }

                    // Fetch fraud alerts
                    val fraudQuery =
                        ParseQuery.getQuery<ParseObject>("FraudAlert").apply {
                            whereEqualTo("status", "ACTIVE")
                        }
                    fraudQuery.countInBackground { fraudCount, fraudError ->
                        if (fraudError != null) {
                            onError("Failed to fetch fraud alerts: ${fraudError.localizedMessage}")
                            return@countInBackground
                        }

                        // Calculate success rate
                        val total = activeTransfers + completedTransfers
                        val successRate =
                            if (total > 0) (completedTransfers.toDouble() / total) * 100 else 100.0

                        onResult(
                            TraceabilityMetrics(
                                activeTransfersCount = activeTransfers,
                                completedTransfersCount = completedTransfers,
                                pendingVerifications = pendingVerifications,
                                fraudAlertsCount = fraudCount,
                                verificationSuccessRate = successRate,
                                avgTransferTime = 24.0, // Would need actual calculation from transfer data
                            ),
                        )
                    }
                }
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardAnalyticsMetrics(
    onResult: (AnalyticsMetrics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        // Fetch marketplace listings for price analysis
        val priceQuery =
            ParseQuery.getQuery<ParseObject>("Listing").apply {
                whereEqualTo("isActive", true)
                selectKeys(listOf("price"))
            }
        priceQuery.findInBackground { listings, priceError ->
            if (priceError != null) {
                onError("Failed to fetch price data: ${priceError.localizedMessage}")
                return@findInBackground
            }

            val prices =
                listings?.mapNotNull {
                    it.getString("price")?.toDoubleOrNull()
                }?.filter { it > 0 } ?: emptyList()

            val averagePrice = if (prices.isNotEmpty()) prices.average() else 0.0
            val priceVariance =
                if (prices.size > 1) {
                    val mean = prices.average()
                    val variance = prices.map { (it - mean) * (it - mean) }.average()
                    (kotlin.math.sqrt(variance) / mean) * 100
                } else {
                    0.0
                }

            // Fetch suspicious patterns (simplified)
            val suspiciousQuery =
                ParseQuery.getQuery<ParseObject>("SuspiciousActivity").apply {
                    whereGreaterThan(
                        "createdAt",
                        Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                    )
                }
            suspiciousQuery.countInBackground { suspiciousCount, suspiciousError ->
                if (suspiciousError != null) {
                    onError("Failed to fetch suspicious patterns: ${suspiciousError.localizedMessage}")
                    return@countInBackground
                }

                // Calculate transfer velocity (transfers per hour in last 24h)
                val transferQuery =
                    ParseQuery.getQuery<ParseObject>("FowlTransfer").apply {
                        whereGreaterThan(
                            "createdAt",
                            Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                        )
                    }
                transferQuery.countInBackground { recentTransfers, transferError ->
                    if (transferError != null) {
                        onError("Failed to fetch transfer velocity: ${transferError.localizedMessage}")
                        return@countInBackground
                    }

                    val transferVelocity = recentTransfers / 24 // per hour

                    onResult(
                        AnalyticsMetrics(
                            transferVelocity = transferVelocity.toDouble(),
                            averagePrice = averagePrice,
                            priceVariance = priceVariance,
                            suspiciousPatterns = suspiciousCount,
                            networkHealth = 98.5, // Converted to Double
                            dataIntegrity = 98.5, // Would need data validation checks
                        ),
                    )
                }
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardFraudAlerts(
    onResult: (List<FraudAlert>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("FraudAlert").apply {
                whereEqualTo("status", "ACTIVE")
                orderByDescending("createdAt")
                limit = 10
            }
        query.findInBackground { objects, e ->
            if (e != null) {
                onError("Failed to fetch fraud alerts: ${e.localizedMessage}")
            } else {
                val alerts =
                    objects?.mapNotNull { obj ->
                        try {
                            FraudAlert(
                                alertId = obj.objectId,
                                alertType = obj.getString("alertType") ?: "",
                                severity = obj.getString("severity") ?: "Medium",
                                relatedEntity = obj.getString("relatedEntity") ?: "",
                                status = obj.getString("status") ?: "ACTIVE",
                                timestamp = obj.createdAt?.time ?: System.currentTimeMillis(),
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(alerts)
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardFarmVerifications(
    onResult: (List<FarmVerification>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("FarmVerification").apply {
                whereNotEqualTo("verificationStatus", "COMPLETED")
                include("owner")
                orderByDescending("createdAt")
                limit = 10
            }
        query.findInBackground { objects, e ->
            if (e != null) {
                onError("Failed to fetch farm verifications: ${e.localizedMessage}")
            } else {
                val verifications =
                    objects?.mapNotNull { obj ->
                        try {
                            val owner = obj.getParseUser("owner")
                            FarmVerification(
                                verificationId = obj.objectId,
                                farmName = obj.getString("farmName") ?: "",
                                ownerName = owner?.username ?: "Unknown",
                                verificationStatus = obj.getString("verificationStatus") ?: "PENDING",
                                riskLevel = obj.getString("riskLevel") ?: "Low",
                                submittedDate = obj.createdAt?.time ?: System.currentTimeMillis(),
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(verifications)
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardUserVerifications(
    onResult: (List<UserVerification>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("UserVerification").apply {
                whereNotEqualTo("verificationLevel", "VERIFIED")
                include("user")
                orderByDescending("updatedAt")
                limit = 10
            }
        query.findInBackground { objects, e ->
            if (e != null) {
                onError("Failed to fetch user verifications: ${e.localizedMessage}")
            } else {
                val verifications =
                    objects?.mapNotNull { obj ->
                        try {
                            val user = obj.getParseUser("user")
                            UserVerification(
                                verificationId = obj.objectId,
                                userName = user?.username ?: "Unknown",
                                userType = obj.getString("userType") ?: "farmer",
                                verificationLevel = obj.getString("verificationLevel") ?: "PENDING",
                                verificationProgress = obj.getDouble("verificationProgress"),
                                lastActivity = obj.updatedAt?.time ?: System.currentTimeMillis(),
                                documentsSubmitted = obj.getList<String>("documentsSubmitted")?.size ?: 0,
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(verifications)
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardTopFarmers(
    onResult: (List<TopFarmer>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val query =
            ParseQuery.getQuery<ParseUser>(ParseUser::class.java).apply {
                whereEqualTo("role", "farmer")
                orderByDescending("farmerScore")
                limit = 10
            }
        query.findInBackground { users, e ->
            if (e != null) {
                onError("Failed to fetch top farmers: ${e.localizedMessage}")
            } else {
                val farmers =
                    users?.mapIndexed { index, user ->
                        try {
                            TopFarmer(
                                id = user.objectId,
                                name = user.username ?: "Unknown",
                                location = user.getString("location") ?: "Unknown",
                                fowlCount = user.getInt("fowlCount"),
                                score = user.getDouble("farmerScore"),
                                rank = index + 1,
                            )
                        } catch (ex: Exception) {
                            TopFarmer("", "Unknown", "Unknown", 0, 0.0, index + 1)
                        }
                    } ?: emptyList()
                onResult(farmers)
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardRecentActivities(
    onResult: (List<RecentActivity>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("ActivityLog").apply {
                orderByDescending("createdAt")
                limit = 20
            }
        query.findInBackground { objects, e ->
            if (e != null) {
                onError("Failed to fetch recent activities: ${e.localizedMessage}")
            } else {
                val activities =
                    objects?.mapNotNull { obj ->
                        try {
                            val timeDiff = System.currentTimeMillis() - (obj.createdAt?.time ?: 0)
                            val timeAgo =
                                when {
                                    timeDiff < 60000 -> "Just now"
                                    timeDiff < 3600000 -> "${timeDiff / 60000} minutes ago"
                                    timeDiff < 86400000 -> "${timeDiff / 3600000} hours ago"
                                    else -> "${timeDiff / 86400000} days ago"
                                }

                            RecentActivity(
                                type = obj.getString("activityType") ?: "",
                                description = obj.getString("description") ?: "",
                                timeAgo = timeAgo,
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: generateRecentActivities() // Fallback to mock data
                onResult(activities)
            }
        }
    } catch (ex: Exception) {
        onError(ex.localizedMessage)
    }
}

private fun fetchDashboardSystemHealth(
    onResult: (SystemHealth) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    try {
        // Check server health by querying a simple Parse object
        val healthQuery = ParseQuery.getQuery<ParseObject>("SystemHealth")
        healthQuery.getFirstInBackground { obj, e ->
            val serverStatus = if (e == null) "Healthy" else "Issues Detected"

            // Check database connectivity
            val dbQuery = ParseQuery.getQuery<ParseUser>(ParseUser::class.java)
            dbQuery.limit = 1
            dbQuery.findInBackground { users, dbError ->
                val databaseStatus = if (dbError == null) "Healthy" else "Connection Issues"

                onResult(
                    SystemHealth(
                        serverStatus = serverStatus,
                        databaseStatus = databaseStatus,
                    ),
                )
            }
        }
    } catch (ex: Exception) {
        onResult(
            SystemHealth(
                serverStatus = "Unknown",
                databaseStatus = "Unknown",
            ),
        )
    }
}

// ===============================
// VET CONSULTATION ENHANCED FETCHERS
// ===============================

// Fetch available consultation slots for a given vet
fun fetchConsultationSlots(
    vetId: String,
    onResult: (List<ConsultationSlot>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("ConsultationSlot").apply {
                whereEqualTo("vetId", vetId)
                whereEqualTo("isAvailable", true)
                orderByAscending("startTime")
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val slots =
                    objects?.mapNotNull { obj ->
                        try {
                            ConsultationSlot(
                                slotId = obj.objectId,
                                vetId = obj.getString("vetId") ?: "",
                                startTime = obj.getDate("startTime") ?: Date(),
                                endTime = obj.getDate("endTime") ?: Date(),
                                isAvailable = obj.getBoolean("isAvailable"),
                                consultationType =
                                    ConsultationType.valueOf(
                                        obj.getString("consultationType") ?: "AI_CHAT",
                                    ),
                                price = obj.getString("price")?.toDoubleOrNull() ?: 0.0,
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid consultation type: ${obj.getString("consultationType")}")
                            null
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(slots)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch vet availability schedule for a given vet
fun fetchVetAvailability(
    vetId: String,
    onResult: (List<VetAvailability>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("VetAvailability").apply {
                whereEqualTo("vetId", vetId)
                orderByAscending("dayOfWeek")
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val avail =
                    objects?.mapNotNull { obj ->
                        try {
                            VetAvailability(
                                vetId = obj.getString("vetId") ?: "",
                                dayOfWeek = obj.getInt("dayOfWeek"),
                                startTime = obj.getString("startTime") ?: "",
                                endTime = obj.getString("endTime") ?: "",
                                isAvailable = obj.getBoolean("isAvailable"),
                                consultationTypes =
                                    obj.getList<String>("consultationTypes")
                                        ?.mapNotNull { typeString ->
                                            try {
                                                ConsultationType.valueOf(typeString)
                                            } catch (ex: IllegalArgumentException) {
                                                onError("Invalid consultation type in availability: $typeString")
                                                null
                                            }
                                        } ?: emptyList(),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(avail)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch consultation ratings for a given vet
fun fetchConsultationRatings(
    vetId: String,
    onResult: (List<ConsultationRating>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("ConsultationRating").apply {
                whereEqualTo("vetId", vetId)
                orderByDescending("createdAt")
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val ratings =
                    objects?.mapNotNull { obj ->
                        try {
                            ConsultationRating(
                                ratingId = obj.objectId,
                                consultationId = obj.getString("consultationId") ?: "",
                                farmerId = obj.getString("farmerId") ?: "",
                                vetId = obj.getString("vetId") ?: "",
                                rating = obj.getInt("rating"),
                                review = obj.getString("review"),
                                createdAt = obj.createdAt ?: Date(),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(ratings)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// EDUCATIONAL CONTENT ENHANCED FETCHERS
// ===============================

// Fetch available educational categories
fun fetchEducationalCategories(
    onResult: (List<EducationalCategory>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("EducationalCategory").apply {
                orderByAscending("name") // Or by a custom order field if available
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val categories =
                    objects?.mapNotNull { obj ->
                        try {
                            EducationalCategory(
                                categoryId = obj.objectId,
                                name = obj.getString("name") ?: "",
                                description = obj.getString("description") ?: "",
                                iconUrl = obj.getParseFile("icon")?.url,
                                resourceCount = obj.getInt("resourceCount"),
                                isPopular = obj.getBoolean("isPopular"),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(categories)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch learning progress for a user and resource
fun fetchLearningProgress(
    userId: String,
    resourceId: String,
    onResult: (LearningProgress?) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("LearningProgress").apply {
                whereEqualTo("userId", userId)
                whereEqualTo("resourceId", resourceId)
            }
        query.getFirstInBackground { obj, e ->
            setLoading(false)
            if (e != null) {
                if (e.code == ParseException.OBJECT_NOT_FOUND) {
                    onResult(null) // No progress found, not an error
                } else {
                    onError(e.localizedMessage)
                }
            } else {
                val progress =
                    obj?.let {
                        try {
                            LearningProgress(
                                userId = it.getString("userId") ?: "",
                                resourceId = it.getString("resourceId") ?: "",
                                progress = it.getDouble("progress"),
                                lastWatchedTime = it.getLong("lastWatchedTime"),
                                isCompleted = it.getBoolean("isCompleted"),
                                startedAt = it.getDate("startedAt") ?: Date(),
                                completedAt = it.getDate("completedAt"),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    }
                onResult(progress)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch comments for a specific educational resource
fun fetchResourceComments(
    resourceId: String,
    onResult: (List<ResourceComment>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("ResourceComment").apply {
                whereEqualTo("resourceId", resourceId)
                orderByDescending("createdAt") // Show newest comments first
                // include("user") // If you want to fetch user details, though model has userName
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val comments =
                    objects?.mapNotNull { obj ->
                        try {
                            ResourceComment(
                                commentId = obj.objectId,
                                resourceId = obj.getString("resourceId") ?: "",
                                userId = obj.getString("userId") ?: "",
                                userName =
                                    obj.getString("userName")
                                        ?: "Anonymous",
                                // Fallback for userName
                                comment = obj.getString("comment") ?: "",
                                parentCommentId = obj.getString("parentCommentId"),
                                likeCount = obj.getInt("likeCount"),
                                createdAt = obj.createdAt ?: Date(),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()
                onResult(comments)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// FARM MANAGEMENT FETCHERS
// ===============================

// Fetch comprehensive farm analytics for a specific farmer
fun fetchFarmManagementData(
    farmerId: String? = null,
    onResult: (FarmManagementData) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentFarmerId = farmerId ?: ParseUser.getCurrentUser()?.objectId

    if (currentFarmerId == null) {
        onError("User not authenticated")
        return
    }

    try {
        // Fetch fowl inventory
        val fowlQuery =
            ParseQuery.getQuery<ParseObject>("Fowl").apply {
                whereEqualTo("ownerId", currentFarmerId)
                whereEqualTo("isActive", true)
            }
        fowlQuery.findInBackground { fowls, fowlError ->
            if (fowlError != null) {
                onError("Failed to fetch fowl data: ${fowlError.localizedMessage}")
                return@findInBackground
            }

            // Fetch health records
            val healthQuery =
                ParseQuery.getQuery<ParseObject>("HealthRecord").apply {
                    whereContainedIn("fowlId", fowls?.map { it.objectId } ?: emptyList<String>())
                    orderByDescending("date")
                    limit = 50
                }
            healthQuery.findInBackground { healthRecords, healthError ->
                if (healthError != null) {
                    onError("Failed to fetch health records: ${healthError.localizedMessage}")
                    return@findInBackground
                }

                // Fetch feed consumption
                val feedQuery =
                    ParseQuery.getQuery<ParseObject>("FeedRecord").apply {
                        whereEqualTo("farmerId", currentFarmerId)
                        whereGreaterThan(
                            "date",
                            Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
                        )
                    }
                feedQuery.findInBackground { feedRecords, feedError ->
                    if (feedError != null) {
                        onError("Failed to fetch feed records: ${feedError.localizedMessage}")
                        return@findInBackground
                    }

                    // Process data and create farm management summary
                    val totalFowl = fowls?.size ?: 0
                    val healthyFowl =
                        fowls?.count { fowl ->
                            fowl.getString("healthStatus") == "HEALTHY"
                        } ?: 0

                    val totalFeedConsumed =
                        feedRecords?.sumOf {
                            it.getDouble("quantity")
                        } ?: 0.0

                    val recentHealthIssues =
                        healthRecords?.count { record ->
                            record.getString("status") == "SICK" &&
                                record.createdAt?.after(Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)) == true
                        } ?: 0

                    val farmData =
                        FarmManagementData(
                            farmerId = currentFarmerId,
                            totalFowl = totalFowl,
                            healthyFowl = healthyFowl,
                            sickFowl = totalFowl - healthyFowl,
                            totalFeedConsumed = totalFeedConsumed,
                            averageFeedPerFowl = if (totalFowl > 0) totalFeedConsumed / totalFowl else 0.0,
                            recentHealthIssues = recentHealthIssues,
                            mortalityRate = 0.0, // Would need death records
                            productionEfficiency = if (healthyFowl > 0) (healthyFowl.toDouble() / totalFowl) * 100 else 0.0,
                        )

                    setLoading(false)
                    onResult(farmData)
                }
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch vaccination schedule and compliance
fun fetchVaccinationSchedule(
    farmerId: String? = null,
    onResult: (List<VaccinationRecord>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentFarmerId = farmerId ?: ParseUser.getCurrentUser()?.objectId

    if (currentFarmerId == null) {
        onError("User not authenticated")
        return
    }

    try {
        val query =
            ParseQuery.getQuery<ParseObject>("Vaccination").apply {
                whereEqualTo("createdBy", currentFarmerId)
                orderByDescending("scheduledDate")
                limit = 100
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError("Failed to fetch vaccination records: ${e.localizedMessage}")
            } else {
                val records =
                    objects?.mapNotNull { obj ->
                        try {
                            VaccinationRecord.fromParseObject(obj)
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(records)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch breeding program data
fun fetchBreedingProgram(
    farmerId: String? = null,
    onResult: (List<FarmBreedingRecord>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentFarmerId = farmerId ?: ParseUser.getCurrentUser()?.objectId

    if (currentFarmerId == null) {
        onError("User not authenticated")
        return
    }

    try {
        val query =
            ParseQuery.getQuery<ParseObject>("BreedingRecord").apply {
                whereEqualTo("farmerId", currentFarmerId)
                orderByDescending("breedingDate")
                limit = 50
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError("Failed to fetch breeding records: ${e.localizedMessage}")
            } else {
                val records =
                    objects?.mapNotNull { obj ->
                        try {
                            FarmBreedingRecord(
                                recordId = obj.objectId,
                                farmerId = obj.getString("farmerId") ?: "",
                                maleId = obj.getString("maleId") ?: "",
                                femaleId = obj.getString("femaleId") ?: "",
                                breedingDate = obj.getDate("breedingDate") ?: Date(),
                                expectedHatchDate = obj.getDate("expectedHatchDate"),
                                actualHatchDate = obj.getDate("actualHatchDate"),
                                eggsLaid = obj.getInt("eggsLaid"),
                                eggsHatched = obj.getInt("eggsHatched"),
                                hatchingRate = obj.getDouble("hatchingRate"),
                                notes = obj.getString("notes") ?: "",
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()
                onResult(records)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// FINANCIAL ANALYTICS FETCHERS
// ===============================

// Fetch comprehensive financial data
fun fetchFinancialAnalytics(
    farmerId: String? = null,
    periodMonths: Int = 12,
    onResult: (FinancialAnalytics) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentFarmerId = farmerId ?: ParseUser.getCurrentUser()?.objectId

    if (currentFarmerId == null) {
        onError("User not authenticated")
        return
    }

    val periodStart = Date(System.currentTimeMillis() - periodMonths * 30L * 24 * 60 * 60 * 1000)

    try {
        // Fetch income from sales
        val salesQuery =
            ParseQuery.getQuery<ParseObject>("PaymentTransaction").apply {
                whereEqualTo("sellerId", currentFarmerId)
                whereEqualTo("status", "SUCCESS")
                whereGreaterThan("createdAt", periodStart)
            }
        salesQuery.findInBackground { sales, salesError ->
            if (salesError != null) {
                onError("Failed to fetch sales data: ${salesError.localizedMessage}")
                return@findInBackground
            }

            // Fetch expenses
            val expenseQuery =
                ParseQuery.getQuery<ParseObject>("FarmExpense").apply {
                    whereEqualTo("farmerId", currentFarmerId)
                    whereGreaterThan("date", periodStart)
                }
            expenseQuery.findInBackground { expenses, expenseError ->
                if (expenseError != null) {
                    onError("Failed to fetch expense data: ${expenseError.localizedMessage}")
                    return@findInBackground
                }

                val totalRevenue = sales?.sumOf { it.getDouble("amount") } ?: 0.0
                val totalExpenses = expenses?.sumOf { it.getDouble("amount") } ?: 0.0
                val netProfit = totalRevenue - totalExpenses

                // Calculate monthly breakdown
                val monthlyData = mutableMapOf<String, FinancialMonth>()

                sales?.forEach { sale ->
                    val monthKey =
                        java.text.SimpleDateFormat("yyyy-MM", Locale.US)
                            .format(sale.createdAt ?: Date())
                    val existing = monthlyData[monthKey] ?: FinancialMonth(monthKey, 0.0, 0.0, 0.0)
                    monthlyData[monthKey] =
                        existing.copy(revenue = existing.revenue + sale.getDouble("amount"))
                }

                expenses?.forEach { expense ->
                    val monthKey =
                        java.text.SimpleDateFormat("yyyy-MM", Locale.US)
                            .format(expense.getDate("date") ?: Date())
                    val existing = monthlyData[monthKey] ?: FinancialMonth(monthKey, 0.0, 0.0, 0.0)
                    monthlyData[monthKey] =
                        existing.copy(expenses = existing.expenses + expense.getDouble("amount"))
                }

                // Calculate profit for each month
                monthlyData.forEach { (key, month) ->
                    monthlyData[key] = month.copy(profit = month.revenue - month.expenses)
                }

                val analytics =
                    FinancialAnalytics(
                        farmerId = currentFarmerId,
                        periodStart = periodStart,
                        periodEnd = Date(),
                        totalRevenue = totalRevenue,
                        totalExpenses = totalExpenses,
                        netProfit = netProfit,
                        profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0,
                        monthlyBreakdown = monthlyData.values.toList().sortedBy { it.month },
                    )

                setLoading(false)
                onResult(analytics)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch expense categories breakdown
fun fetchExpenseBreakdown(
    farmerId: String? = null,
    periodMonths: Int = 12,
    onResult: (List<ExpenseCategory>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentFarmerId = farmerId ?: ParseUser.getCurrentUser()?.objectId

    if (currentFarmerId == null) {
        onError("User not authenticated")
        return
    }

    val periodStart = Date(System.currentTimeMillis() - periodMonths * 30L * 24 * 60 * 60 * 1000)

    try {
        val query =
            ParseQuery.getQuery<ParseObject>("FarmExpense").apply {
                whereEqualTo("farmerId", currentFarmerId)
                whereGreaterThan("date", periodStart)
            }
        query.findInBackground { expenses, e ->
            setLoading(false)
            if (e != null) {
                onError("Failed to fetch expense breakdown: ${e.localizedMessage}")
            } else {
                val categoryMap = mutableMapOf<String, Double>()

                expenses?.forEach { expense ->
                    val category = expense.getString("category") ?: "Other"
                    val amount = expense.getDouble("amount")
                    categoryMap[category] = (categoryMap[category] ?: 0.0) + amount
                }

                val totalExpenses = categoryMap.values.sum()
                val categories =
                    categoryMap.map { (category, amount) ->
                        ExpenseCategory(
                            category = category,
                            amount = amount,
                            percentage = if (totalExpenses > 0) (amount / totalExpenses) * 100 else 0.0,
                        )
                    }.sortedByDescending { it.amount }

                onResult(categories)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// MARKET INTELLIGENCE FETCHERS
// ===============================

// Fetch market trends and price analysis
fun fetchMarketIntelligence(
    region: String? = null,
    breed: String? = null,
    onResult: (MarketIntelligence) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)

    try {
        // Fetch recent market transactions
        val transactionQuery =
            ParseQuery.getQuery<ParseObject>("PaymentTransaction").apply {
                whereEqualTo("status", "SUCCESS")
                whereGreaterThan(
                    "createdAt",
                    Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
                )
                limit = 1000
            }
        transactionQuery.findInBackground { transactions, transactionError ->
            if (transactionError != null) {
                onError("Failed to fetch market data: ${transactionError.localizedMessage}")
                return@findInBackground
            }

            // Fetch active listings for current market prices
            val listingQuery =
                ParseQuery.getQuery<ParseObject>("Listing").apply {
                    whereEqualTo("isActive", true)
                    breed?.let { whereEqualTo("breed", it) }
                    region?.let { whereEqualTo("region", it) }
                }
            listingQuery.findInBackground { listings, listingError ->
                if (listingError != null) {
                    onError("Failed to fetch listing data: ${listingError.localizedMessage}")
                    return@findInBackground
                }

                val prices = transactions?.mapNotNull { it.getDouble("amount") } ?: emptyList()
                val currentPrices =
                    listings?.mapNotNull {
                        it.getString("price")?.toDoubleOrNull()
                    } ?: emptyList()

                val averagePrice = if (prices.isNotEmpty()) prices.average() else 0.0
                val medianPrice =
                    if (prices.isNotEmpty()) {
                        val sorted = prices.sorted()
                        sorted[sorted.size / 2]
                    } else {
                        0.0
                    }

                val currentAveragePrice =
                    if (currentPrices.isNotEmpty()) currentPrices.average() else 0.0

                // Calculate price trend (simplified)
                val priceChange = currentAveragePrice - averagePrice
                val priceChangePercent =
                    if (averagePrice > 0) (priceChange / averagePrice) * 100 else 0.0

                val intelligence =
                    MarketIntelligence(
                        region = region ?: "All Regions",
                        breed = breed ?: "All Breeds",
                        averagePrice = averagePrice,
                        medianPrice = medianPrice,
                        currentAveragePrice = currentAveragePrice,
                        priceChange = priceChange,
                        priceChangePercent = priceChangePercent,
                        totalTransactions = transactions?.size ?: 0,
                        activeListings = listings?.size ?: 0,
                        marketVolume = prices.sum(),
                        demandScore =
                            calculateDemandScore(
                                listings?.size ?: 0,
                                transactions?.size ?: 0,
                            ),
                        lastUpdated = Date(),
                    )

                setLoading(false)
                onResult(intelligence)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

private fun calculateDemandScore(
    activeListings: Int,
    recentTransactions: Int,
): Double {
    return when {
        activeListings == 0 && recentTransactions == 0 -> 0.0
        activeListings == 0 -> 100.0 // High demand, no supply
        recentTransactions == 0 -> 20.0 // Low demand
        else -> {
            val ratio = recentTransactions.toDouble() / activeListings
            minOf(100.0, ratio * 50.0) // Scale to 0-100
        }
    }
}

// ===============================
// NOTIFICATION & ALERT FETCHERS
// ===============================

// Fetch personalized notifications and alerts
fun fetchNotificationCenter(
    userId: String? = null,
    onResult: (NotificationCenter) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    val currentUserId = userId ?: ParseUser.getCurrentUser()?.objectId

    if (currentUserId == null) {
        onError("User not authenticated")
        return
    }

    try {
        // Fetch user notifications
        val notificationQuery =
            ParseQuery.getQuery<ParseObject>("Notification").apply {
                whereEqualTo("userId", currentUserId)
                orderByDescending("createdAt")
                limit = 50
            }
        notificationQuery.findInBackground { notifications, notificationError ->
            if (notificationError != null) {
                onError("Failed to fetch notifications: ${notificationError.localizedMessage}")
                return@findInBackground
            }

            // Fetch system alerts
            val alertQuery =
                ParseQuery.getQuery<ParseObject>("SystemAlert").apply {
                    whereEqualTo("isActive", true)
                    orderByDescending("createdAt")
                    limit = 20
                }
            alertQuery.findInBackground { alerts, alertError ->
                if (alertError != null) {
                    onError("Failed to fetch alerts: ${alertError.localizedMessage}")
                    return@findInBackground
                }

                val userNotifications =
                    notifications?.mapNotNull { obj ->
                        try {
                            UserNotification(
                                notificationId = obj.objectId,
                                userId = obj.getString("userId") ?: "",
                                title = obj.getString("title") ?: "",
                                message = obj.getString("message") ?: "",
                                type = obj.getString("type") ?: "INFO",
                                isRead = obj.getBoolean("isRead"),
                                createdAt = obj.createdAt ?: Date(),
                                actionUrl = obj.getString("actionUrl"),
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()

                val systemAlerts =
                    alerts?.mapNotNull { obj ->
                        try {
                            SystemAlert(
                                alertId = obj.objectId,
                                title = obj.getString("title") ?: "",
                                message = obj.getString("message") ?: "",
                                severity = obj.getString("severity") ?: "INFO",
                                targetAudience = obj.getString("targetAudience") ?: "ALL",
                                isActive = obj.getBoolean("isActive"),
                                createdAt = obj.createdAt ?: Date(),
                                expiresAt = obj.getDate("expiresAt"),
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    } ?: emptyList()

                val center =
                    NotificationCenter(
                        userNotifications = userNotifications,
                        systemAlerts = systemAlerts,
                        unreadCount = userNotifications.count { !it.isRead },
                        lastUpdated = Date(),
                    )

                setLoading(false)
                onResult(center)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Data models for new fetchers
data class FarmManagementData(
    val farmerId: String,
    val totalFowl: Int,
    val healthyFowl: Int,
    val sickFowl: Int,
    val totalFeedConsumed: Double,
    val averageFeedPerFowl: Double,
    val recentHealthIssues: Int,
    val mortalityRate: Double,
    val productionEfficiency: Double,
)

// Removed duplicate VaccinationRecord

data class FarmBreedingRecord(
    val recordId: String,
    val farmerId: String,
    val maleId: String,
    val femaleId: String,
    val breedingDate: Date,
    val expectedHatchDate: Date?,
    val actualHatchDate: Date?,
    val eggsLaid: Int,
    val eggsHatched: Int,
    val hatchingRate: Double,
    val notes: String,
)

data class FinancialAnalytics(
    val farmerId: String,
    val periodStart: Date,
    val periodEnd: Date,
    val totalRevenue: Double,
    val totalExpenses: Double,
    val netProfit: Double,
    val profitMargin: Double,
    val monthlyBreakdown: List<FinancialMonth>,
)

data class FinancialMonth(
    val month: String,
    val revenue: Double,
    val expenses: Double,
    val profit: Double,
)

data class ExpenseCategory(
    val category: String,
    val amount: Double,
    val percentage: Double,
)

data class MarketIntelligence(
    val region: String,
    val breed: String,
    val averagePrice: Double,
    val medianPrice: Double,
    val currentAveragePrice: Double,
    val priceChange: Double,
    val priceChangePercent: Double,
    val totalTransactions: Int,
    val activeListings: Int,
    val marketVolume: Double,
    val demandScore: Double,
    val lastUpdated: Date,
)

data class NotificationCenter(
    val userNotifications: List<UserNotification>,
    val systemAlerts: List<SystemAlert>,
    val unreadCount: Int,
    val lastUpdated: Date,
)

data class UserNotification(
    val notificationId: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: Date,
    val actionUrl: String?,
)

data class SystemAlert(
    val alertId: String,
    val title: String,
    val message: String,
    val severity: String,
    val targetAudience: String,
    val isActive: Boolean,
    val createdAt: Date,
    val expiresAt: Date?,
)

// ===============================
// IOT ENHANCED FETCHERS
// ===============================

// Fetch IoT farm configuration details
fun fetchIoTFarmConfiguration(
    farmId: String,
    onResult: (IoTFarmConfiguration?) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("IoTFarmConfiguration").apply {
                whereEqualTo("farmId", farmId) // Assuming farmId is the link, not objectId
            }
        query.getFirstInBackground { obj, e ->
            setLoading(false)
            if (e != null) {
                if (e.code == ParseException.OBJECT_NOT_FOUND) {
                    onResult(null) // No configuration found, not an error
                } else {
                    onError(e.localizedMessage)
                }
            } else {
                val config =
                    obj?.let {
                        try {
                            val thresholdsMap = mutableMapOf<SensorType, Pair<Double, Double>>()
                            val parseThresholds = it.getJSONObject("alertThresholds")
                            parseThresholds?.keys()?.forEach { key ->
                                try {
                                    val sensorType = SensorType.valueOf(key)
                                    val range = parseThresholds.getJSONObject(key)
                                    val min = range.optDouble("min", Double.MIN_VALUE)
                                    val max = range.optDouble("max", Double.MAX_VALUE)
                                    thresholdsMap[sensorType] = Pair(min, max)
                                } catch (_: Exception) {
                                    // Skip invalid sensor types or formats
                                }
                            }

                            val notificationSettingsObj = it.getJSONObject("notificationSettings")
                            val notificationSettings =
                                notificationSettingsObj?.let { nsObj ->
                                    IoTNotificationSettings(
                                        enableEmailAlerts = nsObj.optBoolean("enableEmailAlerts", false),
                                        enableSMSAlerts = nsObj.optBoolean("enableSMSAlerts", false),
                                        enablePushNotifications =
                                            nsObj.optBoolean(
                                                "enablePushNotifications",
                                                true,
                                            ),
                                        quietHoursStart = nsObj.optString("quietHoursStart", "22:00"),
                                        quietHoursEnd = nsObj.optString("quietHoursEnd", "06:00"),
                                        alertSeverityThreshold =
                                            AlertLevel.valueOf(
                                                nsObj.optString(
                                                    "alertSeverityThreshold",
                                                    "MEDIUM",
                                                ),
                                            ),
                                    )
                                } ?: IoTNotificationSettings(
                                    true,
                                    false,
                                    true,
                                    "22:00",
                                    "06:00",
                                    AlertLevel.MEDIUM,
                                ) // Default

                            IoTFarmConfiguration(
                                farmId = it.getString("farmId") ?: "",
                                farmName = it.getString("farmName") ?: "",
                                location = it.getString("location") ?: "",
                                totalDevices = it.getInt("totalDevices"),
                                activeDevices = it.getInt("activeDevices"),
                                alertThresholds = thresholdsMap,
                                notificationSettings = notificationSettings,
                                lastUpdated = it.updatedAt ?: Date(),
                            )
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    }
                onResult(config)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch historical sensor data for a specific sensor
fun fetchSensorHistory(
    sensorId: String, // Assuming this is the Parse objectId of the IoTDevice
    hoursBack: Int = 7 * 24, // Default to 7 days
    onResult: (SensorHistory?) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("SensorData").apply {
                whereEqualTo("deviceId", sensorId) // Correct field for linking to IoTDevice
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.HOUR_OF_DAY, -hoursBack)
                whereGreaterThanOrEqualTo("timestamp", calendar.time)
                orderByAscending("timestamp")
                limit = 1000 // Max limit for Parse, consider pagination for more data
            }
        query.findInBackground { objects, e ->
            setLoading(false)
            if (e != null) {
                onError(e.localizedMessage)
            } else {
                val readings =
                    objects?.mapNotNull { obj ->
                        try {
                            SensorReading(
                                readingId = obj.objectId,
                                farmId = obj.getString("farmId") ?: "",
                                sensorType =
                                    SensorType.valueOf(
                                        obj.getString("sensorType") ?: "TEMPERATURE",
                                    ),
                                value = obj.getDouble("value"),
                                unit = obj.getString("unit") ?: "",
                                location = obj.getString("location") ?: "",
                                deviceId = obj.getString("deviceId") ?: "",
                                alertLevel =
                                    AlertLevel.valueOf(
                                        obj.getString("alertLevel") ?: "NORMAL",
                                    ),
                                timestamp = obj.getDate("timestamp") ?: Date(),
                                batteryLevel = obj.getDouble("batteryLevel"),
                                signalStrength = obj.getDouble("signalStrength"),
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid sensor or alert type in history: ${ex.message}")
                            null
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()

                if (readings.isNotEmpty()) {
                    val values = readings.map { it.value }
                    val avg = values.average()
                    val min = values.minOrNull() ?: 0.0
                    val max = values.maxOrNull() ?: 0.0
                    // Basic trend: more sophisticated analysis could be done in Cloud Code or client-side
                    val trend =
                        when {
                            readings.size < 2 -> TrendDirection.STABLE
                            readings.last().value > readings.first().value -> TrendDirection.INCREASING
                            readings.last().value < readings.first().value -> TrendDirection.DECREASING
                            else -> TrendDirection.STABLE
                        }
                    val history =
                        SensorHistory(
                            sensorId = sensorId,
                            readings = readings,
                            averageValue = avg,
                            minValue = min,
                            maxValue = max,
                            trendDirection = trend,
                            periodStart = readings.first().timestamp,
                            periodEnd = readings.last().timestamp,
                        )
                    onResult(history)
                } else {
                    onResult(null) // No history found
                }
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// ADDITIONAL MARKETPLACE FETCHERS
// ===============================

// Fetch details for a specific order
fun fetchOrderDetails(
    orderId: String,
    onResult: (OrderDetails?) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("Order")
                .apply { // Assuming "Order" is the Parse class name
                    whereEqualTo("objectId", orderId)
                    // include("listing") // If listing details are needed directly and not just ID
                    // include("buyer") // If buyer details are needed
                    // include("seller") // If seller details are needed
                }
        query.getFirstInBackground { obj, e ->
            setLoading(false)
            if (e != null) {
                if (e.code == ParseException.OBJECT_NOT_FOUND) {
                    onResult(null) // Order not found
                } else {
                    onError(e.localizedMessage)
                }
            } else {
                val order =
                    obj?.let {
                        try {
                            OrderDetails(
                                orderId = it.objectId,
                                listingId =
                                    it.getString("listingId")
                                        ?: "",
                                // or it.getParseObject("listing")?.objectId
                                buyerId = it.getParseUser("buyer")?.objectId ?: "",
                                sellerId =
                                    it.getParseUser("seller")?.objectId
                                        ?: "",
                                // May come from the Listing
                                quantity = it.getInt("quantity"),
                                unitPrice = it.getDouble("unitPrice"),
                                totalAmount = it.getDouble("totalAmount"),
                                paymentMethod =
                                    PaymentMethod.valueOf(
                                        it.getString("paymentMethod") ?: "COD",
                                    ),
                                deliveryAddress = it.getString("deliveryAddress"),
                                specialInstructions = it.getString("specialInstructions"),
                                orderStatus =
                                    OrderStatus.valueOf(
                                        it.getString("orderStatus") ?: "PENDING_PAYMENT",
                                    ),
                                createdAt = it.createdAt ?: Date(),
                                estimatedDelivery = it.getDate("estimatedDelivery"),
                            )
                        } catch (ex: IllegalArgumentException) {
                            onError("Invalid payment method or order status: ${ex.message}")
                            null
                        } catch (ex: Exception) {
                            onError(ex.localizedMessage)
                            null
                        }
                    }
                onResult(order)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// Fetch available payment options
// This might be a static list or configured via Parse Remote Config or a dedicated Parse class
fun fetchPaymentOptions(
    onResult: (List<PaymentOption>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    // For this example, we'll assume they are hardcoded or fetched from a simple Parse Class "PaymentConfig"
    // In a real app, this could be more dynamic, fetched from a "PaymentConfiguration" Parse class.
    try {
        // Placeholder: Returning a static list. Replace with actual Parse query if needed.
        val options =
            listOf(
                PaymentOption(
                    PaymentMethod.COD,
                    "Cash on Delivery",
                    true,
                    0.0,
                    "On Delivery",
                    "ic_cod",
                ),
                PaymentOption(
                    PaymentMethod.RAZORPAY,
                    "Razorpay (Cards, UPI, NetBanking)",
                    true,
                    0.0,
                    "Instant",
                    "ic_razorpay",
                ),
                PaymentOption(PaymentMethod.UPI, "UPI", true, 0.0, "Instant", "ic_upi"),
                // Add other payment methods as configured
            )
        setLoading(false)
        onResult(options)
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// AUCTION WINNER FETCHER
// ===============================

// Fetch the auction winner and backup bidders for a given auction
fun fetchAuctionWinner(
    auctionId: String,
    onResult: (AuctionWinner?) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    setLoading(true)
    try {
        val query =
            ParseQuery.getQuery<ParseObject>("AuctionWinner").apply {
                whereEqualTo("auctionId", auctionId)
                include("winnerUser") // if user pointer exists
            }
        query.getFirstInBackground { obj, e ->
            setLoading(false)
            if (e != null) {
                if (e.code == ParseException.OBJECT_NOT_FOUND) {
                    onResult(null)
                } else {
                    onError(e.localizedMessage)
                }
            } else if (obj != null) {
                try {
                    val winnerUser = obj.getParseUser("winnerUser")
                    // Parse backup bidders list
                    val backupRaw = obj.getList<Map<String, Any>>("backupBidders") ?: emptyList()
                    val backups =
                        backupRaw.mapNotNull { item ->
                            try {
                                BackupBidder(
                                    bidderId = item["bidderId"] as? String ?: "",
                                    bidderName = item["bidderName"] as? String ?: "",
                                    bidAmount = (item["bidAmount"] as? Number)?.toDouble() ?: 0.0,
                                    offerSentTime = (item["offerSentTime"] as? Date) ?: Date(),
                                    offerResponse =
                                        (item["offerResponse"] as? String)
                                            ?.let { OfferResponse.valueOf(it) },
                                    responseDeadline = (item["responseDeadline"] as? Date),
                                )
                            } catch (_: Exception) {
                                null
                            }
                        }
                    val auctionWinner =
                        AuctionWinner(
                            auctionId = obj.getString("auctionId") ?: "",
                            winnerId = winnerUser?.objectId ?: "",
                            winnerName = winnerUser?.username ?: "",
                            winningBid = obj.getDouble("winningBid"),
                            paymentDeadline = obj.getDate("paymentDeadline") ?: Date(),
                            paymentStatus =
                                AuctionPaymentStatus.valueOf(
                                    obj.getString("paymentStatus") ?: AuctionPaymentStatus.PENDING.name,
                                ),
                            backupBidders = backups,
                        )
                    onResult(auctionWinner)
                } catch (ex: Exception) {
                    onError(ex.localizedMessage)
                }
            } else {
                onResult(null)
            }
        }
    } catch (ex: Exception) {
        setLoading(false)
        onError(ex.localizedMessage)
    }
}

// ===============================
// COMMUNITY GROUPS FETCHER
// ===============================

// Fetch community groups, optionally filtered by type or region
fun fetchCommunityGroups(
    skip: Int = 0,
    limit: Int = 20,
    groupType: String? = null,
    region: String? = null,
    onResult: (List<CommunityGroup>) -> Unit,
    onError: (String?) -> Unit,
    setLoading: (Boolean) -> Unit,
) {
    Log.d("CommunityFetcher", "=== FETCH COMMUNITY GROUPS START ===")
    Log.d("CommunityFetcher", "Skip: $skip, Limit: $limit")
    Log.d("CommunityFetcher", "Group type: $groupType, Region: $region")

    setLoading(true)

    try {
        // Test if Parse is initialized and working
        Log.d("CommunityFetcher", "Testing Parse connection...")

        val query =
            ParseQuery.getQuery<ParseObject>("CommunityGroup").apply {
                groupType?.let { whereEqualTo("type", it) }
                region?.let { whereEqualTo("location", it) } // Updated to use 'location' field
                orderByDescending("memberCount") // Show most popular groups first
                setSkip(skip)
                setLimit(limit)
                // include("adminUser") // If admin user details are needed and part of the model
            }

        Log.d("CommunityFetcher", "Query created, executing findInBackground...")

        query.findInBackground { objects, e ->
            setLoading(false)

            Log.d("CommunityFetcher", "=== PARSE QUERY RESULT ===")
            Log.d("CommunityFetcher", "Error: $e")
            Log.d("CommunityFetcher", "Objects count: ${objects?.size ?: 0}")

            if (e != null) {
                Log.e("CommunityFetcher", "Parse query failed", e)
                onError("Failed to load community groups: ${e.localizedMessage}")
            } else {
                Log.d("CommunityFetcher", "Parse query successful")

                val groups =
                    objects?.mapNotNull { obj ->
                        try {
                            Log.d("CommunityFetcher", "Processing object: ${obj.objectId}")
                            Log.d(
                                "CommunityFetcher",
                                "Object data: name=${obj.getString("name")}, memberCount=${obj.getInt("memberCount")}, type=${
                                    obj.getString("type")
                                }",
                            )

                            CommunityGroup(
                                id = obj.objectId,
                                name = obj.getString("name") ?: "",
                                memberCount = obj.getInt("memberCount"),
                                type = obj.getString("type") ?: "public", // Default to public
                                // description = obj.getString("description"), // Add if in your model
                                // iconUrl = obj.getParseFile("icon")?.url // Add if in your model
                            )
                        } catch (ex: Exception) {
                            Log.e("CommunityFetcher", "Error processing community group object", ex)
                            onError(ex.localizedMessage)
                            null
                        }
                    } ?: emptyList()

                Log.d("CommunityFetcher", "Processed ${groups.size} groups successfully")
                groups.forEach { group ->
                    Log.d("CommunityFetcher", "Group: ${group.name} (${group.memberCount} members)")
                }

                // If no groups found, let's create some test data for debugging
                if (groups.isEmpty()) {
                    Log.w("CommunityFetcher", "No groups found in Parse database")
                    Log.w("CommunityFetcher", "This might indicate:")
                    Log.w("CommunityFetcher", "1. Empty CommunityGroup table")
                    Log.w("CommunityFetcher", "2. Parse server connection issues")
                    Log.w("CommunityFetcher", "3. Query filtering out all results")

                    // Return empty list - let UI handle showing appropriate message
                    onResult(emptyList())
                } else {
                    onResult(groups)
                }
            }

            Log.d("CommunityFetcher", "=== FETCH COMMUNITY GROUPS END ===")
        }
    } catch (ex: Exception) {
        Log.e("CommunityFetcher", "Exception in fetchCommunityGroups", ex)
        setLoading(false)
        onError("Network error: ${ex.localizedMessage}")
    }
}
