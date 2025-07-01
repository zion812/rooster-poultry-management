package com.example.rooster

import java.util.*

// ===============================
// VET CONSULTATION DATA MODELS
// ===============================

data class VetProfile(
    val vetId: String,
    val name: String,
    val qualification: String,
    val experience: Int,
    val specialization: List<String>,
    val rating: Double,
    val consultationFee: Double,
    val isAvailable: Boolean,
    val languages: List<String>,
    val region: String,
    val profilePhotoUrl: String?,
)

data class VetConsultation(
    val consultationId: String,
    val farmerId: String,
    val vetId: String,
    val vetName: String,
    // Consultation type (AI_CHAT, VOICE_CALL, VIDEO_CALL, TEXT_CHAT)
    val type: ConsultationType,
    val status: ConsultationStatus,
    val symptoms: String,
    val aiResponse: String?,
    val vetNotes: String?,
    val scheduledTime: Date?,
    val completedTime: Date?,
    val cost: Double,
    // Rating given by the farmer (1.0 to 5.0)
    val rating: Double,
    val createdAt: Date,
)

enum class ConsultationType {
    AI_CHAT,
    VOICE_CALL,
    VIDEO_CALL,
    TEXT_CHAT,
}

enum class ConsultationStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    SCHEDULED,
}

data class AIHealthTip(
    val tipId: String,
    val title: String,
    val description: String,
    val severity: HealthSeverity,
    val recommendedAction: String,
    val confidence: Double,
    val language: String,
    val references: List<String>,
)

enum class HealthSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
}

// ===============================
// PAYMENT INTEGRATION DATA MODELS
// ===============================

data class PaymentTransaction(
    val transactionId: String,
    val razorpayPaymentId: String?,
    val orderId: String,
    val amount: Double,
    val currency: String,
    val status: TransactionStatus,
    val method: PaymentMethod,
    val description: String,
    val listingId: String?,
    val sellerId: String?,
    val buyerId: String?,
    val createdAt: Date,
    val completedAt: Date?,
    val failureReason: String?,
)

enum class TransactionStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED,
}

enum class PaymentMethod {
    RAZORPAY,
    UPI,
    CARD,
    NET_BANKING,
    COD,
    WALLET,
}

data class RevenueAnalytics(
    val totalRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val totalTransactions: Int = 0,
    val successfulTransactions: Int = 0,
    val averageOrderValue: Double = 0.0,
    val topSellingCategory: String = "",
    val recentTransactions: List<PaymentTransaction> = emptyList(),
)

// ===============================
// EDUCATIONAL CONTENT DATA MODELS
// ===============================

data class EducationalResource(
    val resourceId: String,
    val title: String,
    val description: String,
    val type: ResourceType,
    val contentUrl: String,
    val thumbnailUrl: String?,
    val authorId: String,
    val authorName: String,
    val authorType: AuthorType,
    val category: String,
    val estimatedDataUsage: Long,
    val duration: Int,
    val language: String,
    val tags: List<String>,
    val viewCount: Int,
    val likeCount: Int,
    val downloadCount: Int,
    val createdAt: Date,
    val updatedAt: Date,
)

enum class ResourceType {
    VIDEO,
    ARTICLE,
    AUDIO,
    INFOGRAPHIC,
    TUTORIAL,
}

enum class AuthorType {
    VET,
    EXPERT_FARMER,
    RESEARCHER,
    GOVERNMENT_OFFICIAL,
}

data class ContentEngagement(
    val resourceId: String,
    val totalViews: Int = 0,
    val uniqueViewers: Int = 0,
    val averageWatchTime: Double = 0.0,
    val completionRate: Double = 0.0,
    val likeCount: Int = 0,
    val shareCount: Int = 0,
    val downloadCount: Int = 0,
    val commentCount: Int = 0,
    val rating: Double = 0.0,
)

// ===============================
// IOT INTEGRATION DATA MODELS
// ===============================

data class SensorReading(
    val readingId: String,
    val farmId: String,
    val sensorType: SensorType,
    val value: Double,
    val unit: String,
    val location: String,
    val deviceId: String,
    val alertLevel: AlertLevel,
    val timestamp: Date,
    val batteryLevel: Double,
    val signalStrength: Double,
)

enum class SensorType {
    TEMPERATURE,
    HUMIDITY,
    LIGHT,
    MOTION,
    FEED_LEVEL,
    WATER_LEVEL,
    AIR_QUALITY,
    SOUND_LEVEL,
}

enum class AlertLevel {
    NORMAL,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
}

data class IoTDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceType: IoTDeviceType,
    val farmId: String,
    val location: String,
    val isOnline: Boolean,
    val batteryLevel: Double,
    val signalStrength: Double,
    val lastSeen: Date,
    val firmwareVersion: String,
    val configuration: String,
)

