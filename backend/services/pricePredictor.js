const { getMessage, formatPrice, getTrendDescription } = require('../config/translations');

/**
 * Price Prediction Service
 * Implements various algorithms for predicting poultry prices
 * Optimized for rural farming decisions
 */
class PricePredictor {
  constructor(parseService) {
    this.parseService = parseService;
    this.regionalFactors = {
      'Andhra Pradesh': 1.05,
      'Telangana': 1.03,
      'Karnataka': 1.02,
      'Tamil Nadu': 1.04,
      'default': 1.00
    };
  }

  /**
   * Get regional adjustment factor
   * @param {string} region - Region name
   * @returns {number} Regional factor
   */
  getRegionalFactor(region) {
    return this.regionalFactors[region] || this.regionalFactors.default;
  }

  /**
   * Simple moving average prediction
   * @param {Array} prices - Historical prices array
   * @param {number} window - Moving average window (default 7)
   * @returns {number} Predicted price
   */
  movingAveragePrediction(prices, window = 7) {
    if (!prices || prices.length === 0) return 0;
    
    const validPrices = prices.filter(p => p > 0);
    if (validPrices.length === 0) return 0;
    
    const windowSize = Math.min(window, validPrices.length);
    const recentPrices = validPrices.slice(-windowSize);
    
    return recentPrices.reduce((sum, price) => sum + price, 0) / recentPrices.length;
  }

  /**
   * Weighted moving average prediction (recent prices have more weight)
   * @param {Array} prices - Historical prices array
   * @param {number} window - Window size
   * @returns {number} Predicted price
   */
  weightedMovingAverage(prices, window = 7) {
    if (!prices || prices.length === 0) return 0;
    
    const validPrices = prices.filter(p => p > 0);
    if (validPrices.length === 0) return 0;
    
    const windowSize = Math.min(window, validPrices.length);
    const recentPrices = validPrices.slice(-windowSize);
    
    let weightedSum = 0;
    let totalWeight = 0;
    
    recentPrices.forEach((price, index) => {
      const weight = index + 1; // More weight for recent prices
      weightedSum += price * weight;
      totalWeight += weight;
    });
    
    return totalWeight > 0 ? weightedSum / totalWeight : 0;
  }

  /**
   * Seasonal adjustment based on historical patterns
   * @param {number} basePrice - Base predicted price
   * @param {Date} targetDate - Date for prediction
   * @returns {number} Seasonally adjusted price
   */
  applySeasonalAdjustment(basePrice, targetDate = new Date()) {
    const month = targetDate.getMonth() + 1; // 1-12
    
    // Seasonal factors based on typical poultry market patterns
    const seasonalFactors = {
      1: 1.08,  // January - High demand (winter/festivals)
      2: 1.05,  // February
      3: 1.02,  // March
      4: 1.00,  // April - Normal
      5: 0.98,  // May
      6: 0.95,  // June - Lower demand (summer)
      7: 0.95,  // July
      8: 0.98,  // August - Demand picks up
      9: 1.02,  // September
      10: 1.06, // October - Festival season begins
      11: 1.10, // November - Peak festival season
      12: 1.12  // December - Year-end demand
    };
    
    const factor = seasonalFactors[month] || 1.00;
    return basePrice * factor;
  }

  /**
   * Main prediction function
   * @param {string} region - Region for prediction
   * @param {string} fowlType - Type of fowl (optional)
   * @param {Object} options - Prediction options
   * @returns {Promise<Object>} Prediction result
   */
  async predictPrice(region, fowlType = null, options = {}) {
    try {
      const {
        algorithm = 'weighted', // 'simple', 'weighted'
        days = 30,
        includeSeasonalAdjustment = true,
        targetDate = new Date()
      } = options;

      // Fetch historical data
      const historicalData = await this.parseService.getHistoricalPrices(region, fowlType, days);
      
      if (!historicalData || historicalData.length === 0) {
        throw new Error('No historical data available for prediction');
      }

      // Extract prices
      const prices = historicalData.map(record => record.price).filter(p => p > 0);
      
      if (prices.length === 0) {
        throw new Error('No valid price data found');
      }

      // Calculate base prediction using selected algorithm
      let basePrice = 0;
      switch (algorithm) {
        case 'simple':
          basePrice = this.movingAveragePrediction(prices);
          break;
        case 'weighted':
        default:
          basePrice = this.weightedMovingAverage(prices);
          break;
      }

      // Apply regional factor
      const regionalFactor = this.getRegionalFactor(region);
      let predictedPrice = basePrice * regionalFactor;

      // Apply seasonal adjustment if requested
      if (includeSeasonalAdjustment) {
        predictedPrice = this.applySeasonalAdjustment(predictedPrice, targetDate);
      }

      // Get market trend
      const trendData = await this.parseService.getMarketTrend(region, fowlType);

      // Calculate confidence level
      const confidence = this.calculateConfidence(prices, trendData);

      // Generate recommendation
      const recommendation = this.generateRecommendation(predictedPrice, basePrice, trendData, confidence);

      return {
        predictedPrice: Math.round(predictedPrice * 100) / 100,
        basePrice: Math.round(basePrice * 100) / 100,
        region,
        fowlType,
        confidence: Math.round(confidence),
        trend: trendData,
        recommendation,
        factors: {
          regional: regionalFactor,
          seasonal: includeSeasonalAdjustment,
          dataPoints: prices.length,
          algorithm
        },
        priceRange: {
          min: Math.round(predictedPrice * 0.9 * 100) / 100,
          max: Math.round(predictedPrice * 1.1 * 100) / 100
        },
        lastUpdated: new Date().toISOString()
      };

    } catch (error) {
      console.error('Price prediction error:', error.message);
      throw error;
    }
  }

