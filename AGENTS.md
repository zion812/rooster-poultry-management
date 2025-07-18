# AGENTS.md - Rooster Poultry Management App

This document provides guidance for AI agents working on the Rooster Poultry Management App codebase. Adherence to these principles is crucial for maintaining a scalable, maintainable, and robust application.

## 0. Current Development Context & Priorities

**Primary Objective: Functional Application Showcase for Fundraising**

The immediate development goal is to produce a compelling and functional showcase of the Rooster app's capabilities. This showcase is intended for presentations to potential investors and stakeholders to secure funding for further development and full-scale deployment.

**Implications for AI Agent Tasks:**

*   **Feature Completeness for Demonstration:** Implement features as completely as possible in the codebase to demonstrate their full potential and user experience, even if some backend dependencies are simplified or mocked for this phase.
*   **Payment Gateway Integration (Deferred Live Transactions):**
    *   For features requiring payments (e.g., marketplace checkout, auction deposits), the UI and backend flows should be built out.
    *   However, direct calls to live payment gateway APIs (like Razorpay) should be **stubbed, mocked, or use a test mode** if available from the gateway. Live API keys are not expected to be available during this phase.
    *   The system must be designed for straightforward integration of live API keys when they are procured post-funding. Document the integration points clearly.
*   **Free-Tier Resource Management:**
    *   Be mindful that backend services (Firebase, Back4app/Parse) are operating on free tiers.
    *   Avoid implementing solutions that would clearly lead to excessive costs or quick exhaustion of free tier limits during normal showcase usage (e.g., overly frequent polling, storage of excessively large unoptimized media, very complex and frequent Cloud Function invocations for non-critical paths).
    *   Flag operations that are potentially resource-intensive for future optimization or scaling considerations once funding allows for paid tiers.
    *   Prioritize efficient data models and queries.
*   **Focus on Demonstrable Value:** When prioritizing tasks or encountering TODOs, consider their impact on the ability to showcase the app's core value proposition to farmers and potential investors.

---

## 1. Core Architectural Principles

*   **Clean Architecture:** The project follows Clean Architecture. Ensure all new code respects the separation of layers (Presentation, Domain, Data).
    *   **Presentation:** Jetpack Compose (UI), MVVM (ViewModels with Hilt). UI logic, state management.
    *   **Domain:** Use cases, business logic entities. Pure Kotlin modules where possible. This layer should be independent of Android framework dependencies.
    *   **Data:** Repositories, data sources (network, local database). Handles data fetching, storage, and synchronization.
*   **Modularity:** The app is divided into `app`, `core-*`, and `feature-*` modules.
    *   `core-common`: Truly common utilities, base classes, DTOs, and constants used across multiple modules.
    *   `core-network`: Networking infrastructure (Retrofit, OkHttp, serialization, base repository interfaces/abstractions if applicable).
    *   `feature-*`: Self-contained features with their own Presentation, Domain, and Data layers (or parts thereof). Features should be as independent as possible.
    *   Dependency Rule: Features can depend on core modules. Features should generally not depend directly on other features (use shared core modules or defined API modules for inter-feature communication if absolutely necessary and approved). `app` module ties features together.
*   **Dependency Injection:** Hilt is used for dependency injection.
    *   Annotate ViewModels with `@HiltViewModel`.
    *   Provide dependencies in appropriate Hilt modules (`@Module`, `@InstallIn`).
    *   Use constructor injection wherever possible.

## 2. Technology Stack & Key Libraries

*   **UI:** Jetpack Compose.
*   **Asynchronous Operations:** Kotlin Coroutines and Flow. Ensure proper CoroutineScope management and structured concurrency.
*   **Database:** Room for local persistence.
    *   **Migrations:** When entity schemas change, database version MUST be incremented and a proper `androidx.room.migration.Migration` class MUST be provided to the database builder. For development, fallbackToDestructiveMigration may be used temporarily, but never for release builds.
*   **Networking:** Retrofit for API calls, OkHttp for client customization.
    *   **Authentication:** Token-based authentication is handled by `AuthInterceptor` (for adding token to outgoing requests) and `TokenAuthenticator` (for refreshing tokens on 401 errors). Avoid `runBlocking` in interceptors for fetching tokens; proactive fetching or reactive token management is preferred. `TokenAuthenticator` may use `runBlocking` for synchronous token refresh as a concession if the token provider API is suspend-only.
