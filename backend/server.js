require('dotenv').config();
const express = require('express');
const compression = require('compression');
const helmet = require('helmet');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const morgan = require('morgan');
const Joi = require('joi');

// Import services and middleware
const { authenticateToken, optionalAuth } = require('./middleware/auth');
const { getMessage } = require('./config/translations');
const ParseService = require('./services/parseService');
const PricePredictor = require('./services/pricePredictor');

const app = express();
const port = process.env.PORT || 3000;

// Initialize services
const parseService = new ParseService();
const pricePredictor = new PricePredictor(parseService);

// Security and optimization middleware
app.use(helmet({
  contentSecurityPolicy: false, // Allow flexibility for rural app integration
  crossOriginResourcePolicy: { policy: "cross-origin" }
}));

app.use(cors({
  origin: process.env.NODE_ENV === 'production' 
    ? ['https://your-app-domain.com'] 
    : true,
  credentials: true
}));

// Compression for rural 2G networks
app.use(compression({
  level: 9, // Maximum compression for minimal bandwidth usage
  threshold: 1024, // Compress responses over 1KB
}));

// Rate limiting to prevent abuse
const limiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 15 * 60 * 1000, // 15 minutes
  max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100, // Limit each IP
  message: {
    success: false,
    message: 'Too many requests, please try again later.',
    code: 'RATE_LIMIT_EXCEEDED'
  },
  standardHeaders: true,
  legacyHeaders: false,
});

app.use('/api/', limiter);

// Logging
app.use(morgan('combined'));

// Body parsing
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Validation schemas
const predictPriceSchema = Joi.object({
  region: Joi.string().required().min(2).max(50),
  fowlType: Joi.string().optional().min(2).max(30),
  algorithm: Joi.string().valid('simple', 'weighted').default('weighted'),
  days: Joi.number().integer().min(7).max(90).default(30),
  lang: Joi.string().valid('en', 'te').default('en')
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'Price Prediction API is running',
    timestamp: new Date().toISOString(),
    version: '1.0.0'
  });
});

// Get available regions (public endpoint)
app.get('/api/regions', optionalAuth, async (req, res) => {
  try {
    const regions = await parseService.getAvailableRegions();
    const lang = req.lang || 'en';
    
    res.json({
      success: true,
      data: regions,
      message: getMessage(lang, 'success'),
      count: regions.length
    });
  } catch (error) {
    const lang = req.lang || 'en';
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'serverError'),
      code: 'REGIONS_FETCH_FAILED'
    });
  }
});

// Get available fowl types (public endpoint)
app.get('/api/fowl-types', optionalAuth, async (req, res) => {
  try {
    const fowlTypes = await parseService.getAvailableFowlTypes();
    const lang = req.lang || 'en';
    
    res.json({
      success: true,
      data: fowlTypes,
      message: getMessage(lang, 'success'),
      count: fowlTypes.length
    });
  } catch (error) {
    const lang = req.lang || 'en';
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'serverError'),
      code: 'FOWL_TYPES_FETCH_FAILED'
    });
  }
});

// Main price prediction endpoint
app.get('/api/predict-price', authenticateToken, async (req, res) => {
  try {
    // Validate request parameters
    const { error, value } = predictPriceSchema.validate(req.query);
    if (error) {
      return res.status(400).json({
        success: false,
        message: getMessage(req.lang, 'error', { message: error.details[0].message }),
        code: 'VALIDATION_ERROR'
      });
    }

    const { region, fowlType, algorithm, days, lang } = value;

    // Make prediction
    const prediction = await pricePredictor.predictPrice(region, fowlType, {
      algorithm,
      days,
      includeSeasonalAdjustment: true
    });

    // Format response for rural optimization
    const response = pricePredictor.formatResponse(prediction, lang);
    
    // Log prediction for analytics (async, don't wait)
    parseService.savePrediction({
      userId: req.user.uid,
      region,
      fowlType,
      predictedPrice: prediction.predictedPrice,
      confidence: prediction.confidence,
      algorithm
    }).catch(err => console.log('Prediction logging failed:', err.message));

    res.json(response);

  } catch (error) {
    console.error('Price prediction error:', error);
    const lang = req.lang || 'en';
    
    if (error.message.includes('No historical data') || error.message.includes('No valid price data')) {
      return res.status(404).json({
        success: false,
        message: getMessage(lang, 'noDataFound'),
        code: 'NO_DATA_AVAILABLE'
      });
    }
    
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'serverError'),
      code: 'PREDICTION_FAILED',
      ...(process.env.NODE_ENV === 'development' && { debug: error.message })
    });
  }
});

