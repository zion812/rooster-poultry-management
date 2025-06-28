# Rooster Poultry Management – Cloud Code (Parse Server) & Firebase Rules

This directory contains server-side logic deployed as Parse Cloud Code on Back4App, along with Firebase security rules.

## Purpose

The Parse Cloud Code (`main.js`, `liveStreamingFunctions.js`, `security/sanitizer.js`) extends the capabilities of the Parse Server backend. It implements complex business logic, server-side validations, and automated tasks that are not suitable for client-side execution. This includes features like advanced auctions, live streaming, and a virtual token/coin economy.

The Firebase rules (`firestore.rules`, `realtime-database.rules.json`) define access control for data stored directly in Firebase services (Firestore, Realtime Database) by the Android application.

## Structure

*   **`main.js`**: The primary entry point for all Parse Cloud Code functions. It includes:
    *   Automated input sanitization for all Cloud Functions.
    *   Functions for managing marketplace listings with auction capabilities (`createMarketplaceListing`, `getEnhancedMarketplaceListings`).
    *   Advanced auction processing logic (`processAuctionCompletion`, `processWinnerPayment`, various helper functions for deposits and bidder progression).
    *   A virtual token/coin system (`deductUserTokens`, `addUserTokens`).
    *   Functions for fetching data (public bird profiles, market summaries, admin performance metrics).
    *   Activity-based farmer verification logic.
    *   `beforeFind` hooks for programmatic database index creation on various Parse classes.
*   **`liveStreamingFunctions.js`**: Contains Cloud Functions specifically for a live video streaming feature, including session management (start, join, stop) and a gifting system using virtual coins.
*   **`security/sanitizer.js`**: A utility module for sanitizing inputs to Cloud Functions, helping to prevent injection attacks.
*   **`package.json`**: Defines Node.js dependencies for the Cloud Code environment (primarily the Parse SDK).
*   **Firebase Rules:**
    *   `firestore.rules`: Security rules for Cloud Firestore.
    *   `realtime-database.rules.json`: Security rules for Firebase Realtime Database.
    *   *Note: The deployment and management of these Firebase rules might occur through Firebase CLI tools, separate from the Parse Cloud Code deployment.*

## Key Cloud Code Features

*   **Enhanced Auction System:** Manages complex auction lifecycles, including bidding, deposits, winner selection, and payment status tracking.
*   **Marketplace & Auction Integration:** Allows marketplace listings to be tied to auctions.
*   **Live Streaming Platform:** Basic infrastructure for live broadcasts with virtual gifting.
*   **Virtual Token/Coin Economy:** System for users to earn/spend virtual tokens or coins.
*   **Server-Side Logic & Automation:** Includes functions for data aggregation (metrics, summaries), activity verification, and automated database index creation.
*   **Security:** Input sanitization for Cloud Functions.

## Build & Deploy (Parse Cloud Code)

1.  **Prerequisites:**
    *   Node.js (version 16+ recommended, see `package.json`).
    *   Parse CLI installed and configured for your Back4App project.
2.  **Install Dependencies:** Run `npm install` in the `cloud/` directory.
3.  **Deploy:** Use the Parse CLI command `parse deploy` from within the `cloud/` directory to deploy the Cloud Code to your Back4App instance.

## Firebase Rules

The Firebase rules (`firestore.rules`, `realtime-database.rules.json`) should be deployed to your Firebase project using the Firebase CLI (`firebase deploy --only firestore:rules` and `firebase deploy --only database`). Ensure these rules align with your application's data access patterns from the Android client and any backend services interacting with Firebase.

## Contribution

*   Follow project-wide guidelines in `DEVELOPER_ONBOARDING.md`.
*   When adding or modifying Cloud Functions, ensure inputs are sanitized and proper error handling is implemented.
*   Update or add database indexes via `beforeFind` hooks if new query patterns are introduced.
*   If Firebase data structures change, update the corresponding Firebase rules files.

---