*   **Serialization:** `kotlinx.serialization` is the standard for all new DTOs and Retrofit converter factories. Existing Gson usage should be phased out where practical.
*   **Background Tasks:** WorkManager for deferrable background tasks.
    *   **Hilt Integration:** Use `@HiltWorker` and inject `HiltWorkerFactory` into the Application class's `WorkManagerConfiguration.Provider` for DI in workers.

## 3. 2G Network Optimization & Offline-First

This application targets rural users with potentially unreliable and slow (2G) internet.
*   **Offline-First:** Critical data and functionality must be available offline. Cache aggressively using Room.
*   **Data Efficiency:**
    *   Minimize payload sizes for API requests/responses.
    *   Implement differential updates for synchronization where possible.
    *   Optimize images and other assets.
*   **UI Responsiveness:**
    *   Network operations must not block the main thread.
    *   Provide immediate user feedback (loading states, progress indicators).
    *   Gracefully handle network errors and timeouts.
*   **Synchronization:**
        *   Dedicated `WorkManager` workers (`FarmDataSyncWorker`, `MarketplaceSyncWorker`, `CommunitySyncWorker`) are implemented for `feature-farm`, `feature-marketplace`, and `feature-community` respectively.
        *   These workers are responsible for uploading locally created/modified entities (e.g., `FlockEntity`, `ProductListingEntity`, `OrderEntity`, `CommunityUserProfileEntity`, `PostEntity`, `CommentEntity`) that are marked with `needsSync = true` to the remote backend (primarily Firestore).
        *   **Per-Item Retry Logic:** Workers now implement per-item retry logic. Entities track `syncAttempts` and `lastSyncAttemptTimestamp`. If an item fails to sync, its `syncAttempts` count is incremented. If `syncAttempts` exceeds `MAX_SYNC_ATTEMPTS` (e.g., 5), the worker will skip that item in the current run (logging this) but will still signal a retry for the overall job if other items failed or if skipped items still require syncing. Successful sync resets `syncAttempts` to 0 and `needsSync` to `false`.
        *   The `FirebaseFarmDataSource` for `feature-farm` employs a dual-write strategy for some entities (e.g., Flocks, Mortality records), saving them to both Firestore and Firebase Realtime Database.
    *   **Sync Flags & Attempt Tracking:** Room entities intended for synchronization use:
            *   `needsSync: Boolean`: Set to `true` when data is created/modified locally. Cleared by the sync worker upon successful remote persistence.
            *   `syncAttempts: Int`: Tracks the number of failed synchronization attempts for an item.
            *   `lastSyncAttemptTimestamp: Long`: Records the timestamp of the last sync attempt.
    *   **Enqueueing:** Sync workers are enqueued periodically (e.g., every few hours) via `App.kt` and are constrained by network connectivity.
    *   WorkManager jobs utilize `Result.retry()` for transient failures or when items still require syncing after individual attempt limits are considered in a run.

## 4. Code Quality and Conventions

*   **Kotlin Coding Conventions:** Follow official Kotlin style guides.
*   **Ktlint:** Code is linted with Ktlint. Ensure your changes pass Ktlint checks.
*   **SOLID Principles:** Strive to apply SOLID principles in your designs.
*   **Error Handling:**
    *   Use the `Result<T>` pattern (from `core-common`) for operations that can fail.
    *   Define clear error types/exceptions for different failure scenarios.
    *   Provide user-friendly error messages.
*   **Testing:**
    *   Write unit tests for business logic (Use Cases, ViewModels).
    *   Write integration tests for data layer components (Repositories, DAOs, API services).
    *   UI tests (Compose) are encouraged for critical user flows.
*   **Localization:** All user-facing strings must be localized for Telugu.

## 5. Module Interactions

*   Inter-module communication (especially between features) should be minimized.
*   If features need to share data or functionality, consider abstracting it into a `core` module or a dedicated shared API module.
*   Navigation between features should be handled via defined routes (e.g., using the `core-navigation` module).

## 6. Data Synchronization Strategy (General)