enum class IoTDeviceType {
    SENSOR,
    ACTUATOR,
    CAMERA,
    FEEDER,
    GATEWAY,
}

data class IoTAlert(
    val alertId: String,
    val farmId: String,
    val deviceId: String,
    val alertType: IoTAlertType,
    val severity: AlertLevel,
    val title: String,
    val description: String,
    val value: Double,
    val threshold: Double,
    val isActive: Boolean,
    val isAcknowledged: Boolean,
    val createdAt: Date,
    val acknowledgedAt: Date?,
)

enum class IoTAlertType {
    THRESHOLD_EXCEEDED,
    DEVICE_OFFLINE,
    LOW_BATTERY,
    SENSOR_MALFUNCTION,
    CRITICAL_CONDITION,
}

// ===============================
// ANALYTICS & REPORTING DATA MODELS
// ===============================

data class FarmAnalytics(
    val totalFowl: Int = 0,
    val activeFowl: Int = 0,
    val avgHealthScore: Double = 0.0,
    val mortalityRate: Double = 0.0,
    val feedEfficiency: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val yearlyRevenue: Double = 0.0,
    val totalSales: Int = 0,
    val avgSalePrice: Double = 0.0,
    val breedPerformance: List<BreedPerformance> = emptyList(),
    val monthlyTrends: List<MonthlyTrend> = emptyList(),
    val lastUpdated: Date = Date(),
)

data class BreedPerformance(
    val breedName: String,
    val count: Int,
    val avgPrice: Double,
    val healthScore: Double,
    val growthRate: Double,
)

data class MonthlyTrend(
    val month: String,
    val fowlCount: Int,
    val revenue: Double,
    val expenses: Double,
    val profit: Double,
)

// ===============================
// DATA USAGE TRACKING DATA MODELS
// ===============================

data class DataUsageAnalytics(
    val sessionUsage: Long,
    val dailyUsage: Long,
    val weeklyUsage: Long,
    val monthlyUsage: Long,
    val totalAppUsage: Long,
    val imageDataUsage: Long,
    val videoDataUsage: Long,
    val chatDataUsage: Long,
    val marketplaceDataUsage: Long,
    val lastResetDate: Date,
    val warningThreshold: Long,
    val monthlyLimit: Long,
)

data class DataUsageBreakdown(
    val feature: String,
    val bytesUsed: Long,
    val percentage: Double,
    val lastUpdated: Date,
)

data class NetworkUsageSession(
    val sessionId: String,
    val startTime: Date,
    val endTime: Date?,
    val totalBytes: Long,
    val networkType: String,
    val quality: NetworkQualityLevel,
)

// ===============================
// ENHANCED MARKETPLACE DATA MODELS
// ===============================

// Community post model representing a user post in Community
data class CommunityPost(
    val objectId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val imageUrl: String?,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val createdAt: Date,
    val tags: List<String>,
    val location: String?,
    val isVerified: Boolean,
)

// Represents a user community group
data class CommunityGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
    // "public" or "private"
    val type: String,
)

// Cart item model representing items in the user's cart
data class CartItem(
    val objectId: String,
    val listingId: String,
    val title: String,
    val price: Double,
    val quantity: Int,
    val sellerId: String,
    val sellerName: String,
    val imageUrl: String?,
    val addedAt: Date,
    val isAvailable: Boolean,
)

data class PaymentOption(
    val method: PaymentMethod,
    val displayName: String,
    val isEnabled: Boolean,
    val processingFee: Double,
    val estimatedTime: String,
    val icon: String,
)

data class OrderDetails(
    val orderId: String,
    val listingId: String,
    val buyerId: String,
    val sellerId: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val paymentMethod: PaymentMethod,
    val deliveryAddress: String?,
    val specialInstructions: String?,
    val orderStatus: OrderStatus,
    val createdAt: Date,
    val estimatedDelivery: Date?,
)

enum class OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
}

// ===============================
// VET CONSULTATION ENHANCED MODELS
// ===============================

data class ConsultationSlot(
    val slotId: String,
    val vetId: String,
    val startTime: Date,
    val endTime: Date,
    val isAvailable: Boolean,
    val consultationType: ConsultationType,
    val price: Double,
)

data class VetAvailability(
    val vetId: String,
    val dayOfWeek: Int, // 1-7, Monday to Sunday
    val startTime: String, // HH:mm format
    val endTime: String,
    val isAvailable: Boolean,
    val consultationTypes: List<ConsultationType>,
)

data class ConsultationRating(
    val ratingId: String,
    val consultationId: String,
    val farmerId: String,
    val vetId: String,
    // Rating given by the farmer (1-5 stars)
    val rating: Int,
    val review: String?,
    val createdAt: Date,
)

