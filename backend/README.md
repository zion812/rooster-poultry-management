# Rooster Poultry Management – Backend API Server

This Node.js/Express.js application serves as the primary backend API for the Rooster mobile app. It provides specialized services such as poultry price prediction and payment processing, and acts as an intermediary for some interactions with a Parse Server backend (Back4App).

## Structure

-   **`server.js`**: Main Express application entry point. Initializes middleware, defines routes, and handles global error management.
-   **`services/`**: Contains the core business logic:
    *   `parseService.js`: Client for interacting with the Parse Server (Back4App) via its REST API. Used for fetching data like historical poultry prices and potentially other domain-specific data.
    *   `pricePredictor.js`: Implements algorithms for poultry price prediction using data from `parseService`.
    *   `razorpayService.js`: Integrates with the Razorpay SDK to manage payment order creation and signature verification.
-   **`middleware/`**:
    *   `auth.js`: Handles authentication of API requests using Firebase ID tokens. Initializes `firebase-admin` SDK for token verification.
-   **`config/`**:
    *   `translations.js`: Manages localized (English, Telugu) strings for API responses.
-   **`server/init.js`**: (Assumed, based on import in `server.js`) Potentially handles initial schema setup or other one-time initialization tasks.
-   **Environment Variables**: Uses `.env` files for configuration (API keys, server settings). Templates like `.env.example` and `.env.back4app.template` are provided.
-   **Containerization**: `Dockerfile` and `docker-compose.yml` are included for building and running the server in Docker containers.

## Key Features & Endpoints

*   **Price Prediction:**
    *   `GET /api/predict-price`: Predicts poultry prices for a given region.
    *   `GET /api/market-summary`: Provides a market summary for a region.
    *   `POST /api/predict-bulk`: Bulk price prediction for multiple regions.
    *   Relies on historical data from Parse backend via `parseService.js`.
*   **Payment Processing (Razorpay):**
    *   `POST /api/payments/orders`: Creates a payment order with Razorpay.
    *   `POST /api/payments/verify`: Verifies payment signatures from client-side Razorpay completion.
    *   `POST /api/payments/webhook`: Handles incoming webhooks from Razorpay for payment status updates.
*   **Data Endpoints (via Parse):**
    *   `GET /api/regions`: Fetches available regions.
    *   `GET /api/fowl-types`: Fetches available fowl types.
*   **Authentication:** Protected endpoints use JWT (Firebase ID tokens) verification.
*   **Security & Optimization:** Implements `helmet` for security headers, `cors` for cross-origin requests, `express-rate-limit` for abuse prevention, and `compression` for optimizing responses for low-bandwidth networks.
*   **Logging:** Uses `morgan` for request logging.

## Build & Run

1.  **Prerequisites:** Node.js (version 18+ recommended, see `package.json` engines).
2.  **Install Dependencies:** `npm install`
3.  **Environment Setup:** Create a `.env` file based on `.env.example` and fill in necessary API keys and configurations (Back4App credentials, Firebase service account details, Razorpay keys, etc.).
4.  **Run Development Server:** `npm run dev` (uses `nodemon` for auto-restarts)
5.  **Run Production Server:** `npm start`
6.  **Docker:**
    *   Build: `npm run docker:build` or `docker-compose build`
    *   Run: `npm run docker:run` or `docker-compose up`

## Testing

*   Unit and integration tests are run using Jest and Supertest: `npm test`

## API Documentation

*   Basic endpoint listing available at `GET /api/docs`.
*   Refer to `../docs/API_OVERVIEW.md` for a more detailed overview. (TODO: Consider generating Swagger/OpenAPI documentation).

---
