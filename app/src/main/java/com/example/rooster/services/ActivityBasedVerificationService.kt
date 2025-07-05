package com.example.rooster.services

import com.example.rooster.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.roundToInt

/**
 * Activity-Based Verification Service implementing the verification refactor from TODO.md:
 * - Tracks farmer activity over ~1 month period
 * - Makes verification decisions based on activity patterns
 * - Integrals activity tracking with UI workflows
 * - Provides comprehensive verification scoring system
 */
class ActivityBasedVerificationService {
    companion object {
        private const val VERIFICATION_PERIOD_DAYS = 30
        private const val MIN_FOWL_MANAGEMENT_SCORE = 70
        private const val MIN_MARKETPLACE_SCORE = 60
        private const val MIN_COMMUNITY_SCORE = 50
        private const val MIN_OVERALL_SCORE = 65
    }

    /**
     * Comprehensive activity tracking data model
     */
    data class FarmerActivityProfile(
        val farmerId: String,
        val trackingStartDate: Date,
        val trackingEndDate: Date,
        val fowlManagementActivity: FowlManagementActivity,
        val marketplaceActivity: MarketplaceActivity,
        val communityActivity: CommunityActivity,
        val overallScore: Int,
        val verificationStatus: ActivityVerificationStatus,
        val verificationDate: Date?,
        val lastActivityDate: Date,
        val consistencyScore: Double,
        val authenticityScore: Double,
    )

    data class FowlManagementActivity(
        val fowlRecordsCreated: Int,
        val milestonesRecorded: Int,
        val healthRecordsUpdated: Int,
        val lineageDataComplete: Boolean,
        val photoUploadsCount: Int,
        val updateFrequency: Double, // Updates per week
        val dataQualityScore: Int, // 0-100
    )

    data class MarketplaceActivity(
        val listingsCreated: Int,
        val successfulSales: Int,
        val purchasesMade: Int,
        val auctionParticipation: Int,
        val customerFeedbackScore: Double,
        val transactionReliability: Double,
        val priceConsistency: Boolean,
    )

    data class CommunityActivity(
        val postsCreated: Int,
        val commentsPosted: Int,
        val helpfulAnswers: Int,
        val knowledgeSharing: Int,
        val eventParticipation: Int,
        val reputationScore: Int,
        val communityStanding: CommunityStanding,
    )

    enum class ActivityVerificationStatus {
        PENDING_TRACKING,
        UNDER_REVIEW,
        VERIFIED_BASIC,
        VERIFIED_ADVANCED,
        VERIFIED_EXPERT,
        REJECTED_INSUFFICIENT_ACTIVITY,
        REJECTED_SUSPICIOUS_PATTERNS,
        REJECTED_FAKE_DATA,
    }

    enum class CommunityStanding {
        NEWCOMER,
        REGULAR,
        RESPECTED,
        EXPERT,
        COMMUNITY_LEADER,
    }