// ===============================
// EDUCATIONAL CONTENT ENHANCED MODELS
// ===============================

data class EducationalCategory(
    val categoryId: String,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val resourceCount: Int,
    val isPopular: Boolean,
)

data class LearningProgress(
    val userId: String,
    val resourceId: String,
    // Progress percentage (0.0 to 1.0)
    val progress: Double,
    // Time in seconds
    val lastWatchedTime: Long,
    val isCompleted: Boolean,
    val startedAt: Date,
    val completedAt: Date?,
)

data class ResourceComment(
    val commentId: String,
    val resourceId: String,
    val userId: String,
    val userName: String,
    val comment: String,
    val parentCommentId: String?, // For replies
    val likeCount: Int,
    val createdAt: Date,
)

// ===============================
// IOT ENHANCED DATA MODELS
// ===============================

data class IoTFarmConfiguration(
    val farmId: String,
    val farmName: String,
    val location: String,
    val totalDevices: Int,
    val activeDevices: Int,
    val alertThresholds: Map<SensorType, Pair<Double, Double>>, // min, max
    val notificationSettings: IoTNotificationSettings,
    val lastUpdated: Date,
)

data class IoTNotificationSettings(
    val enableEmailAlerts: Boolean,
    val enableSMSAlerts: Boolean,
    val enablePushNotifications: Boolean,
    val quietHoursStart: String, // HH:mm
    val quietHoursEnd: String,
    val alertSeverityThreshold: AlertLevel,
)

data class SensorHistory(
    val sensorId: String,
    val readings: List<SensorReading>,
    val averageValue: Double,
    val minValue: Double,
    val maxValue: Double,
    val trendDirection: TrendDirection,
    val periodStart: Date,
    val periodEnd: Date,
)

enum class TrendDirection {
    INCREASING,
    DECREASING,
    STABLE,
    FLUCTUATING,
}

// ===============================
// SEARCH & DISCOVERY DATA MODELS
// ===============================
// ===============================
// MESSAGING DATA MODELS
// ===============================
// Model for one-on-one personal message
data class PersonalMessage(
    val messageId: String,
    val senderId: String? = null, // Made nullable to match Fetchers.kt usage
    val senderName: String? = null,
    val receiverId: String? = null,
    val receiverName: String? = null,
    val content: String,
    val messageType: MessageType?, // Made nullable to match Fetchers.kt usage
    val isRead: Boolean,
    val createdAt: Date,
    val imageUrl: String?,
    val audioUrl: String?,
)

// Model for group chat message
data class GroupMessage(
    val messageId: String,
    val groupId: String,
    val senderId: String? = null, // Made nullable to match Fetchers.kt usage
    val senderName: String? = null,
    val content: String,
    val messageType: MessageType?, // Made nullable to match Fetchers.kt usage
    val createdAt: Date,
    val imageUrl: String?,
    val audioUrl: String?,
    val isSystemMessage: Boolean,
)

// Model for community-wide messages and announcements
data class CommunityMessage(
    val messageId: String,
    val authorId: String? = null, // Made nullable to match Fetchers.kt usage
    val authorName: String? = null,
    val title: String,
    val content: String,
    val category: String,
    val priority: AnnouncementPriority?, // Made nullable to match Fetchers.kt usage
    val isSticky: Boolean,
    val createdAt: Date,
    val imageUrl: String?,
    val viewCount: Int,
    val likeCount: Int,
)

// Announcement priority levels
enum class AnnouncementPriority {
    LOW,
    NORMAL,
    HIGH,
}

// ===============================
// ADMIN DASHBOARD DATA MODELS
// ===============================
// Model for recent user activities in admin dashboard
data class ActivityItem(
    val userId: String? = null, // Made nullable to match Fetchers.kt usage
    val userName: String? = null,
    val action: String? = null,
    val timestamp: Date? = null,
    val details: String? = null,
)

// ===============================
// EVENTS & COMPETITIONS DATA MODELS
// ===============================

data class ParseEvent(
    val eventId: String,
    val title: String,
    val description: String,
    val eventType: EventType,
    val eventDate: Date,
    val location: String,
    val region: String,
    val organizerId: String,
    val organizerName: String,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val entryFee: Double,
    val prizeMoney: Double,
    val registrationDeadline: Date?,
    val isRegistrationOpen: Boolean,
    val category: String,
    val imageUrl: String?,
    val createdAt: Date,
)

enum class EventType {
    COMPETITION,
    WORKSHOP,
    EXHIBITION,
    SEMINAR,
    CULTURAL,
    AUCTION,
}

// ===============================
// AUCTION & BIDDING DATA MODELS
// ===============================

// These models have been moved to core/core-common/src/main/java/com/example/rooster/core/common/models/auction/
// and core/core-common/src/main/java/com/example/rooster/core/common/enums/
// Definitions are removed from here.

