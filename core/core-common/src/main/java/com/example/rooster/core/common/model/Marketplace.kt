package com.example.rooster.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val name: String = "",
    val description: String = "",
    val category: ProductCategory = ProductCategory.BIRDS,
    val subcategory: String = "",
    val breed: String = "",
    val quantity: Int = 0,
    val unit: String = "",
    val pricePerUnit: Double = 0.0,
    val totalPrice: Double = 0.0,
    val images: List<String> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val location: Address = Address(),
    val availableFrom: Long = System.currentTimeMillis(),
    val availableUntil: Long? = null,
    val certifications: List<Certification> = emptyList(),
    val qualityGrade: QualityGrade = QualityGrade.STANDARD,
    val minOrderQuantity: Int = 1,
    val bulkDiscounts: List<BulkDiscount> = emptyList(),
    val status: ProductStatus = ProductStatus.AVAILABLE,
    val views: Int = 0,
    val favorites: Int = 0,
    val ratings: List<Rating> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isTraceable: Boolean = false,
    val traceabilityCode: String = "",
    val sellerRating: Double = 0.0,
    val deliveryOptions: List<DeliveryOption> = emptyList()
)

@Serializable
data class Order(
    val id: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val discount: Double = 0.0,
    val taxes: Double = 0.0,
    val finalAmount: Double = 0.0,
    val deliveryAddress: Address = Address(),
    val deliveryType: DeliveryType = DeliveryType.PICKUP,
    val deliveryDate: Long? = null,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val transactionId: String = "",
    val notes: String = "",
    val trackingInfo: TrackingInfo? = null,
    val orderDate: Long = System.currentTimeMillis(),
    val completedDate: Long? = null,
    val cancellationReason: String = "",
    val refundAmount: Double = 0.0
)

@Serializable
data class Auction(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val title: String = "",
    val description: String = "",
    val productDetails: Product = Product(),
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val startingBid: Double = 0.0,
    val reservePrice: Double? = null,
    val currentBid: Double = 0.0,
    val bidIncrement: Double = 0.0,
    val totalBids: Int = 0,
    val highestBidderId: String = "",
    val highestBidderName: String = "",
    val bidHistory: List<Bid> = emptyList(),
    val watchers: List<String> = emptyList(),
    val auctionType: AuctionType = AuctionType.ENGLISH,
    val status: AuctionStatus = AuctionStatus.SCHEDULED,
    val images: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    val documents: List<String> = emptyList(),
    val location: Address = Address(),
    val viewingSchedule: List<ViewingSlot> = emptyList(),
    val terms: String = "",
    val paymentTerms: String = "",
    val deliveryTerms: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val bidderDepositRequired: Boolean = false,
    val depositPercentage: Double = 0.0,
    val autoExtendEnabled: Boolean = false,
    val extensionMinutes: Int = 5
)

@Serializable
data class Bid(
    val id: String = "",
    val auctionId: String = "",
    val bidderId: String = "",
    val bidderName: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoBid: Boolean = false,
    val maxBidAmount: Double? = null,
    val status: BidStatus = BidStatus.ACTIVE,
    val bidderRating: Double = 0.0,
    val depositPaid: Boolean = false,
    val isWinning: Boolean = false
)

@Serializable
data class BulkDiscount(
    val minQuantity: Int = 0,
    val discountPercentage: Double = 0.0,
    val discountAmount: Double = 0.0
)

@Serializable
data class Rating(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val review: String = "",
    val images: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val helpful: Int = 0,
    val productId: String = "",
    val orderId: String = "",
    val aspects: Map<String, Int> = emptyMap() // quality, delivery, service, etc.
)

@Serializable
data class TrackingInfo(
    val trackingNumber: String = "",
    val carrier: String = "",
    val estimatedDelivery: Long? = null,
    val currentStatus: String = "",
    val statusHistory: List<StatusUpdate> = emptyList(),
    val currentLocation: String = "",
    val deliveryInstructions: String = ""
)

@Serializable
data class StatusUpdate(
    val status: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val description: String = "",
    val updatedBy: String = ""
)

@Serializable
data class ViewingSlot(
    val date: Long = 0L,
    val startTime: String = "",
    val endTime: String = "",
    val location: Address = Address(),
    val maxAttendees: Int = 0,
    val currentAttendees: Int = 0,
    val registeredUsers: List<String> = emptyList(),
    val requirements: List<String> = emptyList()
)

@Serializable
data class AuctionResult(
    val auctionId: String = "",
    val winnerId: String? = null,
    val winnerName: String = "",
    val winningBid: Double = 0.0,
    val totalBids: Int = 0,
    val endTime: Long = 0L,
    val paymentDeadline: Long = 0L,
    val isPaid: Boolean = false,
    val transactionId: String = "",
    val deliveryStatus: DeliveryStatus = DeliveryStatus.PENDING,
    val completionDate: Long? = null
)

@Serializable
data class DeliveryOption(
    val type: DeliveryType,
    val cost: Double = 0.0,
    val estimatedDays: Int = 0,
    val description: String = "",
    val available: Boolean = true
)

@Serializable
data class PaymentTransaction(
    val id: String = "",
    val orderId: String = "",
    val auctionId: String = "",
    val payerId: String = "",
    val payeeId: String = "",
    val amount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.UPI,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val gatewayTransactionId: String = "",
    val gatewayResponse: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val failureReason: String = ""
)

enum class ProductCategory {
    BIRDS, EGGS, FEED, EQUIPMENT, MEDICINE, ACCESSORIES, SERVICES, ORGANIC_PRODUCTS
}

enum class QualityGrade {
    PREMIUM, STANDARD, ECONOMY, ORGANIC, CERTIFIED, HERITAGE
}

enum class ProductStatus {
    AVAILABLE, OUT_OF_STOCK, DISCONTINUED, PENDING_APPROVAL, UNDER_REVIEW, SUSPENDED
}

enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, PACKED, SHIPPED, OUT_FOR_DELIVERY,
    DELIVERED, CANCELLED, RETURNED, REFUNDED
}

enum class PaymentStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED, CANCELLED
}

enum class DeliveryType {
    PICKUP, HOME_DELIVERY, COURIER, EXPRESS, SAME_DAY
}

enum class AuctionType {
    ENGLISH, DUTCH, SEALED_BID, RESERVE, BUY_NOW
}

enum class AuctionStatus {
    SCHEDULED, LIVE, PAUSED, ENDED, CANCELLED, COMPLETED, PAYMENT_PENDING
}

enum class BidStatus {
    ACTIVE, OUTBID, WINNING, CANCELLED, EXPIRED
}

enum class DeliveryStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED
}

// Community and Social Features
@Serializable
data class CommunityPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val content: String = "",
    val images: List<String> = emptyList(),
    val category: PostCategory = PostCategory.GENERAL,
    val tags: List<String> = emptyList(),
    val likes: Int = 0,
    val comments: List<Comment> = emptyList(),
    val shares: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isExpert: Boolean = false,
    val location: String = ""
)

@Serializable
data class Comment(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val replies: List<Comment> = emptyList()
)

enum class PostCategory {
    GENERAL, HEALTH, NUTRITION, BREEDING, MARKET_NEWS, TECHNOLOGY, TIPS, QUESTIONS
}