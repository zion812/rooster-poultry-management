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
const { initializeSchemas, initializeCloudFunctions } = require('./server/init');

const app = express();
const port = process.env.PORT || 3000;

// Initialize Parse and price predictor services
const parseService = new ParseService();
const pricePredictor = new PricePredictor();
const RazorpayService = require('./services/razorpayService'); // Import RazorpayService
const razorpayService = new RazorpayService(); // Instantiate RazorpayService

// Set up the relationship between services
pricePredictor.setParseService(parseService);

// Initialize server components
async function initializeServer() {
  try {
    console.log('🚀 Initializing Rooster API Server...');
    console.log('📊 Parse Service ready');
    console.log('🧮 Price Predictor ready');
    console.log('✅ Server initialization complete');
  } catch (error) {
    console.error('❌ Server initialization failed:', error.message);
  }
}

// Initialize on startup
initializeServer();

// Security and optimization middleware
app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"], // Only allow loading resources from the same origin by default
      scriptSrc: ["'self'"], // Add other trusted script sources if needed e.g. CDN for client-side libs
      styleSrc: ["'self'", "'unsafe-inline'"], // Allow inline styles if necessary, or specific sources
      imgSrc: ["'self'", "data:"], // Allow data URIs for images
      connectSrc: ["'self'"], // For XHR, WebSockets, etc. Add specific API endpoints if different.
      fontSrc: ["'self'"],
      objectSrc: ["'none'"], // Disallow <object>, <embed>, <applet>
      upgradeInsecureRequests: [], // Upgrade HTTP to HTTPS
    }
  },
  crossOriginResourcePolicy: { policy: "cross-origin" } // Keep this as is
}));

app.use(cors({
  origin: process.env.NODE_ENV === 'production' 
    ? ['https://your-app-domain.com'] // TODO: Replace with actual production domain
    : true, // TODO: For non-production, restrict to specific localhost ports (e.g., React/Vue dev server) or dev domains instead of `true` for better security.
  credentials: true
}));

// TODO: Implement structured logging (e.g., Winston or Pino) for better production monitoring.
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

// Validation schema for market summary
const marketSummarySchema = Joi.object({
  region: Joi.string().required().min(2).max(50),
  days: Joi.number().integer().min(1).max(90).default(7),
  lang: Joi.string().valid('en', 'te').default('en')
});

// Get market summary for a region
app.get('/api/market-summary', authenticateToken, async (req, res) => {
  try {
    const { error, value } = marketSummarySchema.validate(req.query);
    if (error) {
      return res.status(400).json({
        success: false,
        message: getMessage(req.query.lang || 'en', 'error', { message: error.details[0].message }),
        code: 'VALIDATION_ERROR'
      });
    }
    const { region, days, lang } = value;

    // Get average prices and trend
    const [averageData, trendData] = await Promise.all([
      parseService.getAveragePrices(region, days), // days is already an int
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

// Validation schema for bulk price prediction
const predictBulkSchema = Joi.object({
  regions: Joi.array().items(Joi.string().min(2).max(50)).min(1).max(5).required() // Max 5 regions
    .messages({
      'array.min': 'Regions array must contain at least 1 region.',
      'array.max': 'Maximum 5 regions allowed for bulk prediction.',
      'any.required': 'Regions array is required.'
    }),
  fowlType: Joi.string().optional().min(2).max(30),
  algorithm: Joi.string().valid('simple', 'weighted').default('weighted'),
  days: Joi.number().integer().min(7).max(90).default(30),
  lang: Joi.string().valid('en', 'te').default('en')
});

// Bulk price prediction for multiple regions
app.post('/api/predict-bulk', authenticateToken, async (req, res) => {
  try {
    const { error, value } = predictBulkSchema.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: getMessage(req.body.lang || 'en', 'error', { message: error.details[0].message }),
        code: 'VALIDATION_ERROR'
      });
    }
    const { regions, fowlType, algorithm, days, lang } = value;

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

// --- Payment Endpoints ---

// Create Razorpay Order
const createOrderSchema = Joi.object({
  amount: Joi.number().integer().min(100).required(), // Amount in paise (e.g., 50000 for ₹500)
  currency: Joi.string().default('INR'),
  receiptId: Joi.string().required(), // Unique receipt ID from client
  notes: Joi.object().optional()
});

app.post('/api/payments/orders', authenticateToken, async (req, res) => {
  const lang = req.lang || 'en';
  try {
    const { error, value } = createOrderSchema.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: getMessage(lang, 'error', { message: error.details[0].message }),
        code: 'VALIDATION_ERROR'
      });
    }

    const { amount, currency, receiptId, notes } = value;
    // Additional notes could include auctionId, userId from req.user.uid, etc.
    const orderNotes = {
      ...notes,
      userId: req.user.uid,
      email: req.user.email
    };

    const order = await razorpayService.createOrder(amount, currency, receiptId, orderNotes);
    res.json({ success: true, data: order, message: getMessage(lang, 'orderCreatedSuccess') });

  } catch (error) {
    console.error('Error creating Razorpay order:', error);
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'error', { message: 'Failed to create payment order.' }),
      code: 'ORDER_CREATION_FAILED',
      ...(process.env.NODE_ENV === 'development' && { debug: error.message })
    });
  }
});