*   **Conflict Resolution (Initial Steps):**
    *   When fetching data that is also cached locally (e.g., details for a flock, product, or post), if the local version has `needsSync = true` (indicating unsynced local changes), the system will prioritize emitting the local data and log a warning, rather than immediately overwriting the cache with potentially stale remote data from a listener or general fetch. This helps prevent loss of pending offline edits.
    *   Full conflict resolution (e.g., merging strategies, user prompts for conflicting edits) remains future work for true bidirectional sync scenarios.
*   **Integrity:** Ensure data remains consistent across client and server.
*   **Transparency:** Provide users with feedback on sync status and any errors.
*   **Uploads (via Sync Workers):**
    *   Features requiring offline creation/modification use a `needsSync: Boolean` flag in local Room entities.
    *   Additionally, entities now include `syncAttempts: Int` and `lastSyncAttemptTimestamp: Long` to support robust retry logic.
    *   Dedicated `WorkManager` workers (`FarmDataSyncWorker`, `MarketplaceSyncWorker`, `CommunitySyncWorker`) periodically query for entities with `needsSync = true`.
    *   Workers attempt to upload these items, incrementing `syncAttempts` on failure. Items exceeding `MAX_SYNC_ATTEMPTS` are temporarily skipped in a run to prevent blocking other items.
    *   On successful upload, the worker clears the `needsSync` flag to `false` and resets `syncAttempts` to `0`.
*   **Downloads/Caching:**
    *   Data fetched from remote sources is cached in Room.
    *   When updating the cache from remote listeners or fetches, the system now checks if a local version of the item has `needsSync = true`. If so, the cache update for that specific item is deferred to prevent overwriting unsynced local edits, as detailed under "Conflict Resolution (Initial Steps)". Otherwise, cached data from remote has its `needsSync` flag set to `false`.

## 7. Resolved Configuration Issues & Key Architectural Decisions

*   **`core:core-network` Build:** `build.gradle.kts` for `core-network` has been created and configured.
*   **`feature-farm` Data Source:** Now correctly uses `FirebaseFarmDataSource` for remote operations, not a mock.
*   **`AuthInterceptor` `runBlocking`:** The use of `runBlocking` for *forced* token refresh in `AuthInterceptor` has been moved to `TokenAuthenticator`. The interceptor itself still uses `runBlocking` for an initial, non-forced token fetch, which is a known point for future optimization if needed.
*   **`PAYMENT_API_BASE_URL`:** This URL is now sourced from `BuildConfig` (defined in `app/build.gradle.kts`) and provided via Hilt using a qualifier (`@PaymentApiBaseUrl`). Constants.kt no longer hardcodes it.
*   **`FarmRepository` Consolidation:** The redundant `NetworkAwareFarmRepository` has been removed, and its beneficial mapping logic merged into `FarmRepositoryImpl`.
*   **Standardized Serialization:** `core-network`'s Retrofit instances now all use `kotlinx.serialization`. All new DTOs should use `@Serializable`.
*   **`feature-marketplace` Foundation:**
    *   **Data Layer:** Domain models, Room entities (with `needsSync` flags where appropriate for offline operations like listing creation), DAOs, `MarketplaceDatabase`, repository interfaces (`ProductListingRepository`, `CartRepository`, `OrderRepository`), and a `FirebaseMarketplaceDataSource` shell have been defined. Hilt modules provide these components.
    *   **Repository Implementations:** `ProductListingRepositoryImpl`, `CartRepositoryImpl` (local-only for now), and `OrderRepositoryImpl` have been implemented with initial logic for local caching, remote interaction, and `needsSync` flag management for offline-first support.
    *   **Basic UI Screens:** ViewModels and Composable screens for Product Listing, Product Detail, Cart, and Create Listing have been developed, providing a foundational user flow.
*   **`feature-community` Foundation:**
    *   **Module & Data Layer:** The `feature-community` module has been created. Domain models (UserProfile, Post, Comment), Room entities (with `needsSync`), DAOs, `CommunityDatabase`, repository interfaces, and a `FirebaseCommunityDataSource` shell are defined. Hilt modules are set up.
*   **`feature-farm` Lineage Foundation:**
    *   **Data Layer:** Lineage domain models, `LineageLinkEntity`, `LineageDao`, and updates to `FarmDatabase` (version 3), `FarmRepository`, and `FirebaseFarmDataSource` have been implemented to support tracking flock lineage.
