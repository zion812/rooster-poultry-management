# Rooster Poultry Management – Backend API Overview

---

## 1. Introduction
This document provides a starting point for documenting the REST API endpoints exposed by the backend (`backend/server.js`). For full OpenAPI/Swagger integration, see the TODO section below.

---

## 2. Key API Endpoints

This list reflects the primary endpoints identified in `backend/server.js`. All `/api/*` routes are subject to rate limiting. Authentication is typically via Firebase ID Token.

| Method | Endpoint                 | Auth Required | Description                                                                 |
|--------|--------------------------|---------------|-----------------------------------------------------------------------------|
| GET    | /health                  | No            | Health check for the API server.                                            |
| GET    | /api/regions             | Optional      | Get available regions for price prediction (data from Parse).                 |
| GET    | /api/fowl-types          | Optional      | Get available fowl types (data from Parse).                                   |
| GET    | /api/predict-price       | Yes           | Get price prediction for a specific region and optional fowl type.            |
| GET    | /api/market-summary      | Yes           | Get market summary (average prices, trend) for a region (data from Parse).    |
| POST   | /api/predict-bulk        | Yes           | Get price predictions for multiple regions.                                   |
| POST   | /api/payments/orders     | Yes           | Create a Razorpay payment order. (Note: Live transactions deferred for showcase; may return stubbed/test data). |
| POST   | /api/payments/verify     | Yes           | Verify a Razorpay payment signature. (Note: Live transactions deferred for showcase; may use test verification logic). |
| POST   | /api/payments/webhook    | No (Webhook Secret) | Handles incoming webhooks from Razorpay. (Note: Live transactions deferred; will process test/mock webhooks if sent). |
| GET    | /api/docs                | No            | Provides a basic JSON summary of API documentation.                         |

---

## 3. OpenAPI/Swagger Integration (TODO)
- Add `swagger-jsdoc` and `swagger-ui-express` to backend dependencies.
- Create `swagger.yaml` or `swagger.json` in backend directory.
- Document each endpoint with detailed request/response schemas (Joi schemas in `server.js` are a starting point).
- Expose Swagger UI at `/docs` or `/api-docs` endpoint on the backend server.

---

## 4. Example: Predict Price Request & Response

**Request:**
```
GET /api/predict-price?region=Krishna&fowlType=Broiler&days=30&lang=en
Headers:
  Authorization: Bearer <FIREBASE_ID_TOKEN>
```

**Sample Success Response (Structure based on `pricePredictor.js` formatting):**
```json
{
  "success": true,
  "data": {
    "prediction": "Predicted Price: ₹125.50", // Example, localized
    "price": 125.50,
    "priceRange": "Price Range: ₹112.95 - ₹138.05", // Example, localized
    "trend": "Market Trend: Stable", // Example, localized
    "confidence": "Confidence: 75%", // Example, localized
    "recommendation": "Recommendation: Hold", // Example, localized
    "lastUpdated": "Last Updated: 10/7/2023" // Example, localized
  },
  "metadata": {
    "region": "Krishna",
    "fowlType": "Broiler",
    "algorithm": "weighted",
    "dataPoints": 25, // Example
    "confidence": 75 // Example
  }
}
```
**Sample Error Response (e.g., Validation Error):**
```json
{
    "success": false,
    "message": "Error: \"region\" is required", // Example, localized
    "code": "VALIDATION_ERROR"
}
```

---

## 5. Authentication & Security
- Most `/api/*` endpoints (excluding `/api/payments/webhook` and `/api/docs`) require a Firebase ID Token sent as a Bearer token in the `Authorization` header.
- The `/api/payments/webhook` endpoint verifies requests using a shared secret with Razorpay (`X-Razorpay-Signature` header).
- Rate limiting is applied to `/api/*` routes.
- Input validation is performed using Joi schemas for request query/body parameters.
- Security headers are applied using `helmet`.
- CORS is configured (should be restricted to specific origins in production).

---

## 6. Next Steps
- Complete endpoint inventory
- Add OpenAPI/Swagger docs
- Document request/response examples for all endpoints