// Verify Razorpay Payment (Client-driven)
const verifyPaymentSchema = Joi.object({
  razorpay_order_id: Joi.string().required(),
  razorpay_payment_id: Joi.string().required(),
  razorpay_signature: Joi.string().required(),
  // You might want to include auctionId or other context here from client
  auctionId: Joi.string().optional(),
});

app.post('/api/payments/verify', authenticateToken, async (req, res) => {
  const lang = req.lang || 'en';
  try {
    const { error, value } = verifyPaymentSchema.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: getMessage(lang, 'error', { message: error.details[0].message }),
        code: 'VALIDATION_ERROR'
      });
    }

    const { razorpay_order_id, razorpay_payment_id, razorpay_signature, auctionId } = value;

    const isValidSignature = razorpayService.verifyPaymentSignature(
      razorpay_order_id,
      razorpay_payment_id,
      razorpay_signature
    );

    if (isValidSignature) {
      // Signature is valid. Process the payment success.
      // 1. Update your database: Mark the order/auction deposit as paid.
      //    e.g., await parseService.updateAuctionPaymentStatus(auctionId, razorpay_payment_id, 'COMPLETED');
      // 2. Log the transaction.
      //    e.g., await parseService.logTransaction({ ...payment details..., userId: req.user.uid });

      // For now, just returning success
      console.log(`Payment verified for order ${razorpay_order_id} by user ${req.user.uid}. Auction: ${auctionId}`);
      // TODO: Implement actual database updates for payment success.

      res.json({
        success: true,
        message: getMessage(lang, 'paymentVerifiedSuccess'),
        data: {
          orderId: razorpay_order_id,
          paymentId: razorpay_payment_id,
          status: 'VERIFIED'
        }
      });
    } else {
      // Signature is invalid.
      console.warn(`Invalid payment signature for order ${razorpay_order_id} by user ${req.user.uid}`);
      res.status(400).json({
        success: false,
        message: getMessage(lang, 'paymentVerificationFailed'),
        code: 'SIGNATURE_INVALID'
      });
    }
  } catch (error) {
    console.error('Error verifying Razorpay payment:', error);
    res.status(500).json({
      success: false,
      message: getMessage(lang, 'error', { message: 'Payment verification process failed.' }),
      code: 'VERIFICATION_PROCESS_FAILED',
      ...(process.env.NODE_ENV === 'development' && { debug: error.message })
    });
  }
});


// Razorpay Webhook Handler
// Note: This endpoint should NOT use authenticateToken middleware if Razorpay doesn't send JWT.
// It relies on webhook secret for verification.
app.post('/api/payments/webhook', express.raw({type: 'application/json'}), async (req, res) => {
  const secret = process.env.RAZORPAY_WEBHOOK_SECRET;
  const lang = 'en'; // Webhooks don't have user language context easily

  if (!secret) {
    console.error('Razorpay Webhook Secret not configured.');
    return res.status(500).send('Webhook secret not configured.');
  }

  const signature = req.headers['x-razorpay-signature'];

  try {
    const isValidWebhook = razorpayService.verifyWebhookSignature(req.body, signature, secret);

    if (isValidWebhook) {
      console.log('Razorpay Webhook received and signature verified.');
      const eventPayload = JSON.parse(req.body.toString()); // req.body is a Buffer here
      const eventType = eventPayload.event;
      const paymentEntity = eventPayload.payload.payment.entity;
      const orderEntity = eventPayload.payload.order?.entity; // Order entity might not always be present

      console.log(`Webhook event: ${eventType}, Payment ID: ${paymentEntity.id}, Order ID: ${paymentEntity.order_id}`);

      // Handle different webhook events, e.g., payment.captured, payment.failed, order.paid
      switch (eventType) {
        case 'payment.captured':
          // Payment is successful and captured.
          // Update your database, fulfill order, send notifications, etc.
          // e.g., await parseService.updateOrderStatus(paymentEntity.order_id, 'CAPTURED', paymentEntity);
          console.log(`Payment captured for Order ID: ${paymentEntity.order_id}, Payment ID: ${paymentEntity.id}`);
          // TODO: Implement database update for payment captured.
          break;
        case 'payment.failed':
          // Payment failed.
          // Update database, notify user if necessary.
          // e.g., await parseService.updateOrderStatus(paymentEntity.order_id, 'FAILED', paymentEntity);
          console.log(`Payment failed for Order ID: ${paymentEntity.order_id}, Payment ID: ${paymentEntity.id}. Reason: ${paymentEntity.error_description}`);
          // TODO: Implement database update for payment failed.
          break;
        case 'order.paid':
          // Order has been paid (might not be captured yet if capture is manual).
          console.log(`Order paid for Order ID: ${orderEntity.id}, Amount: ${orderEntity.amount_paid}`);
          // TODO: Potentially update order status if relevant to your flow.
          break;
        // Add more cases as needed for other events like refunds, disputes, etc.
        default:
          console.log(`Unhandled webhook event type: ${eventType}`);
      }

      res.status(200).json({ status: 'ok' }); // Respond to Razorpay quickly
    } else {
      console.warn('Invalid Razorpay Webhook signature.');
      res.status(400).send('Invalid signature.');
    }
  } catch (error) {
    console.error('Error processing Razorpay webhook:', error);
    res.status(500).send('Error processing webhook.');
  }
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
