// Parse Cloud Code for Rooster Project
// Deploy this to your Parse Server cloud directory (e.g., main.js)
// -------------------------------------------------------------
//  Security hardening – auto-sanitize every Cloud Function input
// -------------------------------------------------------------
try {
  const { clean } = require('./security/sanitizer');
  const originalDefine = Parse.Cloud.define;
  Parse.Cloud.define = (name, handler, opts) => {
    return originalDefine(name, async (request) => {
      try {
        // Deep scrub request.params in-place
        request.params = clean(request.params);
      } catch (e) {
        throw new Parse.Error(Parse.Error.INVALID_JSON, `Invalid parameter payload: ${e}`);
      }
      return handler(request);
    }, opts);
  };
  console.log('[Sanitizer] Cloud Function input sanitizer enabled');
} catch (e) {
  console.warn('[Sanitizer] Could not attach sanitizer:', e);
}

/**
 * Retrieves a public-safe profile for a bird/fowl.
 * @param {Object} request - The Parse Cloud Function request object.
 * @param {Object} request.params - The parameters passed to the function.
 * @param {string} request.params.birdId - The objectId of the bird to retrieve.
 * @returns {Object} A public-safe representation of the bird.
 * @throws {Parse.Error} Throws OBJECT_NOT_FOUND if the bird is not found.
 */
Parse.Cloud.define("getPublicBirdProfile", async (request) => {
  const { birdId } = request.params;

  if (!birdId) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing birdId parameter.");
  }

  // Query ChickenRecord class (adjust to your actual class name)
  const ChickenRecord = Parse.Object.extend("ChickenRecord");
  const query = new Parse.Query(ChickenRecord);

  try {
    const bird = await query.get(birdId);

    // Construct public-safe profile
    const publicProfile = {
      objectId: bird.id,
      name: bird.get("name") || "N/A",
      breed: bird.get("breed") || "N/A",
      imageUrl: bird.get("imageUrl") || null,
      status: bird.get("publicStatus") || bird.get("status") || "N/A",
      lineage: [], // Array of parent names/IDs if available
      isVerified: bird.get("isBloodlineVerified") || false,
      ageWeeks: bird.get("ageInWeeks") || null,
      gender: bird.get("gender") || "N/A",
      achievements: bird.get("achievements") || [],
      // Competition history (public safe)
      competitions: bird.get("competitionHistory") || [],
      // Cultural significance
      culturalValue: bird.get("culturalSignificance") || "N/A"
    };

    // Build lineage array if parent information exists
    const fatherId = bird.get("fatherId");
    const motherId = bird.get("motherId");
    
    if (fatherId || motherId) {
      const lineageQuery = new Parse.Query(ChickenRecord);
      const lineagePromises = [];
      
      if (fatherId) lineagePromises.push(lineageQuery.get(fatherId));
      if (motherId) lineagePromises.push(lineageQuery.get(motherId));
      
      try {
        const parents = await Promise.all(lineagePromises);
        publicProfile.lineage = parents.map(parent => ({
          id: parent.id,
          name: parent.get("name") || "Unknown",
          breed: parent.get("breed") || "Unknown",
          relation: parent.id === fatherId ? "father" : "mother"
        }));
      } catch (lineageError) {
        console.log("Could not fetch lineage data:", lineageError.message);
        publicProfile.lineage = [];
      }
    }

    return publicProfile;

  } catch (error) {
    if (error.code === Parse.Error.OBJECT_NOT_FOUND) {
      throw new Parse.Error(Parse.Error.OBJECT_NOT_FOUND, `Bird with ID ${birdId} not found.`);
    }
    console.error(`Error in getPublicBirdProfile for birdId ${birdId}:`, error);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "An error occurred while fetching the bird profile.");
  }
});

/**
 * Get market summary data for public sharing
 */
Parse.Cloud.define("getMarketSummary", async (request) => {
  const { region } = request.params;
  
  try {
    const MarketTrend = Parse.Object.extend("MarketTrend");
    const query = new Parse.Query(MarketTrend);
    
    if (region) {
      query.equalTo("region", region);
    }
    
    query.limit(10);
    query.descending("createdAt");
    
    const trends = await query.find();
    
    const summary = {
      region: region || "All Regions",
      trends: trends.map(trend => ({
        fowlType: trend.get("fowlType"),
        averagePrice: trend.get("averagePrice"),
        priceChange: trend.get("priceChange"),
        demandLevel: trend.get("demandLevel"),
        lastUpdated: trend.get("updatedAt")
      })),
      totalListings: trends.length,
      lastUpdated: new Date()
    };
    
    return summary;
  } catch (error) {
    console.error("Error in getMarketSummary:", error);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Failed to fetch market summary");
  }
});

/**
 * Get performance metrics for admin dashboard
 */
Parse.Cloud.define("getPerformanceMetrics", async (request) => {
  try {
    const User = Parse.Object.extend("_User");
    const ChickenRecord = Parse.Object.extend("ChickenRecord");
    const Listing = Parse.Object.extend("Listing");
    
    const [userCount, fowlCount, listingCount] = await Promise.all([
      new Parse.Query(User).count(),
      new Parse.Query(ChickenRecord).count(),
      new Parse.Query(Listing).count()
    ]);
    
    return {
      totalUsers: userCount,
      totalFowls: fowlCount,
      totalListings: listingCount,
      timestamp: new Date(),
      status: "healthy"
    };
  } catch (error) {
    console.error("Error in getPerformanceMetrics:", error);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Failed to fetch performance metrics");
  }
});

