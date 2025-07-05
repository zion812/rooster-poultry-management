const axios = require('axios');

/**
 * Parse Server service for social and e-commerce features
 * Optimized for rural networks with minimal data transfer
 */
class ParseService {
  constructor() {
    this.baseURL = process.env.PARSE_SERVER_URL || 'https://parseapi.back4app.com';
    this.appId = process.env.PARSE_APP_ID;
    this.apiKey = process.env.PARSE_REST_API_KEY;
    
    if (!this.appId || !this.apiKey) {
      throw new Error('Parse Server credentials not configured');
    }
  }

  /**
   * Get default headers for Parse API requests
   */
  getHeaders() {
    return {
      'X-Parse-Application-Id': this.appId,
      'X-Parse-REST-API-Key': this.apiKey,
      'Content-Type': 'application/json'
    };
  }

  /**
   * Create or update user profile
   * @param {Object} userData - User data including profile info
   * @returns {Promise<Object>} Updated user object
   */
  async createUserProfile(userData) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/classes/_User/${userData.objectId}`;
      
      const response = await axios.put(url, {
        ...userData,
        ACL: {
          '*': {
            read: true,
            write: false
          }
        }
      }, { headers });
      
      return response.data;
    } catch (error) {
      throw new Error(`Failed to update user profile: ${error.message}`);
    }
  }

  /**
   * Create a new product listing
   * @param {Object} productData - Product details
   * @returns {Promise<Object>} Created product object
   */
  async createProduct(productData) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/classes/Product`;
      
      const response = await axios.post(url, {
        ...productData,
        ACL: {
          '*': {
            read: true,
            write: false
          }
        }
      }, { headers });
      
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create product: ${error.message}`);
    }
  }

  /**
   * Create a new post
   * @param {Object} postData - Post details
   * @returns {Promise<Object>} Created post object
   */
  async createPost(postData) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/classes/Post`;
      
      const response = await axios.post(url, {
        ...postData,
        ACL: {
          '*': {
            read: true,
            write: false
          }
        }
      }, { headers });
      
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create post: ${error.message}`);
    }
  }

  /**
   * Create a new story
   * @param {Object} storyData - Story details
   * @returns {Promise<Object>} Created story object
   */
  async createStory(storyData) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/classes/Story`;
      
      const response = await axios.post(url, {
        ...storyData,
        ACL: {
          '*': {
            read: true,
            write: false
          }
        }
      }, { headers });
      
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create story: ${error.message}`);
    }
  }

  /**
   * Get user's feed (posts and stories)
   * @param {string} userId - User ID to fetch feed for
   * @returns {Promise<Object>} Feed data
   */
  async getUserFeed(userId) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/functions/getUserFeed`;
      
      const response = await axios.post(url, {
        userId
      }, { headers });
      
