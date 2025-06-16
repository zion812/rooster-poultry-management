package com.example.rooster

import androidx.compose.ui.graphics.Color
import com.parse.ParseObject
import java.util.*

// Campaign Data Models for Awareness and Education

data class Campaign(
    val id: String,
    val title: String,
    val titleInTelugu: String,
    val description: String,
    val descriptionInTelugu: String,
    val category: CampaignCategory,
    val priority: CampaignPriority,
    val status: CampaignStatus,
    val targetAudience: List<String>, // "farmers", "general", "high-level", "all"
    val regions: List<String>, // Telangana, Andhra Pradesh, etc.
    val startDate: Date,
    val endDate: Date,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val actionButtonText: String? = null,
    val actionButtonTextTelugu: String? = null,
    val actionUrl: String? = null,
    val readMoreUrl: String? = null,
    val tags: List<String> = emptyList(),
    val engagement: CampaignEngagement = CampaignEngagement(),
    val isSponsored: Boolean = false,
    val sponsorName: String? = null,
    val createdDate: Date = Date(),
    val lastUpdated: Date = Date(),
    val createdBy: String,
    val isActive: Boolean = true,
    val maxImpressions: Int? = null,
    val currentImpressions: Int = 0,
)

enum class CampaignCategory(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: String,
) {
    HEALTH_AWARENESS(
        "Health Awareness",
        "ఆరోగ్య అవగాహన",
        Color(0xFF4CAF50),
        "🏥",
    ),
    BREED_EDUCATION(
        "Breed Education",
        "జాతుల విద్య",
        Color(0xFF2196F3),
        "🐓",
    ),
    MARKET_TRENDS(
        "Market Trends",
        "మార్కెట్ ట్రెండ్లు",
        Color(0xFFFF9800),
        "📈",
    ),
    CULTURAL_PRESERVATION(
        "Cultural Preservation",
        "సాంస్కృతిక సంరక్షణ",
        Color(0xFF9C27B0),
        "🎭",
    ),
    GOVERNMENT_SCHEMES(
        "Government Schemes",
        "ప్రభుత్వ పథకాలు",
        Color(0xFF607D8B),
        "🏛️",
    ),
    TECHNOLOGY_ADOPTION(
        "Technology Adoption",
        "సాంకేతిక వినియోగం",
        Color(0xFF00BCD4),
        "📱",
    ),
    ENVIRONMENTAL_CARE(
        "Environmental Care",
        "పర్యావరణ సంరక్షణ",
        Color(0xFF8BC34A),
        "🌱",
    ),
    COMMUNITY_BUILDING(
        "Community Building",
        "కమ్యూనిటీ నిర్మాణం",
        Color(0xFFE91E63),
        "👥",
    ),
}

enum class CampaignPriority(val displayName: String, val color: Color) {
    LOW("Low Priority", Color(0xFF4CAF50)),
    MEDIUM("Medium Priority", Color(0xFFFF9800)),
    HIGH("High Priority", Color(0xFFFF5722)),
    URGENT("Urgent", Color(0xFFD32F2F)),
}

enum class CampaignStatus(val displayName: String, val color: Color) {
    DRAFT("Draft", Color(0xFF757575)),
    SCHEDULED("Scheduled", Color(0xFF2196F3)),
    ACTIVE("Active", Color(0xFF4CAF50)),
    PAUSED("Paused", Color(0xFFFF9800)),
    COMPLETED("Completed", Color(0xFF9C27B0)),
    CANCELLED("Cancelled", Color(0xFFD32F2F)),
}

data class CampaignEngagement(
    val views: Int = 0,
    val clicks: Int = 0,
    val shares: Int = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val actionButtonClicks: Int = 0,
    val readMoreClicks: Int = 0,
    val conversionRate: Double = 0.0,
    val engagementRate: Double = 0.0,
    val reachCount: Int = 0,
    val uniqueUsers: Set<String> = emptySet(),
)

data class CampaignInteraction(
    val userId: String,
    val campaignId: String,
    val interactionType: InteractionType,
    val timestamp: Date = Date(),
    val metadata: Map<String, String> = emptyMap(),
)

enum class InteractionType {
    VIEW,
    CLICK,
    SHARE,
    LIKE,
    COMMENT,
    ACTION_BUTTON_CLICK,
    READ_MORE_CLICK,
    DISMISS,
    REPORT,
    SAVE,
}

