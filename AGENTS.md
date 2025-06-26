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
*   **Networking:** Retrofit for API calls, OkHttp for client customization.
*   **Serialization:** `kotlinx.serialization` is preferred for new code. Gson is present and may be used in existing code or for interop where necessary.
*   **Background Tasks:** WorkManager for deferrable background tasks, including data synchronization.

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
*   **Synchronization:** WorkManager jobs should be designed to be resilient to network interruptions, with appropriate retry strategies.

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

## 6. Data Synchronization

*   **Conflict Resolution:** Define and implement clear strategies for handling data conflicts between local and remote sources.
*   **Integrity:** Ensure data remains consistent across client and server.
*   **Transparency:** Provide users with feedback on sync status and any errors.

## 7. Addressing Project Configuration Issues

*   **`core:core-network` Module:** This module is currently missing its `build.gradle.kts` file. This **must be rectified** for the network layer to function correctly. A standard Android library `build.gradle.kts` file needs to be created for it, declaring its dependencies (e.g., Retrofit, OkHttp, Hilt, Kotlinx Serialization).

This document is a living guide. It will be updated as the project evolves.
If you have suggestions for improving these guidelines, please discuss them with the team.
