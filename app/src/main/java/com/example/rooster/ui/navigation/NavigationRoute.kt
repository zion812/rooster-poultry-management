package com.example.rooster.ui.navigation

enum class NavigationRoute(val route: String) {
    // Core navigation routes
    AUTH("auth"),
    HOME("home"),
    MARKET("market"),
    MARKETPLACE("marketplace"),
    EXPLORE("explore"),
    CREATE("create"),
    CART("cart"),
    PROFILE("profile"),
    SETTINGS("settings"),
    HELP("help"),
    COMMUNITY("community"),
    TRANSFERS("transfers"),

    // Main screen routes
    FARMER_HOME("farmer_home"),
    HIGH_LEVEL_HOME("high_level_home"),
    SIMPLE_VIEW_BIRDS("simple_view_birds"),
    SIMPLE_SELL_BIRDS("simple_sell_birds"),
    SIMPLE_ADD_BIRDS("simple_add_birds"),
    SIMPLE_HELP("simple_help"),
    SIMPLE_FARMER("simple_farmer"),

    // Marketplace routes
    MARKETPLACE_LISTING_CREATE("create_listing"),
    MARKETPLACE_LISTING_EDIT("edit_listing/{listingId}"),
    MARKETPLACE_LISTING_DETAIL("listing_detail/{listingId}"),

    // Auction routes
    AUCTIONS("auctions"),
    AUCTION_DETAIL("auction_detail/{auctionId}"),

    // Order management
    ORDER_HISTORY("order_history"),
    ORDER_DETAIL("order_detail/{orderId}"),

    // Communication
    CHAT("chat/{receiverFirebaseUid}"),
    VET_CONSULTATION("vet_consultation"),

    // Farm management
    FLOCK_MONITORING("flock_monitoring"),
    FARM_DASHBOARD("farm_dashboard"),
    FARM_ANALYTICS("farm_analytics"),
    IOT_DASHBOARD("iot_dashboard"),

    // Activity and verification
    TRANSFER_DETAIL("transfer_detail/{transferId}"),
    ACTIVITY_VERIFICATION("activity_verification"),

    // Additional screens
    FOWL_TRACEABILITY("fowl_traceability"),
    DIAGNOSIS_HELP("diagnosis_help"),
    COMPLIANCE_SCREEN("compliance_screen"),
    HEALTH_RECORDS("health_records"),
    LIVE_BROADCAST("broadcast");

    companion object {
        fun fromRoute(route: String?): NavigationRoute? = values().find { it.route == route }

        // Add property aliases for backward compatibility
        val MarketplaceListingEdit = object {
            val base = "edit_listing"
        }

        val MarketplaceListingDetail = object {
            val base = "listing_detail"
        }
    }
}