// Sample campaigns for different categories
fun getSampleCampaigns(): List<Campaign> =
    listOf(
        Campaign(
            id = "health_001",
            title = "Prevent Newcastle Disease",
            titleInTelugu = "న్యూకాజిల్ వ్యాధిని నివారించండి",
            description = "Learn how to protect your flock from Newcastle Disease with proper vaccination schedules and hygiene practices.",
            descriptionInTelugu = "సరైన టీకా వేయింపు మరియు పరిశుభ్రత పద్ధతులతో న్యూకాజిల్ వ్యాధి నుండి మీ పక్షుల మందను ఎలా రక్షించుకోవాలో తెలుసుకోండి.",
            category = CampaignCategory.HEALTH_AWARENESS,
            priority = CampaignPriority.HIGH,
            status = CampaignStatus.ACTIVE,
            targetAudience = listOf("farmers", "general"),
            regions = listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu"),
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000), // 30 days
            actionButtonText = "Learn Vaccination Schedule",
            actionButtonTextTelugu = "టీకా వేయింపు షెడ్యూల్ తెలుసుకోండి",
            tags = listOf("health", "vaccination", "prevention", "rural"),
            createdBy = "health_expert_001",
        ),
        Campaign(
            id = "breed_001",
            title = "Kadaknath: The Black Chicken Wonder",
            titleInTelugu = "కడక్నాత్: నల్ల కోడి అద్భుతం",
            description = "Discover the nutritional benefits and market value of the famous Kadaknath breed. High protein, medicinal properties, and premium pricing.",
            descriptionInTelugu = "ప్రసిద్ధ కడక్నాత్ జాతి యొక్క పోషక ప్రయోజనాలు మరియు మార్కెట్ విలువను కనుగొనండి. అధిక ప్రోటీన్, ఔషధ గుణాలు మరియు ప్రీమియం ధర.",
            category = CampaignCategory.BREED_EDUCATION,
            priority = CampaignPriority.MEDIUM,
            status = CampaignStatus.ACTIVE,
            targetAudience = listOf("all"),
            regions = listOf("all"),
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 60L * 24 * 60 * 60 * 1000), // 60 days
            actionButtonText = "View Breeding Guide",
            actionButtonTextTelugu = "పెంపకం గైడ్ చూడండి",
            tags = listOf("kadaknath", "breed", "nutrition", "premium"),
            createdBy = "breed_expert_001",
        ),
        Campaign(
            id = "market_001",
            title = "Festival Season Price Surge",
            titleInTelugu = "పండుగ కాలంలో ధరల పెరుగుదల",
            description = "Prepare for the upcoming festival season! Cockfight competitions and traditional celebrations are driving up demand and prices.",
            descriptionInTelugu = "రాబోయే పండుగ సీజన్ కోసం సిద్ధంగా ఉండండి! కోడిపోట్లు మరియు సాంప్రదాయ వేడుకలు డిమాండ్ మరియు ధరలను పెంచుతున్నాయి.",
            category = CampaignCategory.MARKET_TRENDS,
            priority = CampaignPriority.HIGH,
            status = CampaignStatus.ACTIVE,
            targetAudience = listOf("farmers", "general"),
            regions = listOf("Telangana", "Andhra Pradesh"),
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 15L * 24 * 60 * 60 * 1000), // 15 days
            actionButtonText = "Check Current Prices",
            actionButtonTextTelugu = "ప్రస్తుత ధరలు చూడండి",
            tags = listOf("festival", "sankranti", "prices", "demand"),
            createdBy = "market_analyst_001",
        ),
        Campaign(
            id = "govt_001",
            title = "Poultry Development Scheme 2025",
            titleInTelugu = "కోడిపెంపకం అభివృద్ధి పథకం 2025",
            description = "Government subsidy available for poultry farmers! Get up to ₹50,000 assistance for setting up modern poultry units.",
            descriptionInTelugu = "కోడిపెంపకం చేసే రైతులకు ప్రభుత్వ సబ్సిడీ అందుబాటులో! ఆధునిక కోడిపెంపకం యూనిట్లు ఏర్పాటు చేయడానికి ₹50,000 వరకు సహాయం పొందండి.",
            category = CampaignCategory.GOVERNMENT_SCHEMES,
            priority = CampaignPriority.HIGH,
            status = CampaignStatus.ACTIVE,
            targetAudience = listOf("farmers"),
            regions = listOf("Telangana", "Andhra Pradesh", "Karnataka"),
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000), // 90 days
            actionButtonText = "Apply Now",
            actionButtonTextTelugu = "ఇప్పుడే దరఖాస్తు చేయండి",
            tags = listOf("government", "subsidy", "assistance", "modern"),
            createdBy = "govt_official_001",
        ),
        Campaign(
            id = "cultural_001",
            title = "Preserve Traditional Breeds",
            titleInTelugu = "సాంప్రదాయ జాతులను కాపాడండి",
            description = "Help preserve our heritage breeds like Asil, Chittagong, and local varieties. These breeds are part of our cultural identity.",
            descriptionInTelugu = "అసిల్, చిట్టగాంగ్ మరియు స్థానిక రకాలు వంటి మన వారసత్వ జాతులను కాపాడటంలో సహాయపడండి. ఈ జాతులు మన సాంస్కృతిక గుర్తింపులో భాగం.",
            category = CampaignCategory.CULTURAL_PRESERVATION,
            priority = CampaignPriority.MEDIUM,
            status = CampaignStatus.ACTIVE,
            targetAudience = listOf("all"),
            regions = listOf("all"),
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 180L * 24 * 60 * 60 * 1000), // 180 days
            actionButtonText = "Join Preservation Program",
            actionButtonTextTelugu = "సంరక్షణ కార్యక్రమంలో చేరండి",
            tags = listOf("traditional", "heritage", "asil", "preservation"),
            createdBy = "cultural_expert_001",
        ),
    )

