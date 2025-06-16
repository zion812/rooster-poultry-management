package com.example.rooster

import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class CampaignsService {
    companion object {
        private const val TAG = "CampaignsService"
        private const val MAX_CAMPAIGNS_EXCELLENT = 20
        private const val MAX_CAMPAIGNS_GOOD = 15
        private const val MAX_CAMPAIGNS_FAIR = 10
        private const val MAX_CAMPAIGNS_POOR = 5
        private const val MAX_CAMPAIGNS_OFFLINE = 3
    }

    // Fetch campaigns based on user role and network quality
    suspend fun fetchCampaigns(
        userRole: String = "general",
        region: String = "all",
        networkQuality: NetworkQualityLevel = NetworkQualityLevel.FAIR,
    ): List<Campaign> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Campaign")

                // Filter by active campaigns
                query.whereEqualTo("isActive", true)
                query.whereEqualTo("status", CampaignStatus.ACTIVE.name)

                // Filter by target audience
                if (userRole != "all") {
                    query.whereContains("targetAudience", userRole)
                }

                // Filter by region if specified
                if (region != "all") {
                    query.whereContains("regions", region)
                }

                // Filter by date range (active campaigns)
                val now = Date()
                query.whereLessThanOrEqualTo("startDate", now)
                query.whereGreaterThanOrEqualTo("endDate", now)

                // Network-adaptive query limits
                val limit =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> MAX_CAMPAIGNS_EXCELLENT
                        NetworkQualityLevel.GOOD -> MAX_CAMPAIGNS_GOOD
                        NetworkQualityLevel.FAIR -> MAX_CAMPAIGNS_FAIR
                        NetworkQualityLevel.POOR -> MAX_CAMPAIGNS_POOR
                        NetworkQualityLevel.OFFLINE -> MAX_CAMPAIGNS_OFFLINE
                    }
                query.limit = limit

                // Order by priority and creation date
                query.addDescendingOrder("priority")
                query.addDescendingOrder("createdDate")

                // Enable cache for poor networks
                if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.OFFLINE) {
                    query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                    query.maxCacheAge = 24 * 60 * 60 * 1000 // 24 hours
                }

                val results = query.find()
                results.map { it.toCampaign() }
            } catch (e: ParseException) {
                // Return sample campaigns if network fails
                if (networkQuality == NetworkQualityLevel.OFFLINE) {
                    getSampleCampaigns().take(MAX_CAMPAIGNS_OFFLINE)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Fetch campaigns by category
    suspend fun fetchCampaignsByCategory(
        category: CampaignCategory,
        networkQuality: NetworkQualityLevel = NetworkQualityLevel.FAIR,
    ): List<Campaign> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Campaign")
                query.whereEqualTo("isActive", true)
                query.whereEqualTo("status", CampaignStatus.ACTIVE.name)
                query.whereEqualTo("category", category.name)

                val limit =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> 15
                        NetworkQualityLevel.GOOD -> 10
                        NetworkQualityLevel.FAIR -> 8
                        NetworkQualityLevel.POOR -> 5
                        NetworkQualityLevel.OFFLINE -> 3
                    }
                query.limit = limit

                query.addDescendingOrder("priority")
                query.addDescendingOrder("createdDate")

                if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.OFFLINE) {
                    query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                    query.maxCacheAge = 12 * 60 * 60 * 1000 // 12 hours
                }

                val results = query.find()
                results.map { it.toCampaign() }
            } catch (e: ParseException) {
                getSampleCampaigns()
                    .filter { it.category == category }
                    .take(if (networkQuality == NetworkQualityLevel.OFFLINE) 2 else 5)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Track campaign interaction
    suspend fun trackCampaignInteraction(
        campaignId: String,
        interactionType: InteractionType,
        metadata: Map<String, String> = emptyMap(),
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val user = ParseUser.getCurrentUser()
                val userId = user?.objectId ?: "anonymous"

                // Create interaction record
                val interaction = ParseObject("CampaignInteraction")
                interaction.put("userId", userId)
                interaction.put("campaignId", campaignId)
                interaction.put("interactionType", interactionType.name)
                interaction.put("timestamp", Date())
                interaction.put("metadata", metadata)
                interaction.save()

                // Update campaign engagement metrics
                updateCampaignEngagement(campaignId, interactionType)

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // Update campaign engagement metrics
    private suspend fun updateCampaignEngagement(
        campaignId: String,
        interactionType: InteractionType,
    ) {
        try {
            val query = ParseQuery.getQuery<ParseObject>("Campaign")
            query.whereEqualTo("campaignId", campaignId)
            val campaign = query.first

            campaign?.let {
                when (interactionType) {
                    InteractionType.VIEW -> {
                        val currentViews = it.getInt("views")
                        it.put("views", currentViews + 1)
                        it.put("currentImpressions", it.getInt("currentImpressions") + 1)
                    }

                    InteractionType.CLICK -> {
                        val currentClicks = it.getInt("clicks")
                        it.put("clicks", currentClicks + 1)
                    }

                    InteractionType.SHARE -> {
                        val currentShares = it.getInt("shares")
                        it.put("shares", currentShares + 1)
                    }

                    InteractionType.LIKE -> {
                        val currentLikes = it.getInt("likes")
                        it.put("likes", currentLikes + 1)
                    }

                    InteractionType.ACTION_BUTTON_CLICK -> {
                        val currentActionClicks = it.getInt("actionButtonClicks")
                        it.put("actionButtonClicks", currentActionClicks + 1)
                    }

                    InteractionType.READ_MORE_CLICK -> {
                        val currentReadMoreClicks = it.getInt("readMoreClicks")
                        it.put("readMoreClicks", currentReadMoreClicks + 1)
                    }

                    else -> {
                        // Handle other interaction types if needed
                    }
                }

                // Update engagement rate
                updateEngagementRate(it)

                it.saveInBackground()
            }
        } catch (e: Exception) {
            // Silent fail for engagement tracking
        }
    }

    // Calculate and update engagement rate
    private fun updateEngagementRate(campaignObject: ParseObject) {
        try {
            val views = campaignObject.getInt("views")
            val clicks = campaignObject.getInt("clicks")
            val shares = campaignObject.getInt("shares")
            val likes = campaignObject.getInt("likes")
            val actionClicks = campaignObject.getInt("actionButtonClicks")

            val totalEngagements = clicks + shares + likes + actionClicks
            val engagementRate =
                if (views > 0) {
                    (totalEngagements.toDouble() / views.toDouble()) * 100
                } else {
                    0.0
                }

            campaignObject.put("engagementRate", engagementRate)

            // Calculate conversion rate (action button clicks / views)
            val conversionRate =
                if (views > 0) {
                    (actionClicks.toDouble() / views.toDouble()) * 100
                } else {
                    0.0
                }

            campaignObject.put("conversionRate", conversionRate)
        } catch (e: Exception) {
            // Silent fail
        }
    }

    // Get featured campaigns (high priority and high engagement)
    suspend fun getFeaturedCampaigns(networkQuality: NetworkQualityLevel = NetworkQualityLevel.FAIR): List<Campaign> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Campaign")
                query.whereEqualTo("isActive", true)
                query.whereEqualTo("status", CampaignStatus.ACTIVE.name)
                query.whereContainedIn(
                    "priority",
                    listOf(
                        CampaignPriority.HIGH.name,
                        CampaignPriority.URGENT.name,
                    ),
                )

                val limit =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> 10
                        NetworkQualityLevel.GOOD -> 8
                        NetworkQualityLevel.FAIR -> 6
                        NetworkQualityLevel.POOR -> 4
                        NetworkQualityLevel.OFFLINE -> 2
                    }
                query.limit = limit

                query.addDescendingOrder("engagementRate")
                query.addDescendingOrder("priority")

                if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.OFFLINE) {
                    query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                    query.maxCacheAge = 6 * 60 * 60 * 1000 // 6 hours
                }

                val results = query.find()
                results.map { it.toCampaign() }
            } catch (e: ParseException) {
                getSampleCampaigns()
                    .filter { it.priority == CampaignPriority.HIGH || it.priority == CampaignPriority.URGENT }
                    .take(if (networkQuality == NetworkQualityLevel.OFFLINE) 2 else 4)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Search campaigns
    suspend fun searchCampaigns(
        searchQuery: String,
        category: CampaignCategory? = null,
        networkQuality: NetworkQualityLevel = NetworkQualityLevel.FAIR,
    ): List<Campaign> {
        return withContext(Dispatchers.IO) {
            try {
                if (searchQuery.isBlank()) {
                    return@withContext emptyList<Campaign>()
                }

                val query = ParseQuery.getQuery<ParseObject>("Campaign")
                query.whereEqualTo("isActive", true)
                query.whereEqualTo("status", CampaignStatus.ACTIVE.name)

                // Search in title, description, and tags
                val titleQuery = ParseQuery.getQuery<ParseObject>("Campaign")
                titleQuery.whereContains("title", searchQuery)

                val titleTeluguQuery = ParseQuery.getQuery<ParseObject>("Campaign")
                titleTeluguQuery.whereContains("titleInTelugu", searchQuery)

                val descriptionQuery = ParseQuery.getQuery<ParseObject>("Campaign")
                descriptionQuery.whereContains("description", searchQuery)

                val tagsQuery = ParseQuery.getQuery<ParseObject>("Campaign")
                tagsQuery.whereContains("tags", searchQuery)

                val combinedQuery =
                    ParseQuery.or(
                        listOf(
                            titleQuery,
                            titleTeluguQuery,
                            descriptionQuery,
                            tagsQuery,
                        ),
                    )

                if (category != null) {
                    combinedQuery.whereEqualTo("category", category.name)
                }

                val limit =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> 15
                        NetworkQualityLevel.GOOD -> 12
                        NetworkQualityLevel.FAIR -> 8
                        NetworkQualityLevel.POOR -> 5
                        NetworkQualityLevel.OFFLINE -> 3
                    }
                combinedQuery.limit = limit

                combinedQuery.addDescendingOrder("priority")
                combinedQuery.addDescendingOrder("engagementRate")

                if (networkQuality == NetworkQualityLevel.POOR || networkQuality == NetworkQualityLevel.OFFLINE) {
                    combinedQuery.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                    combinedQuery.maxCacheAge = 4 * 60 * 60 * 1000 // 4 hours
                }

                val results = combinedQuery.find()
                results.map { it.toCampaign() }
            } catch (e: ParseException) {
                // Offline search in sample campaigns
                val sampleCampaigns = getSampleCampaigns()
                sampleCampaigns.filter { campaign ->
                    val matchesSearch =
                        campaign.title.contains(searchQuery, ignoreCase = true) ||
                            campaign.titleInTelugu.contains(searchQuery, ignoreCase = true) ||
                            campaign.description.contains(searchQuery, ignoreCase = true) ||
                            campaign.tags.any { it.contains(searchQuery, ignoreCase = true) }

                    val matchesCategory = category == null || campaign.category == category

                    matchesSearch && matchesCategory
                }.take(if (networkQuality == NetworkQualityLevel.OFFLINE) 3 else 5)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Create new campaign (for admin/high-level users)
    suspend fun createCampaign(campaign: Campaign): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = campaign.toParseObject()
                parseObject.save()
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // Update campaign status
    suspend fun updateCampaignStatus(
        campaignId: String,
        status: CampaignStatus,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Campaign")
                query.whereEqualTo("campaignId", campaignId)
                val campaign = query.first

                campaign?.let {
                    it.put("status", status.name)
                    it.put("lastUpdated", Date())
                    it.save()
                    true
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    // Get campaign analytics
    suspend fun getCampaignAnalytics(campaignId: String): CampaignEngagement? {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Campaign")
                query.whereEqualTo("campaignId", campaignId)
                val campaign = query.first

                campaign?.let {
                    CampaignEngagement(
                        views = it.getInt("views"),
                        clicks = it.getInt("clicks"),
                        shares = it.getInt("shares"),
                        likes = it.getInt("likes"),
                        comments = it.getInt("comments"),
                        actionButtonClicks = it.getInt("actionButtonClicks"),
                        readMoreClicks = it.getInt("readMoreClicks"),
                        conversionRate = it.getDouble("conversionRate"),
                        engagementRate = it.getDouble("engagementRate"),
                        reachCount = it.getInt("reachCount"),
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    // Populate sample campaigns to backend (for testing)
    suspend fun populateSampleCampaigns(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sampleCampaigns = getSampleCampaigns()
                sampleCampaigns.forEach { campaign ->
                    val parseObject = campaign.toParseObject()
                    parseObject.saveInBackground()
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
