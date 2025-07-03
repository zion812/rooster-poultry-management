package com.example.rooster.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val role: UserRole = UserRole.FARMER,
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val farmProfile: FarmProfile? = null,
    val buyerProfile: BuyerProfile? = null,
    val preferences: UserPreferences = UserPreferences(),
    val tokenBalance: Int = 0,
    val isActivityVerified: Boolean = false,
    val verificationLevel: VerificationLevel = VerificationLevel.BASIC
)

enum class UserRole {
    FARMER,
    BUYER,
    ADMIN,
    EXPERT,
    VETERINARIAN
}

enum class VerificationLevel {
    BASIC,
    VERIFIED,
    PREMIUM,
    ENTERPRISE
}

@Serializable
data class FarmProfile(
    val farmName: String = "",
    val farmAddress: Address = Address(),
    val farmSize: Double = 0.0,
    val farmType: FarmType = FarmType.LAYER,
    val totalBirds: Int = 0,
    val establishedYear: Int = 0,
    val certifications: List<Certification> = emptyList(),
    val licenseNumber: String = "",
    val facilities: List<Facility> = emptyList(),
    val operatingHours: OperatingHours = OperatingHours(),
    val specializations: List<String> = emptyList()
)

@Serializable
data class BuyerProfile(
    val businessName: String = "",
    val businessType: BusinessType = BusinessType.RETAILER,
    val businessAddress: Address = Address(),
    val gstNumber: String = "",
    val preferredProducts: List<String> = emptyList(),
    val creditLimit: Double = 0.0,
    val paymentTerms: PaymentTerms = PaymentTerms()
)

@Serializable
data class Address(
    val street: String = "",
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val pincode: String = "",
    val country: String = "India",
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class UserPreferences(
    val language: String = "te", // Telugu default for Krishna District
    val currency: String = "INR",
    val notifications: NotificationPreferences = NotificationPreferences(),
    val darkMode: Boolean = false,
    val offlineMode: Boolean = true,
    val dataSync: DataSyncPreferences = DataSyncPreferences()
)

@Serializable
data class NotificationPreferences(
    val pushNotifications: Boolean = true,
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = false,
    val auctionUpdates: Boolean = true,
    val priceAlerts: Boolean = true,
    val marketingEmails: Boolean = false,
    val healthAlerts: Boolean = true,
    val weatherAlerts: Boolean = true
)

@Serializable
data class DataSyncPreferences(
    val autoSync: Boolean = true,
    val syncOnWifiOnly: Boolean = true,
    val syncFrequency: SyncFrequency = SyncFrequency.HOURLY,
    val compressImages: Boolean = true,
    val backgroundSync: Boolean = true
)

@Serializable
data class Facility(
    val type: FacilityType,
    val capacity: Int,
    val currentOccupancy: Int,
    val condition: String = "",
    val lastMaintenance: Long = 0L
)

@Serializable
data class OperatingHours(
    val openTime: String = "06:00",
    val closeTime: String = "18:00",
    val workingDays: List<String> = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday"
    ),
    val timezone: String = "Asia/Kolkata"
)

@Serializable
data class PaymentTerms(
    val preferredMethod: PaymentMethod = PaymentMethod.UPI,
    val creditDays: Int = 0,
    val advancePercentage: Double = 0.0,
    val penaltyRate: Double = 0.0
)

enum class FarmType {
    LAYER,
    BROILER,
    MIXED,
    BREEDING,
    ORGANIC,
    FREE_RANGE,
    INTEGRATED
}

enum class BusinessType {
    RETAILER,
    WHOLESALER,
    RESTAURANT,
    DISTRIBUTOR,
    PROCESSOR,
    EXPORT
}

enum class FacilityType {
    BROODER,
    LAYER_HOUSE,
    BROILER_HOUSE,
    FEED_STORAGE,
    WATER_SYSTEM,
    WASTE_MANAGEMENT,
    QUARANTINE
}

enum class SyncFrequency {
    MANUAL,
    HOURLY,
    DAILY,
    WEEKLY
}

enum class PaymentMethod {
    CASH,
    UPI,
    CARD,
    NET_BANKING,
    WALLET,
    CHEQUE,
    RTGS_NEFT
}

@Serializable
data class Certification(
    val type: CertificationType,
    val number: String,
    val issuedBy: String,
    val issuedDate: Long,
    val expiryDate: Long,
    val isActive: Boolean = true,
    val documentUrl: String = ""
)

enum class CertificationType {
    ORGANIC,
    FREE_RANGE,
    HALAL,
    HACCP,
    ISO,
    GOVERNMENT_REGISTERED,
    ANIMAL_WELFARE,
    BIOSECURITY
}