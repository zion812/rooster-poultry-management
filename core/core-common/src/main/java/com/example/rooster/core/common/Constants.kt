package com.example.rooster.core.common

/**
 * Enterprise-grade constants file
 * Centralized configuration values
 */
object Constants {

    // App Configuration
    const val APP_NAME = "Rooster"
    const val APP_VERSION = "1.0.0"
    const val DATABASE_VERSION = 1

    // Network Configuration
    const val NETWORK_TIMEOUT = 30L // In seconds
    const val READ_TIMEOUT = 30L    // In seconds
    const val WRITE_TIMEOUT = 30L   // In seconds
    const val MAX_RETRIES = 3
    const val RETRY_DELAY_MS = 1000L
    // PAYMENT_API_BASE_URL is now sourced from BuildConfig in the app module and provided via Hilt.

    // Parse Configuration
    const val PARSE_APPLICATION_ID = "parse_app_id"
    const val PARSE_CLIENT_KEY = "parse_client_key"
    const val PARSE_SERVER_URL = "https://parseapi.back4app.com/"

    // Cache Configuration
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE = 60 * 60 * 24 // 24 hours
    const val CACHE_MAX_STALE = 60 * 60 * 24 * 7 // 7 days

    // UI Configuration
    const val ANIMATION_DURATION = 300L
    const val SPLASH_DELAY = 2000L
    const val DEBOUNCE_DELAY = 500L

    // Pagination
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val TIME_FORMAT_DISPLAY = "HH:mm"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PASSWORD_LENGTH = 128
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 30

    // File Upload
    const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10MB
    const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/png,image/webp"
    const val COMPRESSION_QUALITY = 80

    // Notification Channels
    const val NOTIFICATION_CHANNEL_GENERAL = "general"
    const val NOTIFICATION_CHANNEL_MESSAGES = "messages"
    const val NOTIFICATION_CHANNEL_MARKETPLACE = "marketplace"
    const val NOTIFICATION_CHANNEL_HEALTH = "health"

    // SharedPreferences Keys
    const val PREF_USER_TOKEN = "user_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_FIRST_LAUNCH = "first_launch"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_LANGUAGE = "language"

    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_GENERIC = "Something went wrong. Please try again."
    const val ERROR_INVALID_CREDENTIALS = "Invalid email or password."
    const val ERROR_USER_NOT_FOUND = "User not found."
    const val ERROR_PERMISSION_DENIED = "Permission denied."

    // Success Messages
    const val SUCCESS_LOGIN = "Login successful"
    const val SUCCESS_REGISTRATION = "Registration successful"
    const val SUCCESS_PASSWORD_RESET = "Password reset email sent"
    const val SUCCESS_PROFILE_UPDATE = "Profile updated successfully"

    // Rural Optimization
    const val LOW_END_DEVICE_RAM_THRESHOLD = 2048 // 2GB in MB
    const val RURAL_NETWORK_TIMEOUT = 60L
    const val OFFLINE_CACHE_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 days

    // Feature Flags
    const val FEATURE_SOCIAL_ENABLED = true
    const val FEATURE_MARKETPLACE_ENABLED = true
    const val FEATURE_ANALYTICS_ENABLED = true
    const val FEATURE_PUSH_NOTIFICATIONS = true

    // Telugu Language Support
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_TELUGU = "te"
    const val DEFAULT_LANGUAGE = LANGUAGE_ENGLISH

    // Marketplace
    const val MIN_BID_AMOUNT = 100.0
    const val MAX_BID_AMOUNT = 1000000.0
    const val AUCTION_DURATION_HOURS = 24
    const val PAYMENT_TIMEOUT_MINUTES = 10

    // Social Features
    const val MAX_GROUP_MEMBERS = 100
    const val MAX_MESSAGE_LENGTH = 1000
    const val MAX_MEDIA_ATTACHMENTS = 10

    // Farm Management
    const val MAX_FOWL_COUNT = 10000
    const val HEALTH_CHECK_INTERVAL_DAYS = 7
    const val VACCINATION_REMINDER_DAYS = 30
}

/**
 * Navigation Routes
 */
object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MARKETPLACE = "marketplace"
    const val SOCIAL = "social"
    const val FARM = "farm"
    const val HEALTH = "health"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
}

/**
 * Intent Actions
 */
object IntentActions {
    const val NOTIFICATION_CLICKED = "notification_clicked"
    const val DEEP_LINK_MARKETPLACE = "deep_link_marketplace"
    const val DEEP_LINK_SOCIAL = "deep_link_social"
    const val SHARE_CONTENT = "share_content"
}

/**
 * Bundle Keys
 */
object BundleKeys {
    const val USER_ID = "user_id"
    const val FOWL_ID = "fowl_id"
    const val GROUP_ID = "group_id"
    const val NOTIFICATION_DATA = "notification_data"
    const val DEEP_LINK_DATA = "deep_link_data"
}