*   **User ID Integration:** A `UserIdProvider` interface (in `core-common`) and `FirebaseUserIdProvider` implementation (in `app` module's `data/authprovider` package, using Firebase Auth) have been introduced. ViewModels like `CartViewModel` and `CreateListingViewModel` now use this provider instead of placeholder IDs. The `:app` module's `AuthBindsModule` handles binding `FirebaseUserIdProvider` to `UserIdProvider`.
*   **Image Upload Service:** The `ImageUploadService` interface (defined in `core-common`) has its concrete implementation `FirebaseStorageImageUploadService` located in the `:app` module's `data/storage` package. This service is used by features like Marketplace for uploading images to Firebase Storage. The `:app` module's `AuthBindsModule` handles binding this implementation. Image compression options are part of the interface, but actual client-side compression logic is noted as pending in the implementation.
*   **WorkManager Hilt Integration:** The main `App.kt` class implements `Configuration.Provider` and injects `HiltWorkerFactory` to enable dependency injection in `WorkManager` workers (e.g., `FarmDataSyncWorker`).
*   **Parse SDK Initialization:** The Parse SDK (Back4App) is initialized in `App.kt`, including registration of ParseObject subclasses. This indicates Parse is used for some backend functionalities alongside Firebase. A specific `app/config/Constants.kt` provides the Back4App keys.
*   **Additional App-Level Database (`PhotoUploadDatabase`):** The `:app` module initializes a Room database named `PhotoUploadDatabase` (for `PhotoUploadEntity` and `MessageEntity`). This database currently uses `fallbackToDestructiveMigration()` and a static singleton access pattern, which are points for future review if this database becomes critical or its schema complex.
*   **Room Migrations (`feature-farm`):** `FarmDatabase` migrations for versions 1-to-2 (adding `needsSync`) and 2-to-3 (adding `lineage_links` table) have been implemented and added to the database builder in `FarmProvidesModule`. `fallbackToDestructiveMigration` has been removed for `FarmDatabase`.
*   **Room Migrations (`feature-marketplace`):** `MarketplaceDatabase` is currently at version 1 and uses `fallbackToDestructiveMigration()` in its Hilt module (`MarketplaceProvidesModule`). **Action Item:** Proper migration strategies MUST be implemented for `MarketplaceDatabase` before any schema changes are made in a production context, adhering to the general database migration guidelines.
*   **Room Migrations (`feature-community`):** `CommunityDatabase` is currently at version 1 and uses `fallbackToDestructiveMigration()` in its Hilt module (`CommunityProvidesModule`). **Action Item:** Similar to Marketplace, proper migration strategies MUST be implemented for `CommunityDatabase` for production readiness.
*   **Marketplace Repository Refinements:** `ProductListingRepositoryImpl` and `OrderRepositoryImpl` were enhanced with a more robust network-bound resource pattern for data fetching and caching. `needsSync` handling in `OrderRepositoryImpl` for updates was improved.
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
*   **Community Repository Implementations:** `CommunityUserProfileRepositoryImpl`, `PostRepositoryImpl`, and `CommentRepositoryImpl` in `feature-community` have been fleshed out with core logic for local/remote data handling, `needsSync` management, and use a network-bound resource pattern.
*   **Basic Community UI (`feature-community`):**
    *   `CommunityProfileScreen` and `ViewModel` created for basic profile display.
    *   `PostFeedScreen` and `ViewModel` created for displaying a list of posts. `PostItem` Composable defined.
    *   `CreatePostScreen` and `ViewModel` created for basic text post creation.
*   **Lineage Implementation (`feature-farm`):**
    *   `FarmRepositoryImpl.getLineageInfo` now contains recursive logic to build lineage trees.
    *   Basic `FlockLineageScreen` and `ViewModel` created for displaying lineage information.
*   **Full Image Handling Workflow (Marketplace):**
    *   `ImageUploadService` interface defined in `core-common`.
    *   `FirebaseStorageImageUploadService` implementation created in `app` module (uploads URIs to Firebase Storage and returns download URLs). Hilt DI set up.
    *   `CreateListingViewModel` now uses `ImageUploadService` to upload selected images. The returned public URLs are stored in `ProductListing.imageUrls`.
    *   Marketplace UI (`ProductListScreen`, `ProductDetailScreen`) now displays images from these Firebase Storage URLs via Coil.
*   **Unit Testing Initiated:** Shell test files with initial test cases created for `CreateListingViewModel` and `FirebaseStorageImageUploadService` as a starting point for comprehensive testing.
 jules/arch-assessment-1
*   **2G Performance Optimization (Ongoing):**
    *   **Pagination (Marketplace Product Listings):** Implemented server-side pagination in `FirebaseMarketplaceDataSource`, `ProductListingRepository`, `ProductListViewModel`, and `ProductListScreen` to significantly reduce initial data load. This serves as a pattern for other list-based features.
    *   **Image Compression (Conceptual Setup):** `ImageUploadService` interface and implementation method signatures updated to accept `ImageCompressionOptions`. `CreateListingViewModel` passes default options. *Actual client-side compression logic is pending.*
    *   **Caching Bugfix:** Corrected `needsSync` flag assignment in `FarmRepositoryImpl.getFlockById` when caching remote data.
    *   **General Best Practice:** For 2G, prioritize server-side pagination for all lists, implement client-side image compression before upload, optimize image loading in UI (e.g. Coil size requests), and use efficient WorkManager strategies. Test thoroughly under simulated 2G conditions.
*   **Client-Side Image Compression Implemented:** `FirebaseStorageImageUploadService` now incorporates client-side image compression logic before uploading to Firebase Storage, using `ImageCompressionOptions`.
*   **Dedicated Sync Workers for Marketplace & Community:** `MarketplaceSyncWorker` and `CommunitySyncWorker` created and enqueued to handle offline data synchronization for their respective features, similar to `FarmDataSyncWorker`.
*   **Robust Sync Worker Retry Logic:** All sync workers (`FarmDataSyncWorker`, `MarketplaceSyncWorker`, `CommunitySyncWorker`) updated to include per-item retry logic. Relevant Room entities (`FlockEntity`, `ProductListingEntity`, `OrderEntity`, `CommunityUserProfileEntity`, `PostEntity`, `CommentEntity`) now have `syncAttempts` and `lastSyncAttemptTimestamp` fields (with corresponding DB migrations) to track and limit retry attempts for individual items, preventing problematic items from blocking the entire sync queue.
*   **Conflict Resolution Enhancement for Local Unsynced Data:** Repository methods that fetch data and update local caches (e.g., `getFlockById`, `getProductListingDetails`, `getOrderDetails`, `getCommunityUserProfile`, `getPostDetails`, `getCommentsForPost`, and list-based equivalents) have been refined. They now check if a local entity has `needsSync = true` before overwriting it with data from a remote listener or fetch, prioritizing local unsynced changes to prevent data loss.
*   **Standardized Result Type for Farm Registration:** The `registerFlock` pathway in `feature-farm` (DataSource, Repository, UseCase, ViewModel) was refactored to use the project's standard `com.example.rooster.core.common.Result<T>` for consistency.
*   **Community Post Liking Feature:** Implemented the ability for users to like and unlike posts in the `feature-community`. This includes UI updates, ViewModel logic, UseCases, Repository methods, and a Firebase backend implementation using Firestore transactions to update `likeCount` and a `likedBy` list on post documents.
*   **Hardened Deserialization and Enum Mapping:** Improved robustness in `FirebaseCommunityDataSource` by adding try-catch blocks for `toObject`/`toObjects` calls to handle potential deserialization errors (e.g., from enum mismatches). Enhanced enum type converters in `MarketplaceTypeConverters` with try-catch and logging.
=======
 jules/arch-assessment-1
=======
=======
*   **Community Repository Shells:** Initial shell implementations for `CommunityUserProfileRepositoryImpl`, `PostRepositoryImpl`, and `CommentRepositoryImpl` were created, including basic local/remote interaction logic and `needsSync` management. Hilt bindings were updated.
*   **Image Handling (Marketplace):** `CreateListingViewModel` and `CreateListingScreen` now support selection of multiple image URIs. The `ProductListing` model stores these as strings (local URIs for now). Actual cloud upload is deferred.
 main
 main
 main

This document is a living guide. It will be updated as the project evolves.
If you have suggestions for improving these guidelines, please discuss them with the team.
