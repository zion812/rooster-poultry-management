// Live Streaming Parse Cloud Functions
// Add these to your existing cloud/main.js file

Parse.Cloud.define("startBroadcast", async (request) => {
  const { birdId, broadcasterName, birdType, category } = request.params;
  
  try {
    // Validate user has enough coins for broadcast
    const user = request.user;
    if (!user) {
      throw new Parse.Error(401, "Authentication required");
    }
    
    const currentCoins = user.get("coins") || 0;
    const broadcastCost = category === "premium" ? 10 : 3; // Premium HD vs Standard
    
    if (currentCoins < broadcastCost) {
      throw new Parse.Error(400, "Insufficient coins for broadcast");
    }
    
    // Deduct broadcast cost
    user.set("coins", currentCoins - broadcastCost);
    await user.save(null, { useMasterKey: true });
    
    // Create broadcast session
    const BroadcastSession = Parse.Object.extend("BroadcastSession");
    const session = new BroadcastSession();
    
    session.set("birdId", birdId);
    session.set("broadcasterUserId", user.id);
    session.set("broadcasterName", broadcasterName);
    session.set("birdType", birdType);
    session.set("category", category);
    session.set("viewerCount", 0);
    session.set("totalGifts", 0);
    session.set("giftRevenue", 0);
    session.set("isActive", true);
    session.set("startTime", new Date());
    session.set("viewers", []);
    session.set("recentGifts", []);
    
    await session.save();
    
    // Log transaction
    const CoinTransaction = Parse.Object.extend("CoinTransaction");
    const transaction = new CoinTransaction();
    transaction.set("userId", user.id);
    transaction.set("type", "DEBIT");
    transaction.set("amount", broadcastCost);
    transaction.set("reason", `Live Broadcast: ${category}`);
    transaction.set("balanceAfter", currentCoins - broadcastCost);
    transaction.set("timestamp", new Date());
    await transaction.save();
    
    return { 
      sessionId: session.id, 
      status: "started",
      remainingCoins: currentCoins - broadcastCost,
      broadcastCost: broadcastCost
    };
  } catch (error) {
    throw new Parse.Error(500, `Broadcast start failed: ${error.message}`);
  }
});

Parse.Cloud.define("joinBroadcast", async (request) => {
  const { sessionId } = request.params;
  const user = request.user;
  
  try {
    if (!user) {
      throw new Parse.Error(401, "Authentication required");
    }
    
    const session = await new Parse.Query("BroadcastSession").get(sessionId);
    if (!session.get("isActive")) {
      throw new Parse.Error(400, "Broadcast is not active");
    }
    
    const viewers = session.get("viewers") || [];
    const userId = user.id;
    
    if (!viewers.includes(userId)) {
      viewers.push(userId);
      session.set("viewers", viewers);
      session.set("viewerCount", viewers.length);
      await session.save();
    }
    
    return {
      success: true,
      viewerCount: viewers.length,
      broadcasterName: session.get("broadcasterName"),
      birdType: session.get("birdType"),
      startTime: session.get("startTime")
    };
  } catch (error) {
    throw new Parse.Error(500, `Join broadcast failed: ${error.message}`);
  }
});

Parse.Cloud.define("sendGift", async (request) => {
  const { sessionId, giftType, message } = request.params;
  const user = request.user;
  
  try {
    if (!user) {
      throw new Parse.Error(401, "Authentication required");
    }
    
    // Gift pricing
    const GIFT_COSTS = {
      "🌹": 1,  // Rose - ₹5
      "🎀": 2,  // Bow - ₹10  
      "🏆": 5,  // Trophy - ₹25
      "💎": 10, // Diamond - ₹50
      "🐓": 15, // Special Rooster - ₹75
      "👑": 25  // Crown - ₹125
    };
    
    const cost = GIFT_COSTS[giftType] || 1;
    const currentCoins = user.get("coins") || 0;
    
    if (currentCoins < cost) {
      throw new Parse.Error(400, "Insufficient coins");
    }
    
    // Deduct coins from sender
    user.set("coins", currentCoins - cost);
    await user.save(null, { useMasterKey: true });
    
    // Update broadcast session
    const session = await new Parse.Query("BroadcastSession").get(sessionId);
    const totalGifts = session.get("totalGifts") || 0;
    const giftRevenue = session.get("giftRevenue") || 0;
    const recentGifts = session.get("recentGifts") || [];
    
    // Add new gift to recent gifts (keep last 10)
    const newGift = {
      type: giftType,
      senderId: user.id,
      senderName: user.get("username") || "Anonymous",
      message: message || "",
      cost: cost,
      timestamp: new Date()
    };
    
    recentGifts.unshift(newGift);
    if (recentGifts.length > 10) {
      recentGifts.pop();
    }
    
    session.set("totalGifts", totalGifts + 1);
    session.set("giftRevenue", giftRevenue + cost);
    session.set("recentGifts", recentGifts);
    await session.save();
    
    // Add coins to broadcaster (70% split, 30% to platform)
    const broadcasterShare = Math.floor(cost * 0.7);
    const broadcasterQuery = new Parse.Query(Parse.User);
    const broadcaster = await broadcasterQuery.get(session.get("broadcasterUserId"), { useMasterKey: true });
    const broadcasterCoins = broadcaster.get("coins") || 0;
    broadcaster.set("coins", broadcasterCoins + broadcasterShare);
    await broadcaster.save(null, { useMasterKey: true });
    
    // Log transactions
    const senderTransaction = new (Parse.Object.extend("CoinTransaction"))();
    senderTransaction.set("userId", user.id);
    senderTransaction.set("type", "DEBIT");
    senderTransaction.set("amount", cost);
    senderTransaction.set("reason", `Gift: ${giftType} to ${session.get("broadcasterName")}`);
    senderTransaction.set("balanceAfter", currentCoins - cost);
    senderTransaction.set("timestamp", new Date());
    await senderTransaction.save();
    
    const broadcasterTransaction = new (Parse.Object.extend("CoinTransaction"))();
    broadcasterTransaction.set("userId", session.get("broadcasterUserId"));
    broadcasterTransaction.set("type", "CREDIT");
    broadcasterTransaction.set("amount", broadcasterShare);
    broadcasterTransaction.set("reason", `Gift received: ${giftType} from ${user.get("username") || "Anonymous"}`);
    broadcasterTransaction.set("balanceAfter", broadcasterCoins + broadcasterShare);
    broadcasterTransaction.set("timestamp", new Date());
    await broadcasterTransaction.save();
    
    return { 
      success: true, 
      remainingCoins: currentCoins - cost,
      broadcasterEarned: broadcasterShare,
      totalGifts: totalGifts + 1
    };
  } catch (error) {
    throw new Parse.Error(500, `Gift send failed: ${error.message}`);
  }
});

