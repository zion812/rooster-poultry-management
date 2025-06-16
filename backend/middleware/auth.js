const admin = require('firebase-admin');
const { getMessage } = require('../config/translations');

/**
 * Initialize Firebase Admin SDK
 * Uses environment variables for configuration
 */
function initializeFirebase() {
  if (!admin.apps.length) {
    const serviceAccount = process.env.GOOGLE_APPLICATION_CREDENTIALS;
    const projectId = process.env.FIREBASE_PROJECT_ID;
    
    if (serviceAccount && projectId) {
      admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        projectId: projectId
      });
    } else {
      // For development or when using default credentials
      admin.initializeApp({
        credential: admin.credential.applicationDefault(),
        projectId: projectId
      });
    }
  }
}

/**
 * Middleware for JWT authentication using Firebase
 * Optimized for rural networks with minimal data overhead
 */
const authenticateToken = async (req, res, next) => {
  try {
    // Initialize Firebase if not already done
    initializeFirebase();
    
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    const lang = req.query.lang || req.headers['accept-language'] || 'en';
    
    if (!token) {
      return res.status(401).json({
        success: false,
        message: getMessage(lang, 'unauthorized'),
        code: 'AUTH_TOKEN_MISSING'
      });
    }
    
    // Verify Firebase ID token
    const decodedToken = await admin.auth().verifyIdToken(token);
    
    // Add user info to request
    req.user = {
      uid: decodedToken.uid,
      email: decodedToken.email,
      emailVerified: decodedToken.email_verified,
      name: decodedToken.name || decodedToken.email,
      // Add custom claims if available
      ...decodedToken
    };
    
    // Add language preference to request
    req.lang = lang;
    
    next();
  } catch (error) {
    const lang = req.query.lang || req.headers['accept-language'] || 'en';
    
    // Handle different Firebase auth errors
    let errorMessage = getMessage(lang, 'unauthorized');
    let statusCode = 403;
    
    if (error.code === 'auth/id-token-expired') {
      errorMessage = getMessage(lang, 'error', { message: 'Token expired' });
      statusCode = 401;
    } else if (error.code === 'auth/id-token-revoked') {
      errorMessage = getMessage(lang, 'error', { message: 'Token revoked' });
      statusCode = 401;
    } else if (error.code === 'auth/invalid-id-token') {
      errorMessage = getMessage(lang, 'error', { message: 'Invalid token' });
      statusCode = 401;
    }
    
    return res.status(statusCode).json({
      success: false,
      message: errorMessage,
      code: error.code || 'AUTH_FAILED',
      // Include minimal error details for debugging (only in development)
      ...(process.env.NODE_ENV === 'development' && { debug: error.message })
    });
  }
};

/**
 * Optional authentication middleware
 * Continues even if token is invalid but adds user info if valid
 */
const optionalAuth = async (req, res, next) => {
  try {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    
    if (token) {
      initializeFirebase();
      const decodedToken = await admin.auth().verifyIdToken(token);
      req.user = {
        uid: decodedToken.uid,
        email: decodedToken.email,
        emailVerified: decodedToken.email_verified,
        name: decodedToken.name || decodedToken.email,
        ...decodedToken
      };
    }
    
    req.lang = req.query.lang || req.headers['accept-language'] || 'en';
    next();
  } catch (error) {
    // Continue without authentication
    req.lang = req.query.lang || req.headers['accept-language'] || 'en';
    next();
  }
};

/**
 * Check if user has specific role or permission
 * @param {string|array} roles - Required role(s)
 */
const requireRole = (roles) => {
  return (req, res, next) => {
    const userRoles = req.user?.customClaims?.roles || [];
    const requiredRoles = Array.isArray(roles) ? roles : [roles];
    
    const hasRole = requiredRoles.some(role => userRoles.includes(role));
    
    if (!hasRole) {
      const lang = req.lang || 'en';
      return res.status(403).json({
        success: false,
        message: getMessage(lang, 'unauthorized'),
        code: 'INSUFFICIENT_PERMISSIONS'
      });
    }
    
    next();
  };
};

module.exports = {
  authenticateToken,
  optionalAuth,
  requireRole,
  initializeFirebase
};