// Parse conversion functions
fun Campaign.toParseObject(): ParseObject {
    val parseObject = ParseObject("Campaign")
    parseObject.put("campaignId", id)
    parseObject.put("title", title)
    parseObject.put("titleInTelugu", titleInTelugu)
    parseObject.put("description", description)
    parseObject.put("descriptionInTelugu", descriptionInTelugu)
    parseObject.put("category", category.name)
    parseObject.put("priority", priority.name)
    parseObject.put("status", status.name)
    parseObject.put("targetAudience", targetAudience)
    parseObject.put("regions", regions)
    parseObject.put("startDate", startDate)
    parseObject.put("endDate", endDate)
    imageUrl?.let { parseObject.put("imageUrl", it) }
    videoUrl?.let { parseObject.put("videoUrl", it) }
    actionButtonText?.let { parseObject.put("actionButtonText", it) }
    actionButtonTextTelugu?.let { parseObject.put("actionButtonTextTelugu", it) }
    actionUrl?.let { parseObject.put("actionUrl", it) }
    readMoreUrl?.let { parseObject.put("readMoreUrl", it) }
    parseObject.put("tags", tags)
    parseObject.put("isSponsored", isSponsored)
    sponsorName?.let { parseObject.put("sponsorName", it) }
    parseObject.put("createdDate", createdDate)
    parseObject.put("lastUpdated", lastUpdated)
    parseObject.put("createdBy", createdBy)
    parseObject.put("isActive", isActive)
    maxImpressions?.let { parseObject.put("maxImpressions", it) }
    parseObject.put("currentImpressions", currentImpressions)

    // Engagement metrics
    parseObject.put("views", engagement.views)
    parseObject.put("clicks", engagement.clicks)
    parseObject.put("shares", engagement.shares)
    parseObject.put("likes", engagement.likes)
    parseObject.put("comments", engagement.comments)
    parseObject.put("actionButtonClicks", engagement.actionButtonClicks)
    parseObject.put("readMoreClicks", engagement.readMoreClicks)
    parseObject.put("conversionRate", engagement.conversionRate)
    parseObject.put("engagementRate", engagement.engagementRate)
    parseObject.put("reachCount", engagement.reachCount)

    return parseObject
}

fun ParseObject.toCampaign(): Campaign {
    return Campaign(
        id = getString("campaignId") ?: objectId,
        title = getString("title") ?: "",
        titleInTelugu = getString("titleInTelugu") ?: "",
        description = getString("description") ?: "",
        descriptionInTelugu = getString("descriptionInTelugu") ?: "",
        category =
            try {
                CampaignCategory.valueOf(getString("category") ?: "COMMUNITY_BUILDING")
            } catch (e: Exception) {
                CampaignCategory.COMMUNITY_BUILDING
            },
        priority =
            try {
                CampaignPriority.valueOf(getString("priority") ?: "MEDIUM")
            } catch (e: Exception) {
                CampaignPriority.MEDIUM
            },
        status =
            try {
                CampaignStatus.valueOf(getString("status") ?: "ACTIVE")
            } catch (e: Exception) {
                CampaignStatus.ACTIVE
            },
        targetAudience =
            try {
                val list = getList<String>("targetAudience")
                list?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
        regions =
            try {
                val list = getList<String>("regions")
                list?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
        startDate = getDate("startDate") ?: Date(),
        endDate = getDate("endDate") ?: Date(),
        imageUrl = getString("imageUrl"),
        videoUrl = getString("videoUrl"),
        actionButtonText = getString("actionButtonText"),
        actionButtonTextTelugu = getString("actionButtonTextTelugu"),
        actionUrl = getString("actionUrl"),
        readMoreUrl = getString("readMoreUrl"),
        tags =
            try {
                val list = getList<String>("tags")
                list?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
        engagement =
            CampaignEngagement(
                views = getInt("views"),
                clicks = getInt("clicks"),
                shares = getInt("shares"),
                likes = getInt("likes"),
                comments = getInt("comments"),
                actionButtonClicks = getInt("actionButtonClicks"),
                readMoreClicks = getInt("readMoreClicks"),
                conversionRate = getDouble("conversionRate"),
                engagementRate = getDouble("engagementRate"),
                reachCount = getInt("reachCount"),
            ),
        isSponsored = getBoolean("isSponsored"),
        sponsorName = getString("sponsorName"),
        createdDate = getDate("createdDate") ?: createdAt,
        lastUpdated = getDate("lastUpdated") ?: updatedAt,
        createdBy = getString("createdBy") ?: "",
        isActive = getBoolean("isActive"),
        maxImpressions = if (has("maxImpressions")) getInt("maxImpressions") else null,
        currentImpressions = getInt("currentImpressions"),
    )
}
