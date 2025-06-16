package com.example.rooster

enum class NavigationRoute(val route: String) {
    AUTH("auth"),
    HOME("home"),
    FARMER_HOME("farmer_home"),
    HIGH_LEVEL_HOME("high_level_home"),
    MARKETPLACE("marketplace"),
    COMMUNITY("community"),
    PROFILE("profile"),
    TRANSFERS("transfers"),
    SETTINGS("settings"),
    HELP("help"),
    VET_CONSULTATION("vet_consultation"),
    IOT_DASHBOARD("iot_dashboard"),
    CHAT("chat/{chatId}") {
        fun withChatId(chatId: String) = "chat/$chatId"
    },
    TRANSFER_DETAIL("transfer_detail/{transferId}") {
        fun withTransferId(transferId: String) = "transfer_detail/$transferId"
    },
    ACTIVITY_VERIFICATION("activity_verification"),
    AUCTIONS("auctions"),
    AUCTION_DETAIL("auction") {
        fun withId(auctionId: String) = "auction/$auctionId"
    },
}

enum class UserRole {
    FARMER,
    GENERAL,
    HIGH_LEVEL,
    UNKNOWN,
}