Parse.Cloud.define("stopBroadcast", async (request) => {
  const { sessionId } = request.params;
  const user = request.user;
  
  try {
    if (!user) {
      throw new Parse.Error(401, "Authentication required");
    }
    
    const session = await new Parse.Query("BroadcastSession").get(sessionId);
    
    // Verify user owns this broadcast
    if (session.get("broadcasterUserId") !== user.id) {
      throw new Parse.Error(403, "Not authorized to stop this broadcast");
    }
    
    const endTime = new Date();
    const startTime = session.get("startTime");
    const duration = Math.floor((endTime.getTime() - startTime.getTime()) / 1000); // seconds
    
    session.set("isActive", false);
    session.set("endTime", endTime);
    session.set("duration", duration);
    await session.save();
    
    // Calculate broadcast statistics
    const totalGifts = session.get("totalGifts") || 0;
    const giftRevenue = session.get("giftRevenue") || 0;
    const viewerCount = session.get("viewerCount") || 0;
    
    return {
      success: true,
      statistics: {
        duration: duration,
        totalViewers: viewerCount,
        totalGifts: totalGifts,
        totalRevenue: giftRevenue,
        avgViewersPerMinute: Math.floor(viewerCount / Math.max(1, duration / 60))
      }
    };
  } catch (error) {
    throw new Parse.Error(500, `Stop broadcast failed: ${error.message}`);
  }
});

Parse.Cloud.define("getActiveBroadcasts", async (request) => {
  const { region, category, limit } = request.params;
  
  try {
    const query = new Parse.Query("BroadcastSession");
    query.equalTo("isActive", true);
    
    if (region) {
      // Assuming broadcaster has region info
      query.equalTo("region", region);
    }
    
    if (category) {
      query.equalTo("category", category);
    }
    
    query.descending("viewerCount");
    query.limit(limit || 20);
    query.include("broadcasterUserId");
    
    const results = await query.find();
    
    const broadcasts = results.map(session => ({
      sessionId: session.id,
      birdId: session.get("birdId"),
      broadcasterName: session.get("broadcasterName"),
      birdType: session.get("birdType"),
      category: session.get("category"),
      viewerCount: session.get("viewerCount"),
      totalGifts: session.get("totalGifts"),
      startTime: session.get("startTime"),
      duration: Math.floor((new Date().getTime() - session.get("startTime").getTime()) / 1000)
    }));
    
    return { broadcasts };
  } catch (error) {
    throw new Parse.Error(500, `Get broadcasts failed: ${error.message}`);
  }
});

Parse.Cloud.define("getBroadcastStats", async (request) => {
  const { userId, period } = request.params; // period: 'week', 'month', 'all'
  const user = request.user;
  
  try {
    if (!user) {
      throw new Parse.Error(401, "Authentication required");
    }
    
    const targetUserId = userId || user.id;
    
    // Get broadcast sessions
    const query = new Parse.Query("BroadcastSession");
    query.equalTo("broadcasterUserId", targetUserId);
    
    if (period === 'week') {
      const weekAgo = new Date();
      weekAgo.setDate(weekAgo.getDate() - 7);
      query.greaterThan("startTime", weekAgo);
    } else if (period === 'month') {
      const monthAgo = new Date();
      monthAgo.setMonth(monthAgo.getMonth() - 1);
      query.greaterThan("startTime", monthAgo);
    }
    
    query.descending("startTime");
    const sessions = await query.find();
    
    const stats = {
      totalBroadcasts: sessions.length,
      totalViewers: sessions.reduce((sum, s) => sum + (s.get("viewerCount") || 0), 0),
      totalGifts: sessions.reduce((sum, s) => sum + (s.get("totalGifts") || 0), 0),
      totalRevenue: sessions.reduce((sum, s) => sum + (s.get("giftRevenue") || 0), 0),
      totalDuration: sessions.reduce((sum, s) => sum + (s.get("duration") || 0), 0),
      avgViewersPerBroadcast: 0,
      avgGiftsPerBroadcast: 0,
      avgRevenuePerBroadcast: 0
    };
    
    if (stats.totalBroadcasts > 0) {
      stats.avgViewersPerBroadcast = Math.floor(stats.totalViewers / stats.totalBroadcasts);
      stats.avgGiftsPerBroadcast = Math.floor(stats.totalGifts / stats.totalBroadcasts);
      stats.avgRevenuePerBroadcast = Math.floor(stats.totalRevenue / stats.totalBroadcasts);
    }
    
    return { stats };
  } catch (error) {
    throw new Parse.Error(500, `Get broadcast stats failed: ${error.message}`);
  }
});