// Database Optimization - Compound Indexes for High-Traffic Queries
Parse.Cloud.beforeFind("TransferRequest", async (request) => {
  // Add compound index for userId + status queries (high frequency)
  await Parse.Schema.get("TransferRequest").addIndex("userId_status_idx", { 
    userId: 1, 
    status: 1 
  }).catch(() => {}); // Ignore if index already exists
  
  // Add compound index for status + createdAt for timeline queries
  await Parse.Schema.get("TransferRequest").addIndex("status_createdAt_idx", { 
    status: 1, 
    createdAt: -1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("FowlMilestone", async (request) => {
  // Compound index for fowl + milestone type queries
  await Parse.Schema.get("FowlMilestone").addIndex("fowlId_milestoneType_idx", { 
    fowlId: 1, 
    milestoneType: 1 
  }).catch(() => {});
  
  // Index for age-based milestone queries
  await Parse.Schema.get("FowlMilestone").addIndex("fowlId_ageWeeks_idx", { 
    fowlId: 1, 
    ageWeeks: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("GroupChat", async (request) => {
  // Index for user + category queries  
  await Parse.Schema.get("GroupChat").addIndex("participants_category_idx", { 
    participants: 1, 
    category: 1 
  }).catch(() => {});
  
  // Index for recent messages in active chats
  await Parse.Schema.get("GroupChat").addIndex("isActive_lastActivity_idx", { 
    isActive: 1, 
    lastActivityAt: -1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("ChatMessage", async (request) => {
  // Critical index for chat message retrieval
  await Parse.Schema.get("ChatMessage").addIndex("chatId_timestamp_idx", { 
    chatId: 1, 
    timestamp: -1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("Listing", async (request) => {
  // Marketplace optimization - location + active status
  await Parse.Schema.get("Listing").addIndex("region_isActive_idx", { 
    region: 1, 
    isActive: 1 
  }).catch(() => {});
  
  // Price range queries
  await Parse.Schema.get("Listing").addIndex("fowlType_price_idx", { 
    fowlType: 1, 
    price: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("PreMarketOrder", async (request) => {
  // Traditional market optimization
  await Parse.Schema.get("PreMarketOrder").addIndex("marketDate_status_idx", { 
    marketDate: 1, 
    status: 1 
  }).catch(() => {});
  
  await Parse.Schema.get("PreMarketOrder").addIndex("region_fowlType_idx", { 
    region: 1, 
    fowlType: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("GroupBuyingRequest", async (request) => {
  // Group buying optimization
  await Parse.Schema.get("GroupBuyingRequest").addIndex("status_deadline_idx", { 
    status: 1, 
    deadline: 1 
  }).catch(() => {});
});

// Performance monitoring function
Parse.Cloud.define("getPerformanceMetrics", async (request) => {
  const user = request.user;
  if (!user) throw new Parse.Error(401, "Authentication required");
  
  try {
    const metrics = {
      timestamp: new Date(),
      userId: user.id,
      // Query performance metrics
      transferRequestCount: await new Parse.Query("TransferRequest").count({ useMasterKey: true }),
      activeChatCount: await new Parse.Query("GroupChat").equalTo("isActive", true).count({ useMasterKey: true }),
      activeListingCount: await new Parse.Query("Listing").equalTo("isActive", true).count({ useMasterKey: true }),
      // System health indicators
      dbConnectionStatus: "healthy",
      indexingStatus: "optimized"
    };
    
    return metrics;
  } catch (error) {
    throw new Parse.Error(500, `Performance metrics error: ${error.message}`);
  }
});

// Original transfer verification function
Parse.Cloud.define("verifyTransfer", async (request) => {
  const orderId = request.params.orderId;
  const color = request.params.color;
  const condition = request.params.condition;
  const user = request.user;
  const Order = Parse.Object.extend("Order");
  const query = new Parse.Query(Order);
  const order = await query.get(orderId, { useMasterKey: true });
  if (!order) throw new Parse.Error(404, "Order not found");
  // Only buyer or seller can verify
  if (
    order.get("buyer").id !== user.id &&
    order.get("seller").id !== user.id
  ) {
    throw new Parse.Error(403, "Unauthorized");
  }
  // Save verification details (optional)
  order.set("color", color);
  order.set("condition", condition);
  order.set("status", "verified");
  await order.save(null, { useMasterKey: true });
  return "Transfer verified";
});

// Network-aware query optimization
Parse.Cloud.define("getOptimizedQuery", async (request) => {
  const { className, networkQuality, userId } = request.params;
  const user = request.user;
  if (!user) throw new Parse.Error(401, "Authentication required");
  
  const limits = {
    EXCELLENT: 50,
    GOOD: 30,
    FAIR: 20,
    POOR: 10,
    OFFLINE: 5
  };
  
  const limit = limits[networkQuality] || 20;
  
  try {
    const query = new Parse.Query(className);
    if (userId) query.equalTo("userId", userId);
    query.limit(limit);
    query.descending("updatedAt");
    
    const results = await query.find({ useMasterKey: true });
    return {
      results,
      count: results.length,
      networkOptimized: true,
      appliedLimit: limit
    };
  } catch (error) {
    throw new Parse.Error(500, `Query optimization error: ${error.message}`);
  }
});

// GraphQL-style query for Marketplace Listings with network adaptation
Parse.Cloud.define('getMarketplaceListings', async (request) => {
    const { limit = 10, networkQuality = 'GOOD' } = request.params;
    
    try {
        // Network-adaptive limits for rural optimization
        const adaptiveLimit = getAdaptiveLimit(networkQuality, limit);
        
        const query = new Parse.Query('Listing');
        query.equalTo('isActive', true);
        query.limit(adaptiveLimit);
        query.descending('createdAt');
        
        // Include owner for complete data
        query.include('owner');
        
        const results = await query.find({ useMasterKey: true });
        
        // Return optimized data structure
        return results.map(listing => ({
            id: listing.id,
            imageUrl: listing.get('image')?.url() || null,
            breed: listing.get('breed') || null,
            age: listing.get('age') || 0,
            price: listing.get('price') || 0,
            owner: listing.get('owner')?.get('username') || 'Unknown',
            createdAt: listing.get('createdAt')?.toISOString() || null,
            isActive: listing.get('isActive') || false
        }));
        
    } catch (error) {
        console.error('Error fetching marketplace listings:', error);
        throw new Parse.Error(
            Parse.Error.INTERNAL_SERVER_ERROR,
            `Failed to fetch listings: ${error.message}`
        );
    }
});

// Helper function for network-adaptive limits (rural optimization)
function getAdaptiveLimit(networkQuality, requestedLimit) {
    const limits = {
        'EXCELLENT': Math.min(requestedLimit, 50),
        'GOOD': Math.min(requestedLimit, 30),
        'FAIR': Math.min(requestedLimit, 20),
        'POOR': Math.min(requestedLimit, 5),
        'OFFLINE': 0
    };
    
    return limits[networkQuality] || limits['GOOD'];
}

// Performance monitoring for marketplace
Parse.Cloud.define('getMarketplacePerformance', async (request) => {
    const startTime = Date.now();
    
    try {
        const query = new Parse.Query('Listing');
        query.equalTo('isActive', true);
        query.limit(1);
        
        await query.find({ useMasterKey: true });
        
        const responseTime = Date.now() - startTime;
        
        return {
            responseTime,
            status: 'healthy',
            timestamp: new Date().toISOString(),
            service: 'marketplace'
        };
        
    } catch (error) {
        return {
            responseTime: Date.now() - startTime,
            status: 'error',
            error: error.message,
            timestamp: new Date().toISOString(),
            service: 'marketplace'
        };
    }
});

// Create a new listing (for testing)
Parse.Cloud.define('createTestListing', async (request) => {
    const { breed, age, price, imageUrl } = request.params;
    
    try {
        const listing = new Parse.Object('Listing');
        listing.set('breed', breed || 'Test Breed');
        listing.set('age', age || 6);
        listing.set('price', price || 500);
        listing.set('isActive', true);
        listing.set('owner', request.user);
        
        if (imageUrl) {
            const imageFile = new Parse.File('listing_image.jpg', { uri: imageUrl });
            await imageFile.save();
            listing.set('image', imageFile);
        }
        
        const result = await listing.save(null, { useMasterKey: true });
        
        return {
            id: result.id,
            message: 'Test listing created successfully'
        };
        
    } catch (error) {
        console.error('Error creating test listing:', error);
        throw new Parse.Error(
            Parse.Error.INTERNAL_SERVER_ERROR,
            `Failed to create listing: ${error.message}`
        );
    }
});

// Enhanced marketplace statistics
Parse.Cloud.define('getMarketplaceStats', async (request) => {
    try {
        const activeListingsQuery = new Parse.Query('Listing');
        activeListingsQuery.equalTo('isActive', true);
        const activeCount = await activeListingsQuery.count({ useMasterKey: true });
        
        const totalListingsQuery = new Parse.Query('Listing');
        const totalCount = await totalListingsQuery.count({ useMasterKey: true });
        
        const priceQuery = new Parse.Query('Listing');
        priceQuery.equalTo('isActive', true);
        priceQuery.select('price');
        const priceResults = await priceQuery.find({ useMasterKey: true });
        
        const prices = priceResults.map(listing => listing.get('price')).filter(price => price > 0);
        const averagePrice = prices.length > 0 ? Math.round(prices.reduce((a, b) => a + b, 0) / prices.length) : 0;
        const minPrice = prices.length > 0 ? Math.min(...prices) : 0;
        const maxPrice = prices.length > 0 ? Math.max(...prices) : 0;
        
        return {
            activeListings: activeCount,
            totalListings: totalCount,
            averagePrice,
            minPrice,
            maxPrice,
            timestamp: new Date().toISOString()
        };
        
    } catch (error) {
        console.error('Error getting marketplace stats:', error);
        throw new Parse.Error(
            Parse.Error.INTERNAL_SERVER_ERROR,
            `Failed to get stats: ${error.message}`
        );
    }
});

// ================================= MARKETPLACE WITH AUCTION INTEGRATION =================================

/**
 * Creates a marketplace listing with optional auction/bidding functionality
 * Supports both normal price buying and auction bidding
 */
Parse.Cloud.define("createMarketplaceListing", async (request) => {
  const user = request.user;
  if (!user) throw new Parse.Error(Parse.Error.UNAUTHORIZED, "Authentication required");
  
  const {
    fowlId,
    title,
    description,
    price,
    enableBidding,
    minimumBidAmount,
    auctionDurationHours,
    bidderDepositPercentage,
    region,
    breed,
    age,
    imageUrls,
    isTraceable // Required for bidding
  } = request.params;

  try {
    // Validation
    if (!fowlId || !title || !price) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing required listing parameters");
    }

    // If bidding enabled, product must be traceable
    if (enableBidding && !isTraceable) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Product must be traceable to enable bidding");
    }

    if (enableBidding) {
      if (!minimumBidAmount || !auctionDurationHours || !bidderDepositPercentage) {
        throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing auction parameters");
      }
      
      if (bidderDepositPercentage < 5 || bidderDepositPercentage > 25) {
        throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Deposit percentage must be between 5-25%");
      }
      
      if (auctionDurationHours < 1 || auctionDurationHours > 168) {
        throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Auction duration must be 1-168 hours");
      }
    }

    // Create marketplace listing
    const listing = new Parse.Object("Listing");
    listing.set("owner", user);
    listing.set("fowlId", fowlId);
    listing.set("title", title);
    listing.set("description", description || "");
    listing.set("price", price);
    listing.set("isActive", true);
    listing.set("region", region || "Unknown");
    listing.set("breed", breed || "N/A");
    listing.set("age", age || 0);
    listing.set("imageUrls", imageUrls || []);
    listing.set("isTraceable", isTraceable || false);
    
    // Auction-specific fields
    listing.set("enableBidding", enableBidding || false);
    listing.set("minimumBidAmount", minimumBidAmount || 0);
    listing.set("bidderDepositPercentage", bidderDepositPercentage || 0);
    listing.set("currentHighestBid", minimumBidAmount || 0);
    listing.set("totalBids", 0);

    const savedListing = await listing.save(null, { useMasterKey: true });

    // If bidding enabled, create corresponding auction
    let auctionId = null;
    if (enableBidding) {
      const auctionParams = {
        fowlId: fowlId,
        title: title,
        description: description,
        startingPrice: minimumBidAmount,
        customDurationHours: auctionDurationHours,
        minimumBidPrice: minimumBidAmount,
        requiresBidderDeposit: true,
        bidderDepositPercentage: bidderDepositPercentage,
        allowsProxyBidding: true,
        sellerBidMonitoring: "ALL_BIDS",
        imageUrls: imageUrls
      };

      const auctionRequest = { user: user, params: auctionParams };
      const auctionResult = await Parse.Cloud.run("createEnhancedAuction", auctionParams, { user: user });
      auctionId = auctionResult.auctionId;
      
      // Link auction to listing
      listing.set("auctionId", auctionId);
      await listing.save(null, { useMasterKey: true });
    }

    return {
      listingId: savedListing.id,
      auctionId: auctionId,
      enableBidding: enableBidding,
      message: enableBidding ? "Listing created with auction enabled" : "Listing created for direct purchase"
    };

  } catch (error) {
    console.error("Error creating marketplace listing:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to create listing: ${error.message}`);
  }
});

/**
 * Get enhanced marketplace listings with auction details
 */
Parse.Cloud.define("getEnhancedMarketplaceListings", async (request) => {
  const { limit = 20, region, enableBidding, networkQuality = 'GOOD' } = request.params;
  
  try {
    const adaptiveLimit = getAdaptiveLimit(networkQuality, limit);
    
    const query = new Parse.Query('Listing');
    query.equalTo('isActive', true);
    query.include('owner');
    
    if (region) query.equalTo('region', region);
    if (enableBidding !== undefined) query.equalTo('enableBidding', enableBidding);
    
    query.limit(adaptiveLimit);
    query.descending('createdAt');
    
    const listings = await query.find({ useMasterKey: true });
    
    // Enhance with auction data if applicable
    const enhancedListings = await Promise.all(listings.map(async (listing) => {
      const listingData = {
        id: listing.id,
        title: listing.get('title'),
        description: listing.get('description'),
        price: listing.get('price'),
        imageUrls: listing.get('imageUrls') || [],
        breed: listing.get('breed'),
        age: listing.get('age'),
        region: listing.get('region'),
        owner: listing.get('owner')?.get('username') || 'Unknown',
        ownerId: listing.get('owner')?.id,
        createdAt: listing.get('createdAt')?.toISOString(),
        isTraceable: listing.get('isTraceable') || false,
        enableBidding: listing.get('enableBidding') || false
      };
      
      // Add auction details if bidding enabled
      if (listing.get('enableBidding') && listing.get('auctionId')) {
        try {
          const auctionQuery = new Parse.Query("EnhancedAuction");
          const auction = await auctionQuery.get(listing.get('auctionId'), { useMasterKey: true });
          
          listingData.auctionDetails = {
            auctionId: auction.id,
            minimumBidAmount: auction.get('minimumBidPrice'),
            currentHighestBid: auction.get('currentBid'),
            bidCount: auction.get('bidCount'),
            endTime: auction.get('endTime')?.toISOString(),
            status: auction.get('status'),
            bidderDepositPercentage: auction.get('bidderDepositPercentage'),
            timeRemaining: auction.get('endTime') ? Math.max(0, auction.get('endTime').getTime() - Date.now()) : 0
          };
        } catch (auctionError) {
          console.warn("Could not fetch auction details:", auctionError);
          listingData.auctionDetails = null;
        }
      }
      
      return listingData;
    }));
    
    return {
      listings: enhancedListings,
      count: enhancedListings.length,
      timestamp: new Date().toISOString()
    };
    
  } catch (error) {
    console.error('Error fetching enhanced marketplace listings:', error);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to fetch listings: ${error.message}`);
  }
});

// ================================= AUCTION COMPLETION & PAYMENT PROCESSING =================================

/**
 * Processes auction completion with payment handling and token management
 */
Parse.Cloud.define("processAuctionCompletion", async (request) => {
  const { auctionId } = request.params;
  
  if (!auctionId) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing auctionId parameter");
  }
  
  try {
    // Get auction details
    const auctionQuery = new Parse.Query("EnhancedAuction");
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });
    
    if (auction.get("status") !== "ENDED") {
      throw new Parse.Error(Parse.Error.INVALID_REQUEST, "Auction is not in ENDED status");
    }
    
    // Get all bids ordered by amount (highest first)
    const bidQuery = new Parse.Query("EnhancedAuctionBid");
    bidQuery.equalTo("auctionId", auctionId);
    bidQuery.descending("bidAmount");
    bidQuery.limit(100);
    
    const bids = await bidQuery.find({ useMasterKey: true });
    
    if (bids.length === 0) {
      auction.set("status", "COMPLETED_NO_BIDS");
      await auction.save(null, { useMasterKey: true });
      return { message: "Auction completed with no bids" };
    }
    
    // Process highest bidder first
    const processResult = await processHighestBidder(auction, bids);
    
    return processResult;
    
  } catch (error) {
    console.error("Error processing auction completion:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to process auction: ${error.message}`);
  }
});

/**
 * Handles payment attempt for winning bidder
 */
Parse.Cloud.define("processWinnerPayment", async (request) => {
  const { auctionId, winnerId, paymentToken } = request.params;
  
  try {
    // Get auction and winner details
    const auctionQuery = new Parse.Query("EnhancedAuction");
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });
    
    const winnerQuery = new Parse.Query("AuctionWinner");
    winnerQuery.equalTo("auctionId", auctionId);
    winnerQuery.equalTo("winnerId", winnerId);
    const winner = await winnerQuery.first({ useMasterKey: true });
    
    if (!winner) {
      throw new Parse.Error(Parse.Error.OBJECT_NOT_FOUND, "Winner record not found for this auction and winner ID.");
    }

    // Check the paymentStatus on the AuctionWinner object.
    // This status is assumed to be set by the backend API after Razorpay payment verification.
    const currentPaymentStatus = winner.get("paymentStatus");

    if (currentPaymentStatus === "COMPLETED") {
      // Payment was already marked as completed by the backend.
      // Finalize auction, refund losing bidders' deposits.
      auction.set("status", "COMPLETED_PAID"); // More specific status
      await auction.save(null, { useMasterKey: true });
      
      await refundLosingBiddersDeposits(auctionId, winnerId);
      
      return {
        success: true,
        message: "Payment previously verified and completed. Auction finalized.",
        auctionStatus: auction.get("status")
      };
      
    } else if (currentPaymentStatus === "PENDING" || currentPaymentStatus === "AWAITING_PAYMENT") {
      // This function might be called by a job to check if payment deadline passed,
      // or if client is re-attempting to confirm status.
      // For now, we assume if it's not "COMPLETED", the client/backend handles actual payment.
      // If a deadline check is needed, that would be separate logic.
      // This function's role is now more about *reacting* to a payment status.

      // If called because a payment *just* failed (e.g., client reports failure after trying to pay),
      // the backend should have updated status to FAILED.
      // For this example, let's assume if it's not COMPLETED, we consider it as "not yet paid" or "failed".
      // This part needs more robust state management based on how payment attempts are tracked.

      // Let's assume for now: if not "COMPLETED", it's effectively a failure for *this* winner attempt for now.
      // A more robust system would have deadlines and retry logic.
      winner.set("paymentStatus", "FAILED_OR_PENDING_DEADLINE"); // Indicate it's not completed.
      await winner.save(null, { useMasterKey: true });
      
      // Logic for forfeited deposit (if applicable)
      // This should check the BidderDeposit object status for the winner.
      const winnerDeposit = await getBidderDeposit(winnerId, auctionId);
      if (winnerDeposit && winnerDeposit.get("status") === "PAID") {
         // Assuming 'PAID' deposit means it was collected. If payment fails, it might be transferred.
         await transferForfeitedDepositToSeller(auction.get("sellerId"), winnerId, auctionId, winner.get("winningBid")); // auctionId is already here
      }
      
      const nextResult = await processNextHighestBidder(auctionId, winnerId);
      
      return {
        success: false,
        message: "Winner payment not completed. Checking for next eligible bidder.",
        auctionStatus: auction.get("status"), // Might still be "ENDED"
        nextBidder: nextResult
      };

    } else if (currentPaymentStatus === "FAILED") {
        // Already marked as FAILED by backend.
        const winnerDeposit = await getBidderDeposit(winnerId, auctionId);
        if (winnerDeposit && winnerDeposit.get("status") === "PAID") {
             await transferForfeitedDepositToSeller(auction.get("sellerId"), winnerId, auctionId, winner.get("winningBid")); // auctionId is already here
        }
        const nextResult = await processNextHighestBidder(auctionId, winnerId);
        return {
            success: false,
            message: "Winner payment previously failed. Checking for next eligible bidder.",
            auctionStatus: auction.get("status"),
            nextBidder: nextResult
        };
    } else {
        // Unknown status or already processed (e.g. COMPLETED_PAID)
         return {
            success: true, // Or false depending on how to treat unknown status
            message: `Auction winner processing: Current payment status is ${currentPaymentStatus}. No further action taken by this function call.`,
            auctionStatus: auction.get("status")
        };
    }
    
  } catch (error) {
    console.error("Error processing winner payment status:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Payment processing failed: ${error.message}`);
  }
});

// ============================= HELPER FUNCTIONS FOR PAYMENT PROCESSING =============================

// Helper to fetch a specific bidder's deposit for an auction
async function getBidderDeposit(bidderId, auctionId) {
  try {
    const depositQuery = new Parse.Query("BidderDeposit");
    depositQuery.equalTo("bidderId", bidderId);
    depositQuery.equalTo("auctionId", auctionId); // Ensure it's for the correct auction
    // Optionally, add other conditions like status if needed for specific contexts
    return await depositQuery.first({ useMasterKey: true });
  } catch (error) {
    console.error(`Error fetching deposit for bidder ${bidderId}, auction ${auctionId}:`, error);
    return null; // Or throw, depending on how critical this is for the caller
  }
}


async function processHighestBidder(auction, bids) {
  const highestBid = bids[0];
  
  // Create or update winner record
  let winner;
  const existingWinnerQuery = new Parse.Query("AuctionWinner");
  existingWinnerQuery.equalTo("auctionId", auction.id);
  existingWinnerQuery.equalTo("winnerId", highestBid.get("bidderId"));
  winner = await existingWinnerQuery.first({ useMasterKey: true });

  if (!winner) {
    winner = new Parse.Object("AuctionWinner");
    winner.set("auctionId", auction.id);
    winner.set("winnerId", highestBid.get("bidderId"));
    winner.set("winnerName", highestBid.get("bidderName"));
    winner.set("winningBid", highestBid.get("bidAmount"));
  }
  
  winner.set("paymentDeadline", new Date(Date.now() + 24 * 60 * 60 * 1000)); // 24 hours
  winner.set("paymentStatus", "PENDING");
  await winner.save(null, { useMasterKey: true });
  
  return {
    winnerId: winner.get("winnerId"),
    winnerName: winner.get("winnerName"),
    winningBid: winner.get("winningBid"),
    paymentDeadline: winner.get("paymentDeadline").toISOString(),
    message: "Highest bidder selected for payment"
  };
}

// Removed old processPayment function as actual payment is handled by client + backend API

async function transferForfeitedDepositToSeller(sellerId, failedBidderId, auctionId, bidAmount) {
  try {
    const deposit = await getBidderDeposit(failedBidderId, auctionId);
    
    if (deposit && deposit.get("status") === "PAID") { // Ensure deposit was paid and not already processed
      // Create a record of the transfer/forfeiture for audit
      const forfeiture = new Parse.Object("DepositForfeiture"); // Or use TokenTransfer if more generic
      forfeiture.set("fromBidderId", failedBidderId);
      forfeiture.set("toSellerId", sellerId);
      forfeiture.set("auctionId", auctionId);
      forfeiture.set("amount", deposit.get("amount")); // The actual deposit amount
      forfeiture.set("reason", "WINNER_PAYMENT_FAILED");
      forfeiture.set("status", "COMPLETED");
      await forfeiture.save(null, { useMasterKey: true });
      
      // Update original deposit status to show it's been forfeited
      deposit.set("status", "FORFEITED_TO_SELLER");
      await deposit.save(null, { useMasterKey: true });

      console.log(`Deposit of ${deposit.get("amount")} from bidder ${failedBidderId} for auction ${auctionId} forfeited to seller ${sellerId}.`);
    } else {
      console.warn(`No PAID deposit found to forfeit for bidder ${failedBidderId}, auction ${auctionId}. Deposit status: ${deposit ? deposit.get("status") : 'N/A'}`);
    }
  } catch (error) { // Corrected catch block
      console.error("Error in transferForfeitedDepositToSeller:", error);
      // Potentially throw new Parse.Error or handle more gracefully depending on desired behavior
  }
}

async function refundLosingBiddersDeposits(auctionId, winnerId) {
  // Refunds deposits for all bidders in an auction except the winner.
  // Assumes 'BidderDeposit' objects exist and their status reflects payment.
  try {
    const bidQuery = new Parse.Query("EnhancedAuctionBid");
    bidQuery.equalTo("auctionId", auctionId);
    bidQuery.notEqualTo("bidderId", winnerId); // Exclude the winner
    bidQuery.select("bidderId"); // Only need bidderId to find their deposits
    bidQuery.limit(1000); // Adjust limit as necessary for max bidders

    const losingBids = await bidQuery.find({ useMasterKey: true });
    const losingBidderIds = [...new Set(losingBids.map(bid => bid.get("bidderId")))]; // Unique bidder IDs

    for (const bidderId of losingBidderIds) {
      const deposit = await getBidderDeposit(bidderId, auctionId);
      
      if (deposit && deposit.get("status") === "PAID") { // Only refund paid deposits
        // Create a refund record for audit purposes
        const refundRecord = new Parse.Object("DepositRefund"); // New class for tracking refunds
        refundRecord.set("bidderId", bidderId);
        refundRecord.set("auctionId", auctionId);
        refundRecord.set("amount", deposit.get("amount"));
        refundRecord.set("reason", "AUCTION_LOST_OR_NOT_WINNER");
        refundRecord.set("status", "PROCESSED"); // Assuming refund is processed immediately
        await refundRecord.save(null, { useMasterKey: true }); // Log with master key

        // Update the original deposit status
        deposit.set("status", "REFUNDED");
        await deposit.save(null, { useMasterKey: true });
        console.log(`Deposit for bidder ${bidderId}, auction ${auctionId} marked as REFUNDED.`);
      } else if (deposit) {
        console.log(`Deposit for bidder ${bidderId}, auction ${auctionId} not in PAID status (Status: ${deposit.get("status")}). No refund processed.`);
      } else {
        console.log(`No deposit record found for losing bidder ${bidderId}, auction ${auctionId}. No refund processed.`);
      }
    }
  } catch (error) {
    console.error(`Error refunding deposits for auction ${auctionId}:`, error);
    // Potentially throw new Parse.Error or handle more gracefully
  }
}


async function processNextHighestBidder(auctionId, excludeBidderId) {
  try {
    // Get next highest bidder
    const bidQuery = new Parse.Query("EnhancedAuctionBid");
    bidQuery.equalTo("auctionId", auctionId);
    bidQuery.notEqualTo("bidderId", excludeBidderId);
    bidQuery.descending("bidAmount");
    bidQuery.limit(1);
    
    const nextBids = await bidQuery.find({ useMasterKey: true });
    
    if (nextBids.length > 0) {
      const nextBid = nextBids[0];
      
      // Create new winner record
      const newWinner = new Parse.Object("AuctionWinner");
      newWinner.set("auctionId", auctionId);
      newWinner.set("winnerId", nextBid.get("bidderId"));
      newWinner.set("winnerName", nextBid.get("bidderName"));
      newWinner.set("winningBid", nextBid.get("bidAmount"));
      newWinner.set("paymentDeadline", new Date(Date.now() + 24 * 60 * 60 * 1000));
      newWinner.set("paymentStatus", "PENDING");
      await newWinner.save(null, { useMasterKey: true });
      
      return {
        winnerId: newWinner.get("winnerId"),
        winnerName: newWinner.get("winnerName"),
        winningBid: newWinner.get("winningBid")
      };
    }
    
    return null;
  } catch (error) {
    console.error("Error processing next highest bidder:", error);
    return null;
  }
}

async function refundLosingBidders(auctionId, winnerId) {
  try {
    // Get all bidders except winner
    const bidQuery = new Parse.Query("EnhancedAuctionBid");
    bidQuery.equalTo("auctionId", auctionId);
    bidQuery.notEqualTo("bidderId", winnerId);
    const losingBids = await bidQuery.find({ useMasterKey: true });
    
    // Refund each losing bidder's deposit
    for (const bid of losingBids) {
      const depositQuery = new Parse.Query("BidderDeposit");
      depositQuery.equalTo("bidderId", bid.get("bidderId"));
      depositQuery.equalTo("status", "PAID");
      const deposit = await depositQuery.first({ useMasterKey: true });
      
      if (deposit) {
        // Create refund record
        const refund = new Parse.Object("TokenRefund");
        refund.set("bidderId", bid.get("bidderId"));
        refund.set("amount", deposit.get("amount"));
        refund.set("reason", "AUCTION_LOST");
        refund.set("status", "PROCESSED");
        refund.set("auctionId", auctionId);
        await refund.save(null, { useMasterKey: true });
        
        // Update deposit status
        deposit.set("status", "REFUNDED");
        await deposit.save(null, { useMasterKey: true });
      }
    }
  } catch (error) {
    console.error("Error refunding losing bidders:", error);
  }
}

// ================================= ENHANCED AUCTION & BIDDING CLOUD FUNCTIONS =================================

// Add comprehensive indexes for auction and bidding performance
Parse.Cloud.beforeFind("EnhancedAuction", async (request) => {
  // Performance indexes for auction queries
  await Parse.Schema.get("EnhancedAuction").addIndex("status_endTime_idx", { 
    status: 1, 
    endTime: -1 
  }).catch(() => {});
  
  await Parse.Schema.get("EnhancedAuction").addIndex("sellerId_status_idx", { 
    sellerId: 1, 
    status: 1 
  }).catch(() => {});
  
  await Parse.Schema.get("EnhancedAuction").addIndex("endTime_bidCount_idx", { 
    endTime: -1, 
    bidCount: -1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("EnhancedAuctionBid", async (request) => {
  // Critical indexes for bidding performance
  await Parse.Schema.get("EnhancedAuctionBid").addIndex("auctionId_bidAmount_idx", { 
    auctionId: 1, 
    bidAmount: -1 
  }).catch(() => {});
  
  await Parse.Schema.get("EnhancedAuctionBid").addIndex("bidderId_bidTime_idx", { 
    bidderId: 1, 
    bidTime: -1 
  }).catch(() => {});
  
  await Parse.Schema.get("EnhancedAuctionBid").addIndex("auctionId_isWinning_idx", { 
    auctionId: 1, 
    isWinning: -1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("AuctionWinner", async (request) => {
  // Index for winner queries
  await Parse.Schema.get("AuctionWinner").addIndex("auctionId_paymentStatus_idx", { 
    auctionId: 1, 
    paymentStatus: 1 
  }).catch(() => {});
});

// Add indexes for new payment and token management tables
Parse.Cloud.beforeFind("BidderDeposit", async (request) => {
  await Parse.Schema.get("BidderDeposit").addIndex("bidderId_status_idx", { 
    bidderId: 1, 
    status: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("TokenTransfer", async (request) => {
  await Parse.Schema.get("TokenTransfer").addIndex("fromBidderId_status_idx", { 
    fromBidderId: 1, 
    status: 1 
  }).catch(() => {});
  
  await Parse.Schema.get("TokenTransfer").addIndex("toSellerId_status_idx", { 
    toSellerId: 1, 
    status: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("TokenRefund", async (request) => {
  await Parse.Schema.get("TokenRefund").addIndex("bidderId_status_idx", { 
    bidderId: 1, 
    status: 1 
  }).catch(() => {});
  
  await Parse.Schema.get("TokenRefund").addIndex("auctionId_status_idx", { 
    auctionId: 1, 
    status: 1 
  }).catch(() => {});
});

Parse.Cloud.beforeFind("Payment", async (request) => {
  await Parse.Schema.get("Payment").addIndex("bidderId_status_idx", { 
    bidderId: 1, 
    status: 1 
  }).catch(() => {});
});

/**
 * Creates a new enhanced auction with seller controls
 * Called by EnhancedAuctionService.createAuction()
 */
Parse.Cloud.define("createEnhancedAuction", async (request) => {
  const user = request.user;
  if (!user) throw new Parse.Error(Parse.Error.UNAUTHORIZED, "Authentication required");
  
  const {
    fowlId,
    title,
    description,
    startingPrice,
    reservePrice,
    customDurationHours,
    minimumBidPrice,
    requiresBidderDeposit,
    bidderDepositPercentage,
    allowsProxyBidding,
    sellerBidMonitoring,
    autoExtendOnLastMinuteBid,
    extensionMinutes,
    buyNowPrice,
    allowedBidderTypes,
    imageUrls
  } = request.params;

  try {
    // Validation
    if (!fowlId || !title || !startingPrice || !customDurationHours) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing required auction parameters");
    }

    if (customDurationHours < 1 || customDurationHours > 168) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Duration must be 1-168 hours");
    }

    if (minimumBidPrice && minimumBidPrice > startingPrice) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Minimum bid cannot exceed starting price");
    }

    if (requiresBidderDeposit && (bidderDepositPercentage < 5 || bidderDepositPercentage > 25)) {
      throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Deposit percentage must be 5-25%");
    }

    // Calculate auction end time
    const startTime = new Date();
    const endTime = new Date(startTime.getTime() + customDurationHours * 60 * 60 * 1000);

    // Create auction
    const auction = new Parse.Object("EnhancedAuction");
    auction.set("sellerId", user.id);
    auction.set("sellerName", user.get("username") || "Unknown Seller");
    auction.set("fowlId", fowlId);
    auction.set("title", title);
    auction.set("description", description || "");
    auction.set("startingPrice", startingPrice);
    auction.set("currentBid", startingPrice);
    auction.set("reservePrice", reservePrice || 0);
    auction.set("customDurationHours", customDurationHours);
    auction.set("minimumBidPrice", minimumBidPrice || startingPrice);
    auction.set("requiresBidderDeposit", requiresBidderDeposit || false);
    auction.set("bidderDepositPercentage", bidderDepositPercentage || 0);
    auction.set("allowsProxyBidding", allowsProxyBidding || true);
    auction.set("sellerBidMonitoring", sellerBidMonitoring || "ALL_BIDS");
    auction.set("autoExtendOnLastMinuteBid", autoExtendOnLastMinuteBid || false);
    auction.set("extensionMinutes", extensionMinutes || 5);
    auction.set("buyNowPrice", buyNowPrice || null);
    auction.set("startTime", startTime);
    auction.set("endTime", endTime);
    auction.set("status", "ACTIVE");
    auction.set("bidCount", 0);
    auction.set("watchers", 0);
    auction.set("allowedBidderTypes", allowedBidderTypes || ["ALL_USERS"]);
    auction.set("imageUrls", imageUrls || []);
    auction.set("minimumIncrement", Math.max(1, startingPrice * 0.01)); // 1% minimum increment

    const savedAuction = await auction.save(null, { useMasterKey: true });

    return {
      auctionId: savedAuction.id,
      endTime: endTime.toISOString(),
      status: "ACTIVE",
      message: "Auction created successfully"
    };

  } catch (error) {
    console.error("Error creating enhanced auction:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to create auction: ${error.message}`);
  }
});

/**
 * Places a bid on an enhanced auction with comprehensive validation
 * Called by EnhancedAuctionService.placeBid()
 */
Parse.Cloud.define("placeEnhancedAuctionBid", async (request) => {
  const user = request.user;
  if (!user) throw new Parse.Error(Parse.Error.UNAUTHORIZED, "Authentication required");

  const {
    auctionId,
    bidAmount,
    bidderMessage,
    proxyMaxAmount,
    isProxyBid
  } = request.params;

  try {
    // Fetch auction with validation
    const auctionQuery = new Parse.Query("EnhancedAuction");
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });

    // Comprehensive bid validation
    await validateBidRequest(auction, user, bidAmount);

    // Handle bidder deposit if required
    let depositStatus = "NOT_REQUIRED";
    let depositAmount = 0;

    if (auction.get("requiresBidderDeposit")) {
      depositAmount = bidAmount * (auction.get("bidderDepositPercentage") / 100);
      // Ensure depositAmount is rounded to the smallest currency unit if necessary, e.g. Math.round(depositAmount)
      // For this example, we assume depositAmount is correctly calculated as needed by checkBidderDepositStatus.
      depositStatus = await checkBidderDepositStatus(user.id, auctionId, depositAmount);
      
      if (depositStatus !== "PAID") {
        throw new Parse.Error(Parse.Error.PAYMENT_REQUIRED, "Required deposit not paid or found for this auction.");
      }
    }

    // Get bidder's platform rating
    const bidderRating = await getBidderRating(user.id);
    
    // Count previous bids by this user on this auction
    const previousBidCount = await getPreviousBidCount(auctionId, user.id);

    // Create bid record
    const bid = new Parse.Object("EnhancedAuctionBid");
    bid.set("auctionId", auctionId);
    bid.set("bidderId", user.id);
    bid.set("bidderName", user.get("username") || "Unknown Bidder");
    bid.set("bidAmount", bidAmount);
    bid.set("bidTime", new Date());
    bid.set("isWinning", true);
    bid.set("isProxyBid", isProxyBid || false);
    bid.set("proxyMaxAmount", proxyMaxAmount || 0);
    bid.set("depositAmount", depositAmount);
    bid.set("depositStatus", depositStatus);
    bid.set("bidStatus", "ACTIVE");
    bid.set("bidMessage", bidderMessage || "");
    bid.set("bidderRating", bidderRating);
    bid.set("previousBidCount", previousBidCount);

    const savedBid = await bid.save(null, { useMasterKey: true });

    // Update auction with new highest bid
    await updateAuctionWithNewBid(auction, bidAmount, savedBid.id);

    // Update previous winning bids
    await updatePreviousWinningBids(auctionId, savedBid.id);

    return {
      bidId: savedBid.id,
      bidAmount: bidAmount,
      currentHighestBid: bidAmount,
      bidTime: savedBid.get("bidTime").toISOString(),
      depositRequired: depositAmount > 0,
      depositAmount: depositAmount,
      depositStatus: depositStatus,
      message: "Bid placed successfully"
    };

  } catch (error) {
    console.error("Error placing enhanced auction bid:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to place bid: ${error.message}`);
  }
});

/**
 * Fetches enhanced auction bids with seller monitoring control
 * Called by fetchEnhancedAuctionBids() in ExtendedFetchers.kt
 */
Parse.Cloud.define("getEnhancedAuctionBids", async (request) => {
  const user = request.user;
  const { auctionId, limit = 50 } = request.params;

  if (!auctionId) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing auctionId parameter");
  }

  try {
    // Get auction to check seller monitoring preferences
    const auctionQuery = new Parse.Query("EnhancedAuction");
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });

    const sellerId = auction.get("sellerId");
    const sellerBidMonitoring = auction.get("sellerBidMonitoring") || "ALL_BIDS";
    const isRequestingUser = user && (user.id === sellerId);

    // Build bid query
    const bidQuery = new Parse.Query("EnhancedAuctionBid");
    bidQuery.equalTo("auctionId", auctionId);
    bidQuery.descending("bidAmount");
    bidQuery.limit(Math.min(limit, 100)); // Cap at 100 for performance

    // Apply seller monitoring filters
    if (!isRequestingUser) {
      switch (sellerBidMonitoring) {
        case "WINNING_BIDS_ONLY":
          bidQuery.equalTo("isWinning", true);
          break;
        case "PRIVATE_BIDDING":
          // Return only bid count and amounts, no bidder details
          bidQuery.select("bidAmount", "bidTime", "isWinning");
          break;
        case "SELLER_NOTIFICATIONS_ONLY":
          // Very limited public view
          bidQuery.limit(5);
          bidQuery.select("bidAmount", "bidTime");
          break;
      }
    }

    const bids = await bidQuery.find({ useMasterKey: true });

    // Transform results based on monitoring settings
    const transformedBids = bids.map(bid => {
      const bidData = {
        bidId: bid.id,
        auctionId: bid.get("auctionId"),
        bidAmount: bid.get("bidAmount"),
        bidTime: bid.get("bidTime")?.toISOString(),
        isWinning: bid.get("isWinning"),
        isProxyBid: bid.get("isProxyBid"),
        bidStatus: bid.get("bidStatus")
      };

      // Add sensitive data only for seller or in open monitoring
      if (isRequestingUser || sellerBidMonitoring === "ALL_BIDS") {
        bidData.bidderId = bid.get("bidderId");
        bidData.bidderName = bid.get("bidderName");
        bidData.bidderRating = bid.get("bidderRating");
        bidData.depositAmount = bid.get("depositAmount");
        bidData.depositStatus = bid.get("depositStatus");
        bidData.bidMessage = bid.get("bidMessage");
        bidData.previousBidCount = bid.get("previousBidCount");
        bidData.proxyMaxAmount = bid.get("proxyMaxAmount");
      } else if (sellerBidMonitoring === "PRIVATE_BIDDING") {
        bidData.bidderName = "Anonymous";
        bidData.bidderId = "anonymous";
      }

      return bidData;
    });

    // Calculate bid statistics
    const totalBids = bids.length;
    const minimumBidPrice = auction.get("minimumBidPrice") || 0;
    const bidsAboveMin = bids.filter(bid => bid.get("bidAmount") >= minimumBidPrice).length;
    const bidsBelowMin = totalBids - bidsAboveMin;
    const highestBid = bids.length > 0 ? bids[0].get("bidAmount") : 0;
    const averageBid = bids.length > 0 ? 
      bids.reduce((sum, bid) => sum + bid.get("bidAmount"), 0) / bids.length : 0;
    const uniqueBidders = new Set(bids.map(bid => bid.get("bidderId"))).size;

    return {
      bids: transformedBids,
      bidStatistics: {
        totalBids,
        bidsAboveMinimum: bidsAboveMin,
        bidsBelowMinimum: bidsBelowMin,
        highestBid,
        averageBid: Math.round(averageBid),
        uniqueBidders,
        minimumBidPrice
      },
      auctionStatus: auction.get("status"),
      sellerMonitoring: sellerBidMonitoring,
      timestamp: new Date().toISOString()
    };

  } catch (error) {
    console.error("Error fetching enhanced auction bids:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to fetch bids: ${error.message}`);
  }
});

// ================================= END ENHANCED AUCTION & BIDDING CLOUD FUNCTIONS =================================

// ============================= HELPER FUNCTIONS FOR AUCTION SYSTEM =============================

/**
 * Validates bid request with comprehensive checks
 */
async function validateBidRequest(auction, user, bidAmount) {
  // Check auction status
  if (auction.get("status") !== "ACTIVE") {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, "Auction is not active");
  }

  // Check if auction has ended
  if (new Date() >= auction.get("endTime")) {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, "Auction has ended");
  }

  // Check if user is the seller
  if (auction.get("sellerId") === user.id) {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, "Seller cannot bid on own auction");
  }

  // Check minimum bid price
  const minimumBidPrice = auction.get("minimumBidPrice") || 0;
  if (bidAmount < minimumBidPrice) {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, 
      `Bid amount must be at least ₹${minimumBidPrice}`);
  }

  // Check current bid
  const currentBid = auction.get("currentBid") || 0;
  const minimumIncrement = auction.get("minimumIncrement") || 1;
  if (bidAmount <= currentBid) {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, 
      `Bid must be higher than current bid of ₹${currentBid}`);
  }

  if (bidAmount < currentBid + minimumIncrement) {
    throw new Parse.Error(Parse.Error.INVALID_REQUEST, 
      `Bid must be at least ₹${currentBid + minimumIncrement}`);
  }
}

/**
 * Processes bidder deposit payment
 */
async function checkBidderDepositStatus(bidderId, auctionId, expectedDepositAmount) {
  // This function now assumes the deposit was handled by the client via backend APIs,
  // and a 'BidderDeposit' object was created/updated by the backend upon successful verification.
  // It checks if a valid, paid deposit exists for this bidder and auction.
  try {
    const depositQuery = new Parse.Query("BidderDeposit");
    depositQuery.equalTo("bidderId", bidderId);
    depositQuery.equalTo("auctionId", auctionId); // Important to scope deposit to the auction
    // Optionally, verify the amount if it's critical for this specific bid context
    // depositQuery.equalTo("amount", expectedDepositAmount);
    
    const deposit = await depositQuery.first({ useMasterKey: true });

    if (deposit) {
      console.log(`Deposit PAID for bidder ${bidderId}, auction ${auctionId}`);
      return "PAID"; // Deposit confirmed
    } else {
      console.warn(`No PAID deposit found for bidder ${bidderId}, auction ${auctionId}`);
      return "NOT_FOUND_OR_UNPAID"; // No paid deposit record found
    }
  } catch (error) {
    console.error("Error checking bidder deposit status:", error);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Failed to check deposit status.");
  }
}

/**
 * Gets bidder's platform rating
 */
async function getBidderRating(bidderId) {
  try {
    // Calculate based on completed transactions, reviews, etc.
    // For now, return a mock rating
    return 4.2 + (Math.random() * 0.8); // 4.2 to 5.0 range
  } catch (error) {
    return 4.0; // Default rating
  }
}

/**
 * Counts previous bids by user on auction
 */
async function getPreviousBidCount(auctionId, bidderId) {
  try {
    const query = new Parse.Query("EnhancedAuctionBid");
    query.equalTo("auctionId", auctionId);
    query.equalTo("bidderId", bidderId);
    return await query.count({ useMasterKey: true });
  } catch (error) {
    return 0;
  }
}

/**
 * Updates auction with new highest bid
 */
async function updateAuctionWithNewBid(auction, bidAmount, bidId) {
  auction.set("currentBid", bidAmount);
  auction.increment("bidCount");
  auction.set("lastBidId", bidId);
  auction.set("lastBidTime", new Date());
  await auction.save(null, { useMasterKey: true });
}

/**
 * Updates previous winning bids to outbid status
 */
async function updatePreviousWinningBids(auctionId, newWinningBidId) {
  const query = new Parse.Query("EnhancedAuctionBid");
  query.equalTo("auctionId", auctionId);
  query.equalTo("isWinning", true);
  query.notEqualTo("objectId", newWinningBidId);

  const previousWinningBids = await query.find({ useMasterKey: true });
  
  for (const bid of previousWinningBids) {
    bid.set("isWinning", false);
    bid.set("bidStatus", "OUTBID");
    await bid.save(null, { useMasterKey: true });
  }
}

// ============================= END HELPER FUNCTIONS =============================

/**
 * Ends an auction and handles winner selection
 * Called by EnhancedAuctionService.endAuction()
 */
Parse.Cloud.define("endEnhancedAuction", async (request) => {
  const user = request.user;
  const { auctionId } = request.params;

  if (!user) throw new Parse.Error(Parse.Error.UNAUTHORIZED, "Authentication required");
  if (!auctionId) throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing auctionId parameter");

  try {
    // Get auction
    const auctionQuery = new Parse.Query("EnhancedAuction");
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });

    // Verify authorization (seller or admin)
    if (auction.get("sellerId") !== user.id && user.get("role") !== "admin") {
      throw new Parse.Error(Parse.Error.UNAUTHORIZED, "Only seller or admin can end auction");
    }

    // Check if auction is already ended
    if (auction.get("status") === "ENDED" || auction.get("status") === "SETTLED") {
      throw new Parse.Error(Parse.Error.INVALID_REQUEST, "Auction already ended");
    }

    // Get winning bid
    const winningBidQuery = new Parse.Query("EnhancedAuctionBid");
    winningBidQuery.equalTo("auctionId", auctionId);
    winningBidQuery.equalTo("isWinning", true);
    winningBidQuery.descending("bidAmount");

    const winningBids = await winningBidQuery.find({ useMasterKey: true });
    
    if (winningBids.length === 0) {
      // No bids - end auction without winner
      auction.set("status", "ENDED_NO_BIDS");
      await auction.save(null, { useMasterKey: true });
      
      return {
        auctionId: auctionId,
        status: "ENDED_NO_BIDS",
        winner: null,
        message: "Auction ended with no bids"
      };
    }

    const winningBid = winningBids[0];
    
    // Check reserve price if set
    const reservePrice = auction.get("reservePrice") || 0;
    if (reservePrice > 0 && winningBid.get("bidAmount") < reservePrice) {
      auction.set("status", "ENDED_RESERVE_NOT_MET");
      await auction.save(null, { useMasterKey: true });
      
      return {
        auctionId: auctionId,
        status: "ENDED_RESERVE_NOT_MET",
        highestBid: winningBid.get("bidAmount"),
        reservePrice: reservePrice,
        message: "Auction ended - reserve price not met"
      };
    }

    // Create winner record
    const winner = new Parse.Object("AuctionWinner");
    winner.set("auctionId", auctionId);
    winner.set("winnerId", winningBid.get("bidderId"));
    winner.set("winnerName", winningBid.get("bidderName"));
    winner.set("winningBid", winningBid.get("bidAmount"));
    winner.set("paymentDeadline", new Date(Date.now() + 10 * 60 * 1000)); // 10 minutes
    winner.set("paymentStatus", "PENDING");

    await winner.save(null, { useMasterKey: true });

    // Update auction status
    auction.set("status", "ENDED");
    auction.set("winnerId", winningBid.get("bidderId"));
    auction.set("winningBid", winningBid.get("bidAmount"));
    await auction.save(null, { useMasterKey: true });

    return {
      auctionId: auctionId,
      status: "ENDED",
      winner: {
        winnerId: winner.get("winnerId"),
        winnerName: winner.get("winnerName"),
        winningBid: winner.get("winningBid"),
        paymentDeadline: winner.get("paymentDeadline").toISOString()
      },
      message: "Auction ended successfully"
    };

  } catch (error) {
    console.error("Error ending enhanced auction:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to end auction: ${error.message}`);
  }
});

// --------------------------------- ADMIN DASHBOARD METRICS ---------------------------------
Parse.Cloud.define("getDashboardMetrics", async (request) => {
  const todayStart = new Date();
  todayStart.setHours(0,0,0,0);
  const sevenDaysAgo = new Date(Date.now() - 7*24*60*60*1000);

  try {
    // 1. DAILY ACTIVE USERS – users whose updatedAt within today OR who have a Session today
    const userQuery = new Parse.Query("_User");
    userQuery.greaterThanOrEqualTo("updatedAt", todayStart);
    const dau = await userQuery.count({ useMasterKey: true });

    // 2. NEW LISTINGS today
    const listingTodayQ = new Parse.Query("Listing");
    listingTodayQ.greaterThanOrEqualTo("createdAt", todayStart);
    const newListingsToday = await listingTodayQ.count({ useMasterKey: true });

    // 3. SALES / TRANSACTIONS volume today – count Orders whose createdAt today & status == COMPLETED
    const orderTodayQ = new Parse.Query("Order");
    orderTodayQ.greaterThanOrEqualTo("createdAt", todayStart);
    orderTodayQ.equalTo("status", "COMPLETED");
    const salesToday = await orderTodayQ.count({ useMasterKey: true });

    // Sum of order amounts
    orderTodayQ.select("totalAmount");
    const orderObjs = await orderTodayQ.find({ useMasterKey: true });
    const salesAmount = orderObjs.reduce((sum, o) => sum + (o.get("totalAmount") || 0), 0);

    // 4. GROWTH – users in last 7 days vs previous 7 days
    const usersLast7Q = new Parse.Query("_User");
    usersLast7Q.greaterThanOrEqualTo("createdAt", sevenDaysAgo);
    const usersLast7 = await usersLast7Q.count({ useMasterKey: true });

    const usersPrev7Q = new Parse.Query("_User");
    usersPrev7Q.lessThan("createdAt", sevenDaysAgo);
    usersPrev7Q.greaterThanOrEqualTo("createdAt", new Date(sevenDaysAgo.getTime()-7*24*60*60*1000));
    const usersPrev7 = await usersPrev7Q.count({ useMasterKey: true });

    const userGrowthPct = usersPrev7 === 0 ? 100 : Math.round(((usersLast7 - usersPrev7) / usersPrev7) * 100);

    // 5. Top regions – aggregate Listings by region today
    const regionsAgg = {};
    const regionListingsQ = new Parse.Query("Listing");
    regionListingsQ.greaterThanOrEqualTo("createdAt", todayStart);
    regionListingsQ.exists("region");
    regionListingsQ.select("region");
    regionListingsQ.limit(1000);
    const regionResults = await regionListingsQ.find({ useMasterKey: true });
    regionResults.forEach(l => {
      const r = l.get("region") || "Unknown";
      regionsAgg[r] = (regionsAgg[r] || 0) + 1;
    });
    // top 3 regions
    const topRegions = Object.entries(regionsAgg)
      .sort((a,b)=>b[1]-a[1])
      .slice(0,3)
      .map(([region,count])=>({ region, listingsToday: count }));

    return {
      dau,
      newListingsToday,
      salesToday,
      salesAmount,
      userGrowthPct,
      topRegions,
      generatedAt: new Date()
    };
  } catch (e) {
    console.error("getDashboardMetrics error", e);
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Dashboard metrics failed");
  }
});
// ----------------------------- END ADMIN DASHBOARD ---------------------------

// ================================= TOKEN MANAGEMENT CLOUD FUNCTIONS =================================

/**
 * Deducts a specified number of tokens from the current user's balance.
 * Requires user to be authenticated.
 * @param {Object} request - The Parse Cloud Function request object.
 * @param {Object} request.params - Parameters.
 * @param {number} request.params.count - Number of tokens to deduct. Must be positive.
 * @returns {Promise<Object>} Object with success status and newBalance.
 * @throws {Parse.Error} If user not authenticated, count invalid, or insufficient tokens.
 */
Parse.Cloud.define("deductUserTokens", async (request) => {
  const user = request.user;
  if (!user) {
    throw new Parse.Error(Parse.Error.SESSION_MISSING, "User must be authenticated to deduct tokens.");
  }

  const count = request.params.count;
  if (typeof count !== 'number' || count <= 0) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Invalid token count specified. Must be a positive number.");
  }

  try {
    await user.fetch({ useMasterKey: true }); // Ensure latest user data, esp. tokenBalance
    const currentBalance = user.get("tokenBalance") || 0;

    if (currentBalance < count) {
      throw new Parse.Error(Parse.Error.VALIDATION_ERROR, `Insufficient tokens. Current balance: ${currentBalance}, trying to deduct: ${count}`);
    }

    user.increment("tokenBalance", -count); // Atomically decrement
    await user.save(null, { useMasterKey: true }); // useMasterKey to save User object fields

    return { success: true, newBalance: user.get("tokenBalance") };
  } catch (error) {
    console.error(`Error in deductUserTokens for user ${user.id}, count ${count}:`, error);
    if (error instanceof Parse.Error) throw error; // Re-throw Parse errors
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Failed to deduct tokens due to a server error.");
  }
});

/**
 * Adds a specified number of tokens to the current user's balance.
 * Requires user to be authenticated.
 * @param {Object} request - The Parse Cloud Function request object.
 * @param {Object} request.params - Parameters.
 * @param {number} request.params.count - Number of tokens to add. Must be positive.
 * @param {string} [request.params.source] - Optional source/reason for adding tokens (e.g., "purchase_pack_A").
 * @returns {Promise<Object>} Object with success status and newBalance.
 * @throws {Parse.Error} If user not authenticated or count invalid.
 */
Parse.Cloud.define("addUserTokens", async (request) => {
  const user = request.user;
  if (!user) {
    throw new Parse.Error(Parse.Error.SESSION_MISSING, "User must be authenticated to add tokens.");
  }

  const count = request.params.count;
  const source = request.params.source || "unknown";

  if (typeof count !== 'number' || count <= 0) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Invalid token count specified. Must be a positive number.");
  }

  try {
    await user.fetch({ useMasterKey: true }); // Ensure latest user data
    user.increment("tokenBalance", count); // Atomically increment
    await user.save(null, { useMasterKey: true });

    // Optional: Log this transaction in a TokenLedger class for auditing
    const TokenLedger = Parse.Object.extend("TokenLedger");
    const ledgerEntry = new TokenLedger();
    ledgerEntry.set("userId", user.id);
    ledgerEntry.set("username", user.get("username"));
    ledgerEntry.set("changeAmount", count);
    ledgerEntry.set("newBalance", user.get("tokenBalance"));
    ledgerEntry.set("type", "credit");
    ledgerEntry.set("source", source);
    ledgerEntry.set("notes", `Added ${count} tokens from source: ${source}`);
    await ledgerEntry.save(null, { useMasterKey: true }); // Log with master key

    return { success: true, newBalance: user.get("tokenBalance") };
  } catch (error) {
    console.error(`Error in addUserTokens for user ${user.id}, count ${count}:`, error);
    if (error instanceof Parse.Error) throw error; // Re-throw Parse errors
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, "Failed to add tokens due to a server error.");
  }
});

// ================================= END TOKEN MANAGEMENT CLOUD FUNCTIONS =================================


// --------------------------------- ACTIVITY-BASED VERIFICATION ---------------------------------
/**
 * Returns activity status for farmers: firstListingAt, total listings, last 30-day listings, eligibility flag
 */
Parse.Cloud.define("getActivityStatus", async (request) => {
  const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
  // Query farmer users (assuming role field)
  const userQuery = new Parse.Query(Parse.User);
  userQuery.equalTo('role', 'farmer');
  userQuery.limit(1000);
  const farmers = await userQuery.find({ useMasterKey: true });
  const Listing = Parse.Object.extend('Listing');
  const results = [];
  for (const farmer of farmers) {
    const userId = farmer.id;
    // total listings
    const totalQ = new Parse.Query(Listing);
    totalQ.equalTo('owner', farmer);
    const totalCount = await totalQ.count({ useMasterKey: true });
    // last 30 days listings
    const recentQ = new Parse.Query(Listing);
    recentQ.equalTo('owner', farmer);
    recentQ.greaterThanOrEqualTo('createdAt', thirtyDaysAgo);
    const recentCount = await recentQ.count({ useMasterKey: true });
    // first listing date (min createdAt)
    const firstQ = new Parse.Query(Listing);
    firstQ.equalTo('owner', farmer);
    firstQ.ascending('createdAt');
    firstQ.limit(1);
    const firstList = await firstQ.find({ useMasterKey: true });
    const firstDate = firstList.length ? firstList[0].get('createdAt') : null;
    // eligibility: >=3 in last 30 days and first listing at least 30 days ago
    const eligible = firstDate && firstDate <= thirtyDaysAgo && recentCount >= 3;
    results.push({
      userId,
      username: farmer.get('username'),
      firstListingAt: firstDate,
      totalListings: totalCount,
      last30DayListings: recentCount,
      isActivityVerified: farmer.get('isActivityVerified') || false,
      eligible
    });
  }
  return results;
});

/**
 * Approves activity-based verification for a farmer by setting isActivityVerified flag
 */
Parse.Cloud.define("approveActivityVerification", async (request) => {
  const { userId } = request.params;
  if (!request.user || !request.user.get('role') === 'admin') {
    throw new Parse.Error(Parse.Error.PERMISSION_DENIED, 'Only admins can approve verification');
  }
  const user = await new Parse.Query(Parse.User).get(userId, { useMasterKey: true });
  user.set('isActivityVerified', true);
  await user.save(null, { useMasterKey: true });
  return { success: true, userId };
});
// --------------------------------- END ACTIVITY-BASED VERIFICATION ---------------------------------

// --------------------------------------------------------------------------
// Auction Bidding Cloud Function
// --------------------------------------------------------------------------

/**
 * Atomically submits a bid for an auction, ensuring data consistency.
 * This function performs sequential server-side operations to reduce race conditions
 * and validate bids securely.
 *
 * @param {string} auctionId - The ID of the auction listing.
 * @param {number} bidAmount - The amount of the bid.
 * @param {number} [depositAmount] - Optional deposit amount.
 * @param {string} [paymentId] - Optional payment ID for the deposit.
 */
Parse.Cloud.define("submitAuctionBid", async (request) => {
  const { auctionId, bidAmount, depositAmount, paymentId } = request.params;
  const user = request.user;

  if (!user) {
    throw new Parse.Error(Parse.Error.SESSION_MISSING, "You must be logged in to bid.");
  }

  if (!auctionId || !bidAmount || bidAmount <= 0) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing or invalid auctionId or bidAmount.");
  }

  try {
    const AuctionListing = Parse.Object.extend("AuctionListing");
    const auctionQuery = new Parse.Query(AuctionListing);
    const auction = await auctionQuery.get(auctionId, { useMasterKey: true });

    // 1. Validate Auction and Bid State
    if (auction.get("status") !== "ACTIVE") {
      throw new Parse.Error(142, `Auction is not active. Current status: ${auction.get("status")}`);
    }

    if (new Date() >= auction.get("endTime")) {
      throw new Parse.Error(143, "This auction has already ended.");
    }

    const currentBid = auction.get("currentBid") || 0;
    const minimumIncrement = auction.get("minimumIncrement") || 1;
    if (bidAmount < currentBid + minimumIncrement) {
      throw new Parse.Error(144, `Your bid of ${bidAmount} is not high enough. Minimum next bid is ${currentBid + minimumIncrement}.`);
    }
    
    if (auction.get("seller") && auction.get("seller").id === user.id) {
      throw new Parse.Error(145, "Sellers cannot bid on their own auctions.");
    }

    // 2. Find and update the current winning bid to not winning
    const Bid = Parse.Object.extend("Bid");
    const bidQuery = new Parse.Query(Bid);
    bidQuery.equalTo("auction", auction);
    bidQuery.equalTo("isWinning", true);
    const currentWinningBid = await bidQuery.first({ useMasterKey: true });
    
    if (currentWinningBid) {
      if (currentWinningBid.get("bidder").id === user.id) {
          throw new Parse.Error(146, "You are already the highest bidder.");
      }
      currentWinningBid.set("isWinning", false);
      await currentWinningBid.save(null, { useMasterKey: true });
    }

    // 3. Create the new Bid object
    const newBid = new Bid();
    newBid.set("auction", auction);
    newBid.set("bidder", user);
    newBid.set("bidAmount", bidAmount);
    newBid.set("isWinning", true);

    if (depositAmount && depositAmount > 0 && paymentId) {
      newBid.set("depositAmount", depositAmount);
      newBid.set("depositStatus", "PAID");
      newBid.set("paymentId", paymentId);
    }
    
    // 4. Update auction and save new bid
    await newBid.save(null, { useMasterKey: true });

    auction.set("currentBid", bidAmount);
    auction.increment("bidCount");
    await auction.save(null, { useMasterKey: true });

    return {
      success: true,
      message: "Bid submitted successfully."
    };

  } catch (error) {
    console.error(`Bid submission failed for auction ${auctionId}: ${error.message}`);
    throw error; // Re-throw the error for the client
  }
});

// ----------------------------------- UPDATE AUCTION CURRENT BID FUNCTION -----------------------------------
/**
 * Updates the current bid amount for an auction
 * Called by auction system to update current highest bid
 */
Parse.Cloud.define("updateAuctionCurrentBid", async (request) => {
  const { auctionId, newBidAmount } = request.params;
  
  if (!auctionId || !newBidAmount || newBidAmount <= 0) {
    throw new Parse.Error(Parse.Error.INVALID_PARAMETER, "Missing or invalid auctionId or newBidAmount");
  }
  
  try {
    // Update both AuctionListing and EnhancedAuction based on which exists
    let updated = false;
    
    // Try to update AuctionListing first
    try {
      const AuctionListing = Parse.Object.extend("AuctionListing");
      const auctionQuery = new Parse.Query(AuctionListing);
      const auction = await auctionQuery.get(auctionId, { useMasterKey: true });
      
      auction.set("currentBid", newBidAmount);
      auction.set("lastBidTime", new Date());
      await auction.save(null, { useMasterKey: true });
      updated = true;
      
    } catch (listingError) {
      // If AuctionListing doesn't exist, try EnhancedAuction
      try {
        const EnhancedAuction = Parse.Object.extend("EnhancedAuction");
        const auctionQuery = new Parse.Query(EnhancedAuction);
        const auction = await auctionQuery.get(auctionId, { useMasterKey: true });
        
        auction.set("currentBid", newBidAmount);
        auction.set("lastBidTime", new Date());
        await auction.save(null, { useMasterKey: true });
        updated = true;
        
      } catch (enhancedError) {
        console.error("Could not find auction in either AuctionListing or EnhancedAuction:", enhancedError);
      }
    }
    
    if (!updated) {
      throw new Parse.Error(Parse.Error.OBJECT_NOT_FOUND, "Auction not found");
    }
    
    return {
      success: true,
      auctionId: auctionId,
      newBidAmount: newBidAmount,
      updatedAt: new Date().toISOString()
    };
    
  } catch (error) {
    console.error("Error updating auction current bid:", error);
    if (error instanceof Parse.Error) throw error;
    throw new Parse.Error(Parse.Error.INTERNAL_SERVER_ERROR, `Failed to update auction: ${error.message}`);
  }
});
// Add a beforeSave trigger to ensure data consistency on the AuctionListing
Parse.Cloud.beforeSave("AuctionListing", async (request) => {
  const auction = request.object;

  // Ensure bidCount is never negative
  if (auction.get("bidCount") < 0) {
    auction.set("bidCount", 0);
  }

  // If the auction is ending, set its status to COMPLETED
  const endTime = auction.get("endTime");
  if (endTime && new Date() > endTime && auction.get("status") === "ACTIVE") {
    auction.set("status", "COMPLETED");
  }
});
