package com.example.rooster.ui.navigation

enum class NavigationRoute(val route: String) {
    AUTH("auth"),
    HOME("home"),
    MARKET("market"), // Placeholder, might need to align with InstagramNavRoute.MARKETPLACE
    EXPLORE("explore"),
    CREATE("create"),
    CART("cart"),
    COMMUNITY("community"),
    DASHBOARD("dashboard"),
    TRANSFERS("transfers"),
    PROFILE("profile"),
    VET_CONSULTATION("vet_consultation"),
    IOT_INTEGRATION("iot_integration"),
    SETTINGS("settings"), // Added based on ProfileHeader menu
    HELP("help_support"), // Added based on ProfileHeader menu
    AUCTIONS("auctions"), // Added based on MainActivity NavHost
    AUCTION_DETAIL("auction_detail"), // Added based on MainActivity NavHost (implicit)
    FARMER_HOME("farmer_home"), // Added based on MainActivity NavHost
    HIGH_LEVEL_HOME("high_level_home"), // Added based on MainActivity NavHost
    SIMPLE_VIEW_BIRDS("simple_view_birds"), // Added based on MainActivity NavHost
    SIMPLE_SELL_BIRDS("simple_sell_birds"), // Added based on MainActivity NavHost
    MARKETPLACE_LISTING_CREATE("create_listing"), // Added based on MainActivity NavHost
    MARKETPLACE_LISTING_EDIT("edit_listing"), // Base for "edit_listing/{listingId}"
    MARKETPLACE_LISTING_DETAIL("listing_detail"), // Base for "listing_detail/{listingId}"
    FLOCK_MONITORING("flock_monitoring"), // Added based on MainActivity NavHost
    PAYMENT("payment"), // Base for "payment/{listingId}/{amount}" and "payment"
    ORDER_HISTORY("orders"), // Added based on MainActivity NavHost
    ORDER_DETAIL("orderDetail"), // Base for "orderDetail/{orderId}"
    COD_CONFIRMATION("codConfirm"), // Base for "codConfirm/{orderId}"
    FEEDBACK("feedback"), // Base for "feedback/{orderId}"
    CERTIFICATION_REQUEST("certificationRequest"), // Added based on MainActivity NavHost
    VACCINATION_TEMPLATES("vaccinationTemplates"), // Added based on MainActivity NavHost
    EVENTS("events"), // Added based on MainActivity NavHost & InstagramNavRoute
    FLOCK_DASHBOARD("flockDashboard"), // Added based on MainActivity NavHost
    LIVE_BROADCAST("broadcast"), // Added based on MainActivity NavHost
    TRANSFER_DETAIL_LEGACY("transferDetail"), // Base for "transferDetail/{chickenId}" - different from orderId based one

    // From backup, potentially conflicting or to be merged:
    // TRANSFER_VERIFICATION("transferVerification/{orderId}"), // Already covered by ORDER_DETAIL or similar
    // CHAT("chat/{receiverFirebaseUid}"), // Covered by COMMUNITY or specific chat feature
    // PAYMENT_LEGACY("payment/{id}/{title}/{price}/{location}"); // Covered by PAYMENT

    // Routes for simple farmer UI from SimpleFarmerScreen.kt implicit navigation
    SIMPLE_ADD_BIRDS("simple_add_birds"),
    SIMPLE_HELP("simple_help");


    companion object {
        fun fromRoute(route: String?): NavigationRoute? = values().find { it.route == route }
    }
}
