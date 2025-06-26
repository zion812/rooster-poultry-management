# AGENTS.md - Rooster Poultry Management App

This document provides guidance for AI agents working on the Rooster Poultry Management App codebase. Adherence to these principles is crucial for maintaining a scalable, maintainable, and robust application.

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
<<<<<<< jules/arch-assessment-1
    *   **Migrations:** When entity schemas change, database version MUST be incremented and a proper `androidx.room.migration.Migration` class MUST be provided to the database builder. For development, fallbackToDestructiveMigration may be used temporarily, but never for release builds.
*   **Networking:** Retrofit for API calls, OkHttp for client customization.
    *   **Authentication:** Token-based authentication is handled by `AuthInterceptor` (for adding token to outgoing requests) and `TokenAuthenticator` (for refreshing tokens on 401 errors). Avoid `runBlocking` in interceptors for fetching tokens; proactive fetching or reactive token management is preferred. `TokenAuthenticator` may use `runBlocking` for synchronous token refresh as a concession if the token provider API is suspend-only.
*   **Serialization:** `kotlinx.serialization` is the standard for all new DTOs and Retrofit converter factories. Existing Gson usage should be phased out where practical.
*   **Background Tasks:** WorkManager for deferrable background tasks.
    *   **Hilt Integration:** Use `@HiltWorker` and inject `HiltWorkerFactory` into the Application class's `WorkManagerConfiguration.Provider` for DI in workers.
  =======
*   **Networking:** Retrofit for API calls, OkHttp for client customization.
*   **Serialization:** `kotlinx.serialization` is preferred for new code. Gson is present and may be used in existing code or for interop where necessary.
*   **Background Tasks:** WorkManager for deferrable background tasks, including data synchronization.
>>>>>>> main

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
<<<<<<< jules/arch-assessment-1
*   **Synchronization:**
    *   **`feature-farm` Data Sync:** Uses a `FarmDataSyncWorker` (WorkManager `CoroutineWorker`) to upload locally created/modified farm data (Flocks, etc.) to Firebase.
    *   **Sync Flags:** Room entities intended for synchronization (e.g., `FlockEntity`) use a `needsSync: Boolean` flag. This flag is set to `true` when data is created/modified locally. The sync worker uploads items where `needsSync = true` and sets it to `false` upon successful remote persistence.
    *   **Enqueueing:** Sync workers are typically enqueued periodically and/or when network connectivity is restored.
    *   WorkManager jobs should be designed to be resilient to network interruptions, with appropriate retry strategies.
=======
*   **Synchronization:** WorkManager jobs should be designed to be resilient to network interruptions, with appropriate retry strategies.
>>>>>>> main

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

<<<<<<< jules/arch-assessment-1
## 6. Data Synchronization Strategy (General)

*   **Conflict Resolution:** (Future Work) Define and implement clear strategies for handling data conflicts between local and remote sources (e.g., last-write-wins, server authoritative, user prompted). This is critical for bidirectional sync.
*   **Integrity:** Ensure data remains consistent across client and server.
*   **Transparency:** Provide users with feedback on sync status and any errors.
*   **Uploads:** For features requiring offline creation/modification (like `feature-farm`), use a `needsSync` flag in local Room entities. A dedicated `WorkManager` worker should periodically query for items with `needsSync = true`, attempt to upload them to the remote data source, and clear the flag on success.
*   **Downloads/Caching:** Data fetched from remote sources should be cached in Room. If real-time updates are needed, use mechanisms like Firebase Realtime listeners or Firestore snapshot listeners, updating the Room cache upon receiving new remote data. Cached data from remote should have its `needsSync` flag set to `false`.

## 7. Resolved Configuration Issues & Key Architectural Decisions

*   **`core:core-network` Build:** `build.gradle.kts` for `core-network` has been created and configured.
*   **`feature-farm` Data Source:** Now correctly uses `FirebaseFarmDataSource` for remote operations, not a mock.
*   **`AuthInterceptor` `runBlocking`:** The use of `runBlocking` for *forced* token refresh in `AuthInterceptor` has been moved to `TokenAuthenticator`. The interceptor itself still uses `runBlocking` for an initial, non-forced token fetch, which is a known point for future optimization if needed.
*   **`PAYMENT_API_BASE_URL`:** This URL is now sourced from `BuildConfig` (defined in `app/build.gradle.kts`) and provided via Hilt using a qualifier (`@PaymentApiBaseUrl`). Constants.kt no longer hardcodes it.
*   **`FarmRepository` Consolidation:** The redundant `NetworkAwareFarmRepository` has been removed, and its beneficial mapping logic merged into `FarmRepositoryImpl`.
*   **Standardized Serialization:** `core-network`'s Retrofit instances now all use `kotlinx.serialization`. All new DTOs should use `@Serializable`.
=======
## 6. Data Synchronization

*   **Conflict Resolution:** Define and implement clear strategies for handling data conflicts between local and remote sources.
*   **Integrity:** Ensure data remains consistent across client and server.
*   **Transparency:** Provide users with feedback on sync status and any errors.

## 7. Addressing Project Configuration Issues

*   **`core:core-network` Module:** This module is currently missing its `build.gradle.kts` file. This **must be rectified** for the network layer to function correctly. A standard Android library `build.gradle.kts` file needs to be created for it, declaring its dependencies (e.g., Retrofit, OkHttp, Hilt, Kotlinx Serialization).
>>>>>>> main

This document is a living guide. It will be updated as the project evolves.
If you have suggestions for improving these guidelines, please discuss them with the team.
