package com.example.rooster.config

object Constants {
    // Back4App Configuration
    const val BACK4APP_APP_ID = "your_back4app_app_id"
    const val BACK4APP_CLIENT_KEY = "your_back4app_client_key"
    const val BACK4APP_SERVER_URL = "https://parseapi.back4app.com/"

    // Network Configuration
    const val NETWORK_TIMEOUT = 30000L
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB

    // Rural Optimization
    const val LOW_BANDWIDTH_THRESHOLD = 1000 // 1KB/s
    const val OFFLINE_CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 hours

    // Firebase Configuration
    const val FIREBASE_REGION = "us-east1"
    const val ANALYTICS_ENABLED = true
}
