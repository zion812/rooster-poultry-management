# Rooster Poultry Management Project – Developer Dictionary

This document provides a comprehensive reference for key terms, modules, and concepts used throughout the Rooster Poultry Management project. It is intended to help new and existing developers quickly understand the architecture, terminology, and responsibilities across the codebase.

---

## Table of Contents
- [App Overview](#app-overview)
- [Core Modules](#core-modules)
- [Feature Modules](#feature-modules)
- [Backend](#backend)
- [Cloud Functions](#cloud-functions)
- [Common Terms & Patterns](#common-terms--patterns)
- [Development Practices](#development-practices)
- [Testing](#testing)

---

## App Overview
- **Rooster Poultry Management App:** Android application designed for rural Telugu farmers to optimize poultry operations and increase income.
- **Tech Stack:** Kotlin, Jetpack Compose, Hilt (DI), Firebase, Gradle (Kotlin DSL).
- **Key Features:** Modular architecture, 2G optimization, localization (Telugu/English), real-time monitoring, and performance optimization.

---

## Core Modules
- **core-common:** Shared utilities, constants, DTOs, core interfaces (e.g., `UserIdProvider`, `ImageUploadService`), extension functions, and `Result` wrapper.
- **core-network:** Network abstraction layer, including Retrofit/OkHttp setup, `TokenProvider`, `TokenAuthenticator`, `NetworkModule` (Hilt), and network utility functions.
- **core-navigation:** Defines base navigation contracts (`AppScreens`, `AppNavHost` structure) for modular navigation.
- **core-analytics:** Provides an abstraction layer (`AnalyticsService`) for analytics, implemented with Firebase Analytics.
- **core-search:** Defines contracts (`SearchRepository`, `SearchResultItem`) for a global search feature (includes placeholder implementation).

---

## Feature Modules
- **feature-farm:** Comprehensive farm management including flock details, lineage tracking, health records, and offline data synchronization via `FarmDataSyncWorker`.
- **feature-marketplace:** Enables browsing, listing (with image uploads), and purchasing products. Supports offline caching and sync for listings and orders. Cart is currently local-only. Implements pagination for product lists.
- **feature-community:** Basic social features allowing users to create profiles, make text posts, and view a feed. Supports offline content creation. Many interactions (likes, comments, media posts) are TODOs.
- **feature-auctions:** (Not fully reviewed in this pass but significant backend logic exists) Handles poultry auctions, likely integrated with marketplace and live streaming.

---

## Backend
- **Purpose:** Node.js/Express.js REST API server. Serves as an API gateway for specialized services:
    - Poultry price prediction (using historical data from Parse).
    - Payment orchestration (integrating with Razorpay).
    - Intermediary for some interactions with the Parse (Back4App) backend.
- **Authentication:** Verifies Firebase ID Tokens for protected endpoints using `firebase-admin`.
- **Stack:** Node.js, Express.js, Firebase Admin, Parse SDK (client), Razorpay SDK, Docker.
- **Key Files:**
  - `server.js`: Main API server.
  - `config/`, `middleware/`, `services/`: Configurations, middleware, and business logic.
  - `Dockerfile`, `docker-compose.yml`: Containerization.
  - `.env.example`: Environment variable template.
- **Testing:** Jest, Supertest.

---

## Cloud Functions
- **Purpose:** Serverless logic hosted on Parse Server (Back4App) using Parse Cloud Code.
- **Key Functionalities:**
    - Advanced Auction System: Manages auction lifecycle, bidding, deposits, winner processing.
    - Live Streaming: Basic infrastructure for live broadcasts with virtual coin/gifting.
    *   Virtual Token/Coin System: Cloud functions for managing user token balances.
    *   Data Aggregation & Utility Functions: Metrics, activity verification.
    *   Automated Database Indexing: `beforeFind` hooks to create Parse DB indexes.
- **Stack:** Parse Cloud Code (Node.js). Deployed via `parse deploy`.
- **Key Files:**
    - `main.js`: Main cloud function entry point, includes auction logic, token management, etc.
    - `liveStreamingFunctions.js`: Logic for the live streaming feature.
    - `security/sanitizer.js`: Input sanitization for cloud functions.
- **Firebase Rules Location:** Note that Firebase security rules (`firestore.rules`, `realtime-database.rules.json`) are currently co-located in the `cloud/` directory. Their deployment is managed via Firebase CLI.

---

## Common Terms & Patterns
- **MVVM (Model-View-ViewModel):** Architecture for UI logic separation.
- **Hilt:** Dependency injection framework for Android.
- **Jetpack Compose:** Modern UI toolkit for building native Android UIs.
- **Repository Pattern:** Abstracts data access, mediating between domain and data layers.
- **Use Case/Interactor:** Encapsulates a single business action or workflow.
- **WorkManager:** Android library for deferrable, guaranteed background work (e.g., `FarmDataSyncWorker`).
- **`needsSync` flag:** A boolean flag in Room entities indicating if local data needs to be synchronized with the remote backend.
- **Network-Bound Resource:** A pattern used in repositories to coordinate data fetching from local cache and remote sources, providing a single source of truth to the UI.
- **Firebase:** Suite of services used for:
    - **Authentication:** Firebase Authentication for user sign-in.
    - **Database:** Firestore and Realtime Database for application data (e.g., farm, marketplace, community features).
    - **Storage:** Firebase Storage for image uploads (e.g., marketplace listings).
    - **Messaging (FCM):** For push notifications.
    - **Analytics & Crashlytics:** For app monitoring.
- **Parse Platform (Back4App):** Backend-as-a-Service used for:
    - **Cloud Code:** Hosting serverless functions for complex business logic (auctions, live streaming, token system).
    - **Database:** Storing specific datasets (e.g., historical poultry prices, Parse User objects with token balances, auction/live stream data).
- **Dual Backend Interaction:** The app interacts directly with Firebase for many features, while the custom Node.js `backend/` server often acts as an API gateway or service layer that can itself interact with Parse.

---

## Development Practices
- **Modularity:** Core logic and features are separated into Gradle modules (`:core-*`, `:feature-*`, `:app`).
- **Offline-First:** Prioritized through local caching (Room) and data synchronization strategies.
- **Database Migrations:** Room database schema changes require proper `Migration` classes. `FarmDatabase` has this implemented. `MarketplaceDatabase`, `CommunityDatabase`, and the app-level `PhotoUploadDatabase` currently use `fallbackToDestructiveMigration()` and need proper migration paths for production.
- **Localization:** Support for Telugu and English.
- **Testing:** Unit tests (JUnit, MockK), UI tests (Compose), and potentially backend tests (Jest/Supertest).
- **Performance & 2G Optimization:** Considerations for low-bandwidth environments, including data compression and pagination.
- **CI/CD:** Scripts available for deployment and maintenance tasks.

---

## Testing
- **App:** Unit tests (Kotlin), UI tests (Compose, Playwright).
- **Backend:** Jest, Supertest.
- **Cloud:** No dedicated test scripts, but logic is modular and testable via Parse.

---

## Quick Reference: Directory Structure
- `app/` – Main Android app.
- `core/` – Shared logic and network modules.
- `feature/` – Feature-specific modules (e.g., farm management).
- `backend/` – Node.js API server.
- `cloud/` – Parse Cloud functions and serverless logic.
- `tests/` – Test suites.
- `scripts/`, `docs/`, `tools/` – Automation, documentation, utilities.

---

For further details on any module or concept, see the README or request a code-level walkthrough.