    /**
     * Initiates activity tracking for a farmer
     */
    suspend fun startActivityTracking(farmerId: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val trackingObject =
                    ParseObject("FarmerActivityTracking").apply {
                        put("farmerId", farmerId)
                        put("trackingStartDate", Date())
                        put(
                            "trackingEndDate",
                            Date(System.currentTimeMillis() + VERIFICATION_PERIOD_DAYS * 24 * 60 * 60 * 1000L),
                        )
                        put("status", ActivityVerificationStatus.PENDING_TRACKING.name)
                        put("fowlManagementScore", 0)
                        put("marketplaceScore", 0)
                        put("communityScore", 0)
                        put("overallScore", 0)
                        put("isActive", true)
                    }

                trackingObject.save()

                FirebaseCrashlytics.getInstance()
                    .log("Activity tracking started for farmer: $farmerId")
                Result.success(trackingObject.objectId)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Records a specific farmer activity
     */
    suspend fun recordFarmerActivity(
        farmerId: String,
        activityType: FarmerActivityType,
        activityData: Map<String, Any>,
    ): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val activityRecord =
                    ParseObject("FarmerActivityRecord").apply {
                        put("farmerId", farmerId)
                        put("activityType", activityType.name)
                        put("activityData", activityData)
                        put("timestamp", Date())
                        put("isAuthentic", validateActivityAuthenticity(activityType, activityData))
                        put("qualityScore", calculateActivityQuality(activityType, activityData))
                    }

                activityRecord.save()

                // Update tracking progress
                updateTrackingProgress(farmerId, activityType)

                FirebaseCrashlytics.getInstance()
                    .log("Activity recorded: $activityType for farmer $farmerId")
                Result.success(true)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Calculates comprehensive activity profile for verification decision
     */
    suspend fun calculateActivityProfile(farmerId: String): Result<FarmerActivityProfile> =
        withContext(Dispatchers.IO) {
            try {
                val trackingPeriod =
                    Date(System.currentTimeMillis() - VERIFICATION_PERIOD_DAYS * 24 * 60 * 60 * 1000L)

                // Fetch all activities in the tracking period
                val fowlActivity = calculateFowlManagementActivity(farmerId, trackingPeriod)
                val marketplaceActivity = calculateMarketplaceActivity(farmerId, trackingPeriod)
                val communityActivity = calculateCommunityActivity(farmerId, trackingPeriod)

                // Calculate scores
                val fowlManagementScore = calculateFowlManagementScore(fowlActivity)
                val marketplaceScore = calculateMarketplaceScore(marketplaceActivity)
                val communityScore = calculateCommunityScore(communityActivity)
                val overallScore =
                    calculateOverallScore(fowlManagementScore, marketplaceScore, communityScore)

                // Determine verification status
                val verificationStatus =
                    determineVerificationStatus(
                        fowlManagementScore,
                        marketplaceScore,
                        communityScore,
                        overallScore,
                    )

                val profile =
                    FarmerActivityProfile(
                        farmerId = farmerId,
                        trackingStartDate = trackingPeriod,
                        trackingEndDate = Date(),
                        fowlManagementActivity = fowlActivity,
                        marketplaceActivity = marketplaceActivity,
                        communityActivity = communityActivity,
                        overallScore = overallScore,
                        verificationStatus = verificationStatus,
                        verificationDate = if (verificationStatus.name.startsWith("VERIFIED")) Date() else null,
                        lastActivityDate = getLastActivityDate(farmerId),
                        consistencyScore = calculateConsistencyScore(farmerId, trackingPeriod),
                        authenticityScore = calculateAuthenticityScore(farmerId, trackingPeriod),
                    )

                // Save profile to database
                saveActivityProfile(profile)

                Result.success(profile)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(e)
            }
        }

    /**
     * Gets current verification status for a farmer
     */
    suspend fun getVerificationStatus(farmerId: String): Result<ActivityVerificationStatus> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("FarmerActivityProfile")
                query.whereEqualTo("farmerId", farmerId)
                query.orderByDescending("createdAt")

                val profile = query.find().firstOrNull()
                val status =
                    if (profile != null) {
                        ActivityVerificationStatus.valueOf(
                            profile.getString("verificationStatus") ?: "PENDING_TRACKING",
                        )
                    } else {
                        ActivityVerificationStatus.PENDING_TRACKING
                    }

                Result.success(status)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to get verification status"))
            }
        }

    // Private helper methods

    private suspend fun calculateFowlManagementActivity(
        farmerId: String,
        since: Date,
    ): FowlManagementActivity {
        return try {
            // Query fowl records
            val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
            fowlQuery.whereEqualTo("ownerId", farmerId)
            fowlQuery.whereGreaterThan("createdAt", since)
            val fowlRecords = fowlQuery.find()

            // Query milestones
            val milestoneQuery = ParseQuery.getQuery<ParseObject>("FowlMilestone")
            milestoneQuery.whereEqualTo("farmerId", farmerId)
            milestoneQuery.whereGreaterThan("createdAt", since)
            val milestones = milestoneQuery.find()

            // Query health records
            val healthQuery = ParseQuery.getQuery<ParseObject>("HealthRecord")
            healthQuery.whereEqualTo("farmerId", farmerId)
            healthQuery.whereGreaterThan("createdAt", since)
            val healthRecords = healthQuery.find()

            FowlManagementActivity(
                fowlRecordsCreated = fowlRecords.size,
                milestonesRecorded = milestones.size,
                healthRecordsUpdated = healthRecords.size,
                lineageDataComplete = checkLineageCompleteness(fowlRecords),
                photoUploadsCount = countPhotoUploads(fowlRecords),
                updateFrequency = calculateUpdateFrequency(farmerId, since),
                dataQualityScore = calculateDataQuality(fowlRecords, milestones, healthRecords),
            )
        } catch (e: Exception) {
            FowlManagementActivity(0, 0, 0, false, 0, 0.0, 0)
        }
    }

    private suspend fun calculateMarketplaceActivity(
        farmerId: String,
        since: Date,
    ): MarketplaceActivity {
        return try {
            // Query listings
            val listingsQuery = ParseQuery.getQuery<ParseObject>("Listing")
            listingsQuery.whereEqualTo("sellerId", farmerId)
            listingsQuery.whereGreaterThan("createdAt", since)
            val listings = listingsQuery.find()

            // Query transactions
            val transactionQuery = ParseQuery.getQuery<ParseObject>("Transaction")
            transactionQuery.whereEqualTo("sellerId", farmerId)
            transactionQuery.whereGreaterThan("createdAt", since)
            val transactions = transactionQuery.find()

            MarketplaceActivity(
                listingsCreated = listings.size,
                successfulSales = transactions.count { it.getString("status") == "COMPLETED" },
                purchasesMade = countPurchases(farmerId, since),
                auctionParticipation = countAuctionParticipation(farmerId, since),
                customerFeedbackScore = calculateFeedbackScore(farmerId),
                transactionReliability = calculateTransactionReliability(transactions),
                priceConsistency = checkPriceConsistency(listings),
            )
        } catch (e: Exception) {
            MarketplaceActivity(0, 0, 0, 0, 0.0, 0.0, false)
        }
    }

    private suspend fun calculateCommunityActivity(
        farmerId: String,
        since: Date,
    ): CommunityActivity {
        return try {
            // Query posts
            val postsQuery = ParseQuery.getQuery<ParseObject>("Post")
            postsQuery.whereEqualTo("authorId", farmerId)
            postsQuery.whereGreaterThan("createdAt", since)
            val posts = postsQuery.find()

            // Query comments
            val commentsQuery = ParseQuery.getQuery<ParseObject>("Comment")
            commentsQuery.whereEqualTo("authorId", farmerId)
            commentsQuery.whereGreaterThan("createdAt", since)
            val comments = commentsQuery.find()

            CommunityActivity(
                postsCreated = posts.size,
                commentsPosted = comments.size,
                helpfulAnswers = countHelpfulAnswers(comments),
                knowledgeSharing = countKnowledgeSharing(posts),
                eventParticipation = countEventParticipation(farmerId, since),
                reputationScore = calculateReputationScore(farmerId),
                communityStanding = determineCommunityStanding(farmerId),
            )
        } catch (e: Exception) {
            CommunityActivity(0, 0, 0, 0, 0, 0, CommunityStanding.NEWCOMER)
        }
    }

    private fun calculateFowlManagementScore(activity: FowlManagementActivity): Int {
        var score = 0

        // Fowl records (0-25 points)
        score += minOf(25, activity.fowlRecordsCreated * 5)

        // Milestones (0-25 points)
        score += minOf(25, activity.milestonesRecorded * 3)

        // Health records (0-20 points)
        score += minOf(20, activity.healthRecordsUpdated * 4)

        // Lineage completeness (0-15 points)
        if (activity.lineageDataComplete) score += 15

        // Photo uploads (0-10 points)
        score += minOf(10, activity.photoUploadsCount * 2)

        // Data quality (0-5 points)
        score += (activity.dataQualityScore * 0.05).roundToInt()

        return minOf(100, score)
    }

    private fun calculateMarketplaceScore(activity: MarketplaceActivity): Int {
        var score = 0

        // Listings created (0-30 points)
        score += minOf(30, activity.listingsCreated * 6)

        // Successful sales (0-25 points)
        score += minOf(25, activity.successfulSales * 8)

        // Customer feedback (0-20 points)
        score += (activity.customerFeedbackScore * 4).roundToInt()

        // Transaction reliability (0-15 points)
        score += (activity.transactionReliability * 15).roundToInt()

        // Auction participation (0-10 points)
        score += minOf(10, activity.auctionParticipation * 3)

        return minOf(100, score)
    }

    private fun calculateCommunityScore(activity: CommunityActivity): Int {
        var score = 0

        // Posts created (0-25 points)
        score += minOf(25, activity.postsCreated * 5)

        // Comments posted (0-20 points)
        score += minOf(20, activity.commentsPosted * 2)

        // Helpful answers (0-20 points)
        score += minOf(20, activity.helpfulAnswers * 4)

        // Knowledge sharing (0-15 points)
        score += minOf(15, activity.knowledgeSharing * 3)

        // Event participation (0-10 points)
        score += minOf(10, activity.eventParticipation * 5)

        // Community standing (0-10 points)
        score +=
            when (activity.communityStanding) {
                CommunityStanding.NEWCOMER -> 2
                CommunityStanding.REGULAR -> 4
                CommunityStanding.RESPECTED -> 6
                CommunityStanding.EXPERT -> 8
                CommunityStanding.COMMUNITY_LEADER -> 10
            }

        return minOf(100, score)
    }

    private fun calculateOverallScore(
        fowlScore: Int,
        marketplaceScore: Int,
        communityScore: Int,
    ): Int {
        // Weighted average: Fowl management (50%), Marketplace (30%), Community (20%)
        return ((fowlScore * 0.5) + (marketplaceScore * 0.3) + (communityScore * 0.2)).roundToInt()
    }

    private fun determineVerificationStatus(
        fowlScore: Int,
        marketplaceScore: Int,
        communityScore: Int,
        overallScore: Int,
    ): ActivityVerificationStatus {
        return when {
            overallScore >= 90 && fowlScore >= 85 && marketplaceScore >= 80 ->
                ActivityVerificationStatus.VERIFIED_EXPERT

            overallScore >= MIN_OVERALL_SCORE + 15 && fowlScore >= MIN_FOWL_MANAGEMENT_SCORE + 10 ->
                ActivityVerificationStatus.VERIFIED_ADVANCED

            overallScore >= MIN_OVERALL_SCORE && fowlScore >= MIN_FOWL_MANAGEMENT_SCORE &&
                marketplaceScore >= MIN_MARKETPLACE_SCORE && communityScore >= MIN_COMMUNITY_SCORE ->
                ActivityVerificationStatus.VERIFIED_BASIC

            overallScore < 30 || fowlScore < 20 ->
                ActivityVerificationStatus.REJECTED_INSUFFICIENT_ACTIVITY

            else -> ActivityVerificationStatus.UNDER_REVIEW
        }
    }

    // Additional helper methods for activity tracking
    private fun validateActivityAuthenticity(
        activityType: FarmerActivityType,
        data: Map<String, Any>,
    ): Boolean {
        // Basic authenticity checks - can be enhanced with ML models
        return when (activityType) {
            FarmerActivityType.FOWL_RECORD_CREATED -> {
                data.containsKey("breed") && data.containsKey("birthDate")
            }

            FarmerActivityType.MILESTONE_RECORDED -> {
                data.containsKey("milestoneType") && data.containsKey("fowlId")
            }

            FarmerActivityType.LISTING_CREATED -> {
                data.containsKey("price") && data.containsKey("description")
            }

            else -> true
        }
    }

    private fun calculateActivityQuality(
        activityType: FarmerActivityType,
        data: Map<String, Any>,
    ): Int {
        // Quality scoring based on completeness and consistency
        var quality = 50 // Base quality

        // Add points for completeness
        quality += data.keys.size * 5

        // Add points for data richness (photos, detailed descriptions, etc.)
        if (data.containsKey("photoUrl")) quality += 10
        if (data.containsKey("description") && (data["description"] as? String)?.length ?: 0 > 50) quality += 10

        return minOf(100, quality)
    }

    private suspend fun updateTrackingProgress(
        farmerId: String,
        activityType: FarmerActivityType,
    ) {
        try {
            val query = ParseQuery.getQuery<ParseObject>("FarmerActivityTracking")
            query.whereEqualTo("farmerId", farmerId)
            query.whereEqualTo("isActive", true)

            val tracking = query.find().firstOrNull()
            tracking?.let { track ->
                val currentCount =
                    track.getNumber("${activityType.name.lowercase()}_count")?.toInt() ?: 0
                track.put("${activityType.name.lowercase()}_count", currentCount + 1)
                track.put("lastActivityDate", Date())
                track.save()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun saveActivityProfile(profile: FarmerActivityProfile) {
        try {
            val profileObject =
                ParseObject("FarmerActivityProfile").apply {
                    put("farmerId", profile.farmerId)
                    put("trackingStartDate", profile.trackingStartDate)
                    put("trackingEndDate", profile.trackingEndDate)
                    put("overallScore", profile.overallScore)
                    put("verificationStatus", profile.verificationStatus.name)
                    put("verificationDate", profile.verificationDate ?: Date(0))
                    put("lastActivityDate", profile.lastActivityDate)
                    put("consistencyScore", profile.consistencyScore)
                    put("authenticityScore", profile.authenticityScore)

                    // Fowl management activity
                    put("fowlRecordsCreated", profile.fowlManagementActivity.fowlRecordsCreated)
                    put("milestonesRecorded", profile.fowlManagementActivity.milestonesRecorded)
                    put("healthRecordsUpdated", profile.fowlManagementActivity.healthRecordsUpdated)
                    put("lineageDataComplete", profile.fowlManagementActivity.lineageDataComplete)

                    // Marketplace activity
                    put("listingsCreated", profile.marketplaceActivity.listingsCreated)
                    put("successfulSales", profile.marketplaceActivity.successfulSales)
                    put("customerFeedbackScore", profile.marketplaceActivity.customerFeedbackScore)

                    // Community activity
                    put("postsCreated", profile.communityActivity.postsCreated)
                    put("commentsPosted", profile.communityActivity.commentsPosted)
                    put("reputationScore", profile.communityActivity.reputationScore)
                    put("communityStanding", profile.communityActivity.communityStanding.name)
                }

            profileObject.save()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // Placeholder implementations for complex calculations
    private fun checkLineageCompleteness(fowlRecords: List<ParseObject>): Boolean =
        fowlRecords.any {
            !it.getString("fatherId").isNullOrEmpty() && !it.getString("motherId").isNullOrEmpty()
        }

    private fun countPhotoUploads(fowlRecords: List<ParseObject>): Int =
        fowlRecords.count {
            !it.getString("imageUrl").isNullOrEmpty()
        }

    private suspend fun calculateUpdateFrequency(
        farmerId: String,
        since: Date,
    ): Double = 2.5 // Mock frequency

    private fun calculateDataQuality(
        fowlRecords: List<ParseObject>,
        milestones: List<ParseObject>,
        healthRecords: List<ParseObject>,
    ): Int = 75

    private suspend fun countPurchases(
        farmerId: String,
        since: Date,
    ): Int = 0 // Implementation needed

    private suspend fun countAuctionParticipation(
        farmerId: String,
        since: Date,
    ): Int = 0 // Implementation needed

    private suspend fun calculateFeedbackScore(farmerId: String): Double = 4.2 // Mock score

    private fun calculateTransactionReliability(transactions: List<ParseObject>): Double = 0.85

    private fun checkPriceConsistency(listings: List<ParseObject>): Boolean = true

    private fun countHelpfulAnswers(comments: List<ParseObject>): Int = comments.count { it.getNumber("likes")?.toInt() ?: 0 > 3 }

    private fun countKnowledgeSharing(posts: List<ParseObject>): Int =
        posts.count {
            it.getList<String>("tags")?.contains("knowledge") == true
        }

    private suspend fun countEventParticipation(
        farmerId: String,
        since: Date,
    ): Int = 1 // Implementation needed

    private suspend fun calculateReputationScore(farmerId: String): Int = 75 // Implementation needed

    private suspend fun determineCommunityStanding(farmerId: String): CommunityStanding = CommunityStanding.REGULAR

    private suspend fun getLastActivityDate(farmerId: String): Date = Date()

    private suspend fun calculateConsistencyScore(
        farmerId: String,
        since: Date,
    ): Double = 0.8

    private suspend fun calculateAuthenticityScore(
        farmerId: String,
        since: Date,
    ): Double = 0.9
}

enum class FarmerActivityType {
    FOWL_RECORD_CREATED,
    MILESTONE_RECORDED,
    HEALTH_RECORD_UPDATED,
    LISTING_CREATED,
    SALE_COMPLETED,
    PURCHASE_MADE,
    AUCTION_PARTICIPATED,
    COMMUNITY_POST_CREATED,
    COMMUNITY_COMMENT_POSTED,
    EVENT_PARTICIPATED,
    KNOWLEDGE_SHARED,
    PHOTO_UPLOADED,
}