      return response.data.result;
    } catch (error) {
      throw new Error(`Failed to fetch user feed: ${error.message}`);
    }
  }

  /**
   * Get product recommendations
   * @param {string} userId - User ID to get recommendations for
   * @returns {Promise<Array>} Recommended products
   */
  async getProductRecommendations(userId) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/functions/getProductRecommendations`;
      
      const response = await axios.post(url, {
        userId
      }, { headers });
      
      return response.data.result;
    } catch (error) {
      throw new Error(`Failed to fetch product recommendations: ${error.message}`);
    }
  }

  /**
   * Get nearby farmers and products
   * @param {Object} location - User's location
   * @param {number} radius - Search radius in kilometers
   * @returns {Promise<Object>} Nearby data
   */
  async getNearbyData(location, radius = 50) {
    try {
      const headers = this.getHeaders();
      const url = `${this.baseURL}/functions/getNearbyData`;
      
      const response = await axios.post(url, {
        location,
        radius
      }, { headers });
      
      return response.data.result;
    } catch (error) {
      throw new Error(`Failed to fetch nearby data: ${error.message}`);
    }
  }

  /**
   * Get historical poultry prices
   * @param {string} region - Region to fetch prices for
   * @param {string} fowlType - Type of fowl (optional)
   * @param {number} days - Number of days to look back (default 30)
   * @returns {Promise<Array>} Historical price data
   */
  async getHistoricalPrices(region, fowlType = null, days = 30) {
    try {
      const cutoffDate = new Date();
      cutoffDate.setDate(cutoffDate.getDate() - days);
      
      // Build query constraints
      const whereClause = {
        region: region,
        createdAt: {
          $gte: {
            __type: "Date",
            iso: cutoffDate.toISOString()
          }
        }
      };
      
      if (fowlType) {
        whereClause.fowlType = fowlType;
      };
      
      // Add fowl type filter if specified
      if (fowlType) {
        whereClause.fowlType = fowlType;
      }
      
      const response = await axios.get(`${this.baseURL}/classes/PoultryPrices`, {
        headers: this.getHeaders(),
        params: {
          where: JSON.stringify(whereClause),
          order: '-createdAt',
          limit: 100, // Limit for rural network optimization
          keys: 'price,fowlType,region,marketDate,createdAt' // Only fetch necessary fields
        }
      });
      
      return response.data.results || [];
    } catch (error) {
      console.error('Error fetching historical prices:', error.message);
      throw new Error(`Failed to fetch price data: ${error.message}`);
    }
  }

  /**
   * Get average market prices by region
   * @param {string} region - Region to query
   * @param {number} days - Days to look back
   * @returns {Promise<Object>} Average price data
   */
  async getAveragePrices(region, days = 7) {
    try {
      const historicalData = await this.getHistoricalPrices(region, null, days);
      
      if (!historicalData.length) {
        return null;
      }
      
      // Group by fowl type and calculate averages
      const pricesByType = {};
      let totalPrices = [];
      
      historicalData.forEach(record => {
        const fowlType = record.fowlType || 'general';
        const price = record.price;
        
        if (price && price > 0) {
          if (!pricesByType[fowlType]) {
            pricesByType[fowlType] = [];
          }
          pricesByType[fowlType].push(price);
          totalPrices.push(price);
        }
      });
      
      // Calculate averages
      const averages = {};
      Object.keys(pricesByType).forEach(type => {
        const prices = pricesByType[type];
        averages[type] = {
          average: prices.reduce((sum, price) => sum + price, 0) / prices.length,
          min: Math.min(...prices),
          max: Math.max(...prices),
          count: prices.length
        };
      });
      
      // Overall average
      const overallAverage = totalPrices.length > 0 
        ? totalPrices.reduce((sum, price) => sum + price, 0) / totalPrices.length 
        : 0;
      
      return {
        region,
        overall: {
          average: overallAverage,
          min: Math.min(...totalPrices),
          max: Math.max(...totalPrices),
          count: totalPrices.length
        },
        byType: averages,
        lastUpdated: new Date().toISOString()
      };
    } catch (error) {
      console.error('Error calculating average prices:', error.message);
      throw error;
    }
  }

  /**
   * Get market trend for a region
   * @param {string} region - Region to analyze
   * @param {string} fowlType - Type of fowl (optional)
   * @returns {Promise<Object>} Trend analysis
   */
  async getMarketTrend(region, fowlType = null) {
    try {
      // Get prices from last 7 days and previous 7 days
      const recent = await this.getHistoricalPrices(region, fowlType, 7);
      const previous = await this.getHistoricalPrices(region, fowlType, 14);
      
      if (recent.length === 0 || previous.length === 0) {
        return { trend: 'stable', change: 0, confidence: 'low' };
      }
      
      // Calculate averages
      const recentPrices = recent.map(r => r.price).filter(p => p > 0);
      const previousPrices = previous.map(r => r.price).filter(p => p > 0);
      
      const recentAvg = recentPrices.reduce((sum, price) => sum + price, 0) / recentPrices.length;
      const previousAvg = previousPrices.reduce((sum, price) => sum + price, 0) / previousPrices.length;
      
      const changePercent = ((recentAvg - previousAvg) / previousAvg) * 100;
      
      let trend = 'stable';
      if (changePercent > 5) trend = 'up';
      else if (changePercent < -5) trend = 'down';
      
      // Confidence based on data points
      const totalDataPoints = recentPrices.length + previousPrices.length;
      let confidence = 'low';
      if (totalDataPoints >= 10) confidence = 'high';
      else if (totalDataPoints >= 5) confidence = 'medium';
      
      return {
        trend,
        change: Math.round(changePercent * 100) / 100,
        recentAverage: Math.round(recentAvg * 100) / 100,
        previousAverage: Math.round(previousAvg * 100) / 100,
        confidence,
        dataPoints: totalDataPoints
      };
    } catch (error) {
      console.error('Error analyzing market trend:', error.message);
      return { trend: 'stable', change: 0, confidence: 'low' };
    }
  }

  /**
   * Save price prediction for future reference
   * @param {Object} predictionData - Prediction data to save
   * @returns {Promise<Object>} Saved prediction
   */
  async savePrediction(predictionData) {
    try {
      const response = await axios.post(`${this.baseURL}/classes/PricePredictions`, 
        {
          ...predictionData,
          createdAt: new Date().toISOString()
        },
        {
          headers: this.getHeaders()
        }
      );
      
      return response.data;
    } catch (error) {
      console.error('Error saving prediction:', error.message);
      // Don't throw error for logging failures
      return null;
    }
  }

  /**
   * Get available regions from the database
   * @returns {Promise<Array>} List of regions
   */
  async getAvailableRegions() {
    try {
      const response = await axios.get(`${this.baseURL}/classes/PoultryPrices`, {
        headers: this.getHeaders(),
        params: {
          keys: 'region',
          limit: 1000
        }
      });
      
      // Extract unique regions
      const regions = [...new Set(
        response.data.results
          .map(record => record.region)
          .filter(region => region && region.trim())
      )];
      
      return regions.sort();
    } catch (error) {
      console.error('Error fetching regions:', error.message);
      // Return default regions if API fails
      return ['Andhra Pradesh', 'Telangana', 'Karnataka', 'Tamil Nadu'];
    }
  }

  /**
   * Get available fowl types
   * @returns {Promise<Array>} List of fowl types
   */
  async getAvailableFowlTypes() {
    try {
      const response = await axios.get(`${this.baseURL}/classes/PoultryPrices`, {
        headers: this.getHeaders(),
        params: {
          keys: 'fowlType',
          limit: 1000
        }
      });
      
      // Extract unique fowl types
      const fowlTypes = [...new Set(
        response.data.results
          .map(record => record.fowlType)
          .filter(type => type && type.trim())
      )];
      
      return fowlTypes.sort();
    } catch (error) {
      console.error('Error fetching fowl types:', error.message);
      // Return default types if API fails
      return ['Broiler', 'Layer', 'Desi', 'Cockerel'];
    }
  }
}

module.exports = ParseService;