const axios = require('axios');

/**
 * Parse Server service for fetching historical price data
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
   * Fetch historical poultry prices from Parse Server
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