// Auction creation and management
data class AuctionSettings(
    val sellerId: String,
    val fowlId: String,
    val startingPrice: Double,
    val reservePrice: Double?,
    val customDurationHours: Int,
    val minimumBidPrice: Double,
    val requiresBidderDeposit: Boolean,
    val bidderDepositPercentage: Double,
    val allowsProxyBidding: Boolean,
    val sellerBidMonitoring: BidMonitoringCategory,
    val autoExtendOnLastMinuteBid: Boolean,
    val extensionMinutes: Int,
    val buyNowPrice: Double?,
    val startTime: Date,
    val allowedBidderTypes: List<BidderType>,
)

enum class BidderType {
    ALL_USERS,
    VERIFIED_ONLY,
    PREMIUM_ONLY,
    REGIONAL_ONLY,
    PREVIOUS_BUYERS_ONLY,
}

// Dashboard Data Models
data class VerificationMetrics(
    val queueSize: Int,
    val averageTurnaroundTime: Double, // in hours
    val failureRate: Double, // percentage
    val successRate: Double, // percentage
    val pendingDisputes: Int,
)

data class RevenueMetrics(
    val dailyRevenue: Double,
    val monthlyRevenue: Double,
    val annualRevenue: Double,
    val commissionsByRegion: Map<String, Double>,
)

data class DisputeMetrics(
    val openCases: Int,
    val averageResolutionTime: Double, // in hours
    val highRiskTransactions: Int,
    val flaggedUsers: List<String>,
)

data class VerificationAction(
    val id: String,
    val userDisplayName: String,
    val verificationType: String,
    val daysPending: Int,
    val priority: ActionPriority = ActionPriority.NORMAL,
)

enum class ActionPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT,
}

// ===============================
// FLOCK REGISTRY DATA MODELS
// ===============================

data class FlockRegistry(
    val id: String,
    val farmerId: String,
    val flockName: String,
    val flockType: FlockType,
    val ageGroup: AgeGroup,
    val registrationDate: Date,
    val verificationStatus: FlockVerificationStatus,
    val requiredFields: Map<String, Any>,
    val optionalFields: Map<String, Any>? = null,
    val documents: List<FlockDocument> = emptyList(),
    val createdAt: Date,
    val updatedAt: Date,
)

enum class FlockType {
    TRACEABLE,
    NON_TRACEABLE,
}

enum class AgeGroup {
    CHICK_CHICKS,
    ZERO_TO_FIVE_WEEKS,
    FIVE_WEEKS_TO_FIVE_MONTHS,
    FIVE_MONTHS_TO_TWELVE_MONTHS_PLUS,
}

enum class FlockVerificationStatus {
    PENDING,
    UNDER_REVIEW,
    VERIFIED,
    REJECTED,
    NEEDS_MORE_INFO,
}

data class FlockDocument(
    val id: String,
    val flockId: String,
    val documentType: DocumentType,
    val fileName: String,
    val fileUrl: String,
    val uploadedAt: Date,
    val verifiedAt: Date? = null,
    val notes: String? = null,
)

enum class DocumentType {
    FAMILY_TREE,
    BIRTH_CERTIFICATE,
    VACCINATION_RECORD,
    HEALTH_CERTIFICATE,
    PROOF_OF_ORIGIN,
    IDENTIFICATION_PHOTO,
    WEIGHT_RECORD,
    OTHER,
}

data class FlockField(
    val name: String,
    val value: String,
    val isRequired: Boolean,
    val fieldType: FieldType,
    val validationRules: List<ValidationRule> = emptyList(),
)

enum class FieldType {
    TEXT,
    NUMBER,
    DATE,
    DROPDOWN,
    FILE_UPLOAD,
    MULTI_SELECT,
    BOOLEAN,
}

data class ValidationRule(
    val type: ValidationType,
    val value: String,
    val errorMessage: String,
)

enum class ValidationType {
    MIN_LENGTH,
    MAX_LENGTH,
    REQUIRED,
    DATE_FORMAT,
    NUMBER_RANGE,
    FILE_SIZE_LIMIT,
    FILE_TYPE,
}

// ===============================
// FLOCK VERIFICATION DATA MODELS
// ===============================

data class FlockVerification(
    val id: String,
    val flockId: String,
    val verifierId: String,
    val verifierName: String,
    val status: FlockVerificationStatus,
    val checkedFields: List<FieldVerification>,
    val comments: String? = null,
    val verifiedAt: Date? = null,
    val rejectionReason: String? = null,
    val createdAt: Date,
)

data class FieldVerification(
    val fieldName: String,
    val isVerified: Boolean,
    val verifierNotes: String? = null,
    val verifiedAt: Date? = null,
)

// ===============================
// EXISTING MODELS CONTINUE...
// ===============================