// Get market summary for a region
app.get('/api/market-summary', authenticateToken, async (req, res) => {
  try {
    const { region, days = 7 } = req.query;
    const lang = req.lang || 'en';
    
    if (!region) {
      return res.status(400).json({
        success: false,
        message: getMessage(lang, 'regionRequired'),
        code: 'REGION_REQUIRED'
      });
    }

    // Get average prices and trend
    const [averageData, trendData] = await Promise.all([
      parseService.getAveragePrices(region, parseInt(days)),
      parseService.getMarketTrend(region)
    ]);

    if (!averageData) {
      return res.status(404).json({
        success: false,
        message: getMessage(lang, 'noDataFound'),
        code: 'NO_DATA_AVAILABLE'
      });
    }

    res.json({
      success: true,
      data: {
        region,
        averagePrice: averageData.overall.average,
        priceRange: {
          min: averageData.overall.min,
          max: averageData.overall.max
        },
        trend: trendData,
        byFowlType: averageData.byType,
        dataPoints: averageData.overall.count,
        lastUpdated: averageData.lastUpdated
      },
      message: getMessage(lang, 'success')
    });

  } catch (error) {
    console.error('Market summary error:', error);
    const lang = req.lang || 'en';
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'serverError'),
      code: 'MARKET_SUMMARY_FAILED'
    });
  }
});

// Bulk price prediction for multiple regions
app.post('/api/predict-bulk', authenticateToken, async (req, res) => {
  try {
    const { regions, fowlType, algorithm = 'weighted', days = 30 } = req.body;
    const lang = req.lang || 'en';
    
    if (!regions || !Array.isArray(regions) || regions.length === 0) {
      return res.status(400).json({
        success: false,
        message: getMessage(lang, 'error', { message: 'Regions array is required' }),
        code: 'REGIONS_REQUIRED'
      });
    }

    // Limit bulk requests for rural network optimization
    if (regions.length > 5) {
      return res.status(400).json({
        success: false,
        message: getMessage(lang, 'error', { message: 'Maximum 5 regions allowed' }),
        code: 'TOO_MANY_REGIONS'
      });
    }

    // Process predictions in parallel
    const predictions = await Promise.allSettled(
      regions.map(async (region) => {
        try {
          const prediction = await pricePredictor.predictPrice(region, fowlType, {
            algorithm,
            days
          });
          return { region, ...prediction };
        } catch (error) {
          return { region, error: error.message };
        }
      })
    );

    const results = predictions.map(result => 
      result.status === 'fulfilled' ? result.value : result.reason
    );

    res.json({
      success: true,
      data: results,
      message: getMessage(lang, 'success'),
      count: results.length
    });

  } catch (error) {
    console.error('Bulk prediction error:', error);
    const lang = req.lang || 'en';
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'serverError'),
      code: 'BULK_PREDICTION_FAILED'
    });
  }
});

// API documentation endpoint
app.get('/api/docs', (req, res) => {
  const documentation = {
    title: 'Rooster Price Prediction API',
    version: '1.0.0',
    description: 'Price prediction API for poultry optimized for rural farmers',
    endpoints: {
      'GET /health': 'Health check endpoint',
      'GET /api/regions': 'Get available regions',
      'GET /api/fowl-types': 'Get available fowl types',
      'GET /api/predict-price': 'Get price prediction for a region',
      'GET /api/market-summary': 'Get market summary for a region',
      'POST /api/predict-bulk': 'Get predictions for multiple regions'
    },
    authentication: 'Firebase JWT token required for protected endpoints',
    languages: ['en (English)', 'te (Telugu)'],
    optimization: 'Optimized for 2G networks with compression and minimal payloads'
  };
  
  res.json(documentation);
});

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint not found',
    code: 'NOT_FOUND'
  });
});

// Global error handler
app.use((error, req, res, next) => {
  console.error('Global error:', error);
  const lang = req.lang || 'en';
  
  res.status(500).json({
    success: false,
    message: getMessage(lang, 'serverError'),
    code: 'INTERNAL_SERVER_ERROR',
    ...(process.env.NODE_ENV === 'development' && { debug: error.message })
  });
});

// Start server
app.listen(port, () => {
  console.log(`🐓 Rooster Price Prediction API running on port ${port}`);
  console.log(`📊 Environment: ${process.env.NODE_ENV || 'development'}`);
  console.log(`🌐 Health check: http://localhost:${port}/health`);
  console.log(`📚 Documentation: http://localhost:${port}/api/docs`);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down gracefully');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('SIGINT received, shutting down gracefully');
  process.exit(0);
});

module.exports = app;