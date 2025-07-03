package com.example.rooster

// Type aliases and imports to make core models available at app level
import com.example.rooster.core.common.models.auction.AuctionListing as CoreAuctionListing
import com.example.rooster.core.common.enums.AuctionStatus as CoreAuctionStatus
import com.example.rooster.core.common.enums.BidMonitoringCategory as CoreBidMonitoringCategory
import com.example.rooster.core.common.models.auction.AuctionWinner as CoreAuctionWinner
import com.example.rooster.core.common.models.auction.EnhancedAuctionBid as CoreEnhancedAuctionBid
import com.example.rooster.core.common.models.auction.BackupBidder as CoreBackupBidder
import com.example.rooster.core.common.enums.OfferResponse as CoreOfferResponse
import com.example.rooster.core.common.enums.AuctionPaymentStatus as CoreAuctionPaymentStatus
import com.example.rooster.core.common.enums.BidStatus as CoreBidStatus
import com.example.rooster.core.common.enums.DepositStatus as CoreDepositStatus

// Type aliases for backward compatibility
typealias AuctionListing = CoreAuctionListing
typealias AuctionStatus = CoreAuctionStatus
typealias BidMonitoringCategory = CoreBidMonitoringCategory
typealias AuctionWinner = CoreAuctionWinner
typealias EnhancedAuctionBid = CoreEnhancedAuctionBid
typealias BackupBidder = CoreBackupBidder
typealias OfferResponse = CoreOfferResponse
typealias AuctionPaymentStatus = CoreAuctionPaymentStatus
typealias BidStatus = CoreBidStatus
typealias DepositStatus = CoreDepositStatus

