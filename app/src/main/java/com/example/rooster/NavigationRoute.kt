package com.example.rooster

sealed class NavigationRoute(val route: String) {
    object Auth : NavigationRoute("auth")
    object Home : NavigationRoute("home")
    object FarmerHome : NavigationRoute("farmer_home")
    object HighLevelHome : NavigationRoute("high_level_home")
    object Marketplace : NavigationRoute("marketplace")
    object Community : NavigationRoute("community")
    object Profile : NavigationRoute("profile")
    object Transfers : NavigationRoute("transfers")
    object Settings : NavigationRoute("settings")
    object Help : NavigationRoute("help")
    object VetConsultation : NavigationRoute("vet_consultation")
    object IoTDashboard : NavigationRoute("iot_dashboard")
    data class Chat(val chatId: String) : NavigationRoute("chat/$chatId") {
        companion object {
            const val base = "chat/{chatId}"
        }
    }
    data class TransferDetail(val transferId: String) : NavigationRoute("transfer_detail/$transferId") {
        companion object {
            const val base = "transfer_detail/{transferId}"
        }
    }
    object ActivityVerification : NavigationRoute("activity_verification")
    object Auctions : NavigationRoute("auctions")
    data class AuctionDetail(val auctionId: String) : NavigationRoute("auction/$auctionId") {
        companion object {
            const val base = "auction/{auctionId}"
        }
    }
    object MarketplaceListingCreate : NavigationRoute("marketplace_listing_create")
    data class MarketplaceListingEdit(val listingId: String) : NavigationRoute("marketplace_listing_edit/$listingId") {
        companion object {
            const val base = "marketplace_listing_edit/{listingId}"
        }
    }
    data class MarketplaceListingDetail(val listingId: String) : NavigationRoute("marketplace_listing_detail/$listingId") {
        companion object {
            const val base = "marketplace_listing_detail/{listingId}"
        }
    }
    object SimpleAddBirds : NavigationRoute("simple_add_birds")
    object SimpleViewBirds : NavigationRoute("simple_view_birds")
    object SimpleSellBirds : NavigationRoute("simple_sell_birds")
    object SimpleHelp : NavigationRoute("simple_help")
    object Feedback : NavigationRoute("feedbackScreen")
    object FarmAnalytics : NavigationRoute("farm_analytics")
    object FarmDashboard : NavigationRoute("farm_dashboard")
    object SimpleFarmer : NavigationRoute("simple_farmer")
    object FarmNewBatch : NavigationRoute("farm_new_batch")
    object FarmNewBird : NavigationRoute("farm_new_bird")
    object FarmNewEggs : NavigationRoute("farm_new_eggs")
    object FarmNewBreeding : NavigationRoute("farm_new_breeding")
    object FarmNewChicks : NavigationRoute("farm_new_chicks")
    object FarmNewFowl : NavigationRoute("farm_new_fowl")
    object FarmNewIncubation : NavigationRoute("farm_new_incubation")
    object FarmReportMortality : NavigationRoute("farm_report_mortality")
    object FarmMortalityRecords : NavigationRoute("farm_mortality_records")
    object FarmUpdateChicks : NavigationRoute("farm_update_chicks")
    object FarmUpdateAdults : NavigationRoute("farm_update_adults")
    object FarmUpdateBreeding : NavigationRoute("farm_update_breeding")
    object FarmUpdateIncubation : NavigationRoute("farm_update_incubation")
    object FarmUpdateBreeders : NavigationRoute("farm_update_breeders")
    object FarmUpdateEggs : NavigationRoute("farm_update_eggs")
    // Add more routes as needed

    companion object {
        fun fromRoute(route: String): NavigationRoute? {
            return when {
                route == Auth.route -> Auth
                route == Home.route -> Home
                route == FarmerHome.route -> FarmerHome
                route == HighLevelHome.route -> HighLevelHome
                route == Marketplace.route -> Marketplace
                route == Community.route -> Community
                route == Profile.route -> Profile
                route == Transfers.route -> Transfers
                route == Settings.route -> Settings
                route == Help.route -> Help
                route == VetConsultation.route -> VetConsultation
                route == IoTDashboard.route -> IoTDashboard
                route.startsWith("chat/") -> Chat(route.removePrefix("chat/"))
                route.startsWith("transfer_detail/") -> TransferDetail(route.removePrefix("transfer_detail/"))
                route == ActivityVerification.route -> ActivityVerification
                route == Auctions.route -> Auctions
                route.startsWith("auction/") -> AuctionDetail(route.removePrefix("auction/"))
                route == MarketplaceListingCreate.route -> MarketplaceListingCreate
                route.startsWith("marketplace_listing_edit/") -> MarketplaceListingEdit(route.removePrefix("marketplace_listing_edit/"))
                route.startsWith("marketplace_listing_detail/") -> MarketplaceListingDetail(route.removePrefix("marketplace_listing_detail/"))
                route == SimpleAddBirds.route -> SimpleAddBirds
                route == SimpleViewBirds.route -> SimpleViewBirds
                route == SimpleSellBirds.route -> SimpleSellBirds
                route == SimpleHelp.route -> SimpleHelp
                route == Feedback.route -> Feedback
                route == FarmAnalytics.route -> FarmAnalytics
                route == FarmDashboard.route -> FarmDashboard
                route == SimpleFarmer.route -> SimpleFarmer
                route == FarmNewBatch.route -> FarmNewBatch
                route == FarmNewBird.route -> FarmNewBird
                route == FarmNewEggs.route -> FarmNewEggs
                route == FarmNewBreeding.route -> FarmNewBreeding
                route == FarmNewChicks.route -> FarmNewChicks
                route == FarmNewFowl.route -> FarmNewFowl
                route == FarmNewIncubation.route -> FarmNewIncubation
                route == FarmReportMortality.route -> FarmReportMortality
                route == FarmMortalityRecords.route -> FarmMortalityRecords
                route == FarmUpdateChicks.route -> FarmUpdateChicks
                route == FarmUpdateAdults.route -> FarmUpdateAdults
                route == FarmUpdateBreeding.route -> FarmUpdateBreeding
                route == FarmUpdateIncubation.route -> FarmUpdateIncubation
                route == FarmUpdateBreeders.route -> FarmUpdateBreeders
                route == FarmUpdateEggs.route -> FarmUpdateEggs
                else -> null
            }
        }
    }
}