  /**
   * Calculate prediction confidence based on data quality
   * @param {Array} prices - Historical prices
   * @param {Object} trendData - Market trend data
   * @returns {number} Confidence percentage (0-100)
   */
  calculateConfidence(prices, trendData) {
    let confidence = 50; // Base confidence
    
    // Data quantity factor
    if (prices.length >= 20) confidence += 20;
    else if (prices.length >= 10) confidence += 10;
    else if (prices.length >= 5) confidence += 5;
    
    // Price stability factor (lower variance = higher confidence)
    const mean = prices.reduce((sum, price) => sum + price, 0) / prices.length;
    const variance = prices.reduce((sum, price) => sum + Math.pow(price - mean, 2), 0) / prices.length;
    const coefficient = Math.sqrt(variance) / mean;
    
    if (coefficient < 0.1) confidence += 15;
    else if (coefficient < 0.2) confidence += 10;
    else if (coefficient < 0.3) confidence += 5;
    
    // Trend confidence factor
    if (trendData.confidence === 'high') confidence += 10;
    else if (trendData.confidence === 'medium') confidence += 5;
    
    return Math.min(confidence, 95); // Cap at 95%
  }

  /**
   * Generate selling recommendation based on prediction
   * @param {number} predictedPrice - Predicted price
   * @param {number} currentPrice - Current/base price
   * @param {Object} trendData - Market trend data
   * @param {number} confidence - Prediction confidence
   * @returns {Object} Recommendation object
   */
  generateRecommendation(predictedPrice, currentPrice, trendData, confidence) {
    const priceDiff = predictedPrice - currentPrice;
    const percentChange = (priceDiff / currentPrice) * 100;
    
    let action = 'hold';
    let reason = 'stable market';
    
    if (confidence < 60) {
      action = 'wait';
      reason = 'insufficient data';
    } else if (trendData.trend === 'up' && percentChange > 3) {
      action = 'wait';
      reason = 'prices rising';
    } else if (trendData.trend === 'down' && percentChange < -3) {
      action = 'sell';
      reason = 'prices falling';
    } else if (percentChange > 5) {
      action = 'sell';
      reason = 'good price';
    }
    
    return {
      action,
      reason,
      confidence: confidence > 70 ? 'high' : confidence > 50 ? 'medium' : 'low',
      expectedChange: Math.round(percentChange * 100) / 100
    };
  }

  /**
   * Format prediction response for API
   * @param {Object} prediction - Prediction data
   * @param {string} lang - Language preference
   * @returns {Object} Formatted response
   */
  formatResponse(prediction, lang = 'en') {
    const formattedPrice = formatPrice(prediction.predictedPrice, lang);
    const trendDesc = getTrendDescription(prediction.trend.trend, lang);
    
    return {
      success: true,
      data: {
        prediction: getMessage(lang, 'predictedPrice', { price: formattedPrice }),
        price: prediction.predictedPrice,
        priceRange: getMessage(lang, 'priceRange', { 
          min: formatPrice(prediction.priceRange.min, lang),
          max: formatPrice(prediction.priceRange.max, lang)
        }),
        trend: getMessage(lang, 'marketTrend', { trend: trendDesc }),
        confidence: getMessage(lang, 'confidence', { level: prediction.confidence }),
        recommendation: getMessage(lang, 'recommendation', { 
          advice: prediction.recommendation.action 
        }),
        lastUpdated: getMessage(lang, 'lastUpdated', { 
          date: new Date(prediction.lastUpdated).toLocaleDateString() 
        })
      },
      metadata: {
        region: prediction.region,
        fowlType: prediction.fowlType,
        algorithm: prediction.factors.algorithm,
        dataPoints: prediction.factors.dataPoints,
        confidence: prediction.confidence
      }
    };
  }
}

module.exports = PricePredictor;