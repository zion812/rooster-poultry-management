package com.example.rooster.config

object Constants {
    // Back4App Configuration
    const val BACK4APP_APP_ID = "HrIgPFpGQ2raCaCCEPr6C9B8O7pLhhcdxgtRvLYZ"
    const val BACK4APP_CLIENT_KEY = "Ce0lpTFzTMtQ196NW91pfD2NJNkA14PjsFyqdTC5"
    const val BACK4APP_SERVER_URL = "https://parseapi.back4app.com/"
    const val BACK4APP_ACCOUNT_KEY = "1FrOEsls2M8SB2hEIpOwoG5BAEFkWoaLrImaxBFO"
    const val BACK4APP_RUST_KEY = "d6KYqv0Jh0cxdRIJsyJ4m97BxMick4oPhuIXtXyk"

    // Network Configuration
    const val NETWORK_TIMEOUT = 30000L
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB

    // Rural Optimization
    const val LOW_BANDWIDTH_THRESHOLD = 1000 // 1KB/s
    const val OFFLINE_CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 hours

    // Firebase Configuration
    const val FIREBASE_REGION = "us-east1"
    const val ANALYTICS_ENABLED = true

    // Razorpay Configuration
    const val RAZORPAY_KEY_TEST = "rzp_test_1234567890ABCD"
    const val RAZORPAY_KEY_LIVE = "YOUR_LIVE_KEY"
}
