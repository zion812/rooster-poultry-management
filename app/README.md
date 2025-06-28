# Rooster Poultry Management – Android App Module

## Purpose
The `:app` module is the main entry point and orchestrator for the Rooster Poultry Management Android application. It's built with Kotlin, Jetpack Compose, and uses Hilt for dependency injection.

## Structure
- `src/main/java/com/example/rooster/App.kt`: The main Application class, responsible for Hilt setup, Parse SDK initialization, WorkManager configuration, and other global initializations.
- `src/main/java/com/example/rooster/MainActivity.kt`: The main UI entry point, hosting the primary navigation graph (`AppNavHost`) and integrating feature modules.
- `src/main/java/com/example/rooster/di/`: Contains app-level Hilt modules for providing dependencies like `FirebaseStorage` and binding implementations for core interfaces (e.g., `UserIdProvider`, `ImageUploadService`).
- `src/main/java/com/example/rooster/data/`: Contains app-specific data components, including implementations for `FirebaseUserIdProvider` and `FirebaseStorageImageUploadService`. Also includes a `PhotoUploadDatabase` (Room) for managing photo uploads and messages.
- `src/main/java/com/example/rooster/config/Constants.kt`: App-specific constants, including backend keys.
- `src/main/java/com/example/rooster/ (various other files)`: Contains UI screens, ViewModels, and utility classes, some ofwhich are legacy or awaiting refactoring into dedicated feature modules.
- `src/test/java/` – Unit tests.
- `src/androidTest/java/` – UI/instrumentation tests.

## Key Responsibilities & Features
- Initializes application-wide services (Hilt, Parse SDK, Firebase services, WorkManager).
- Provides the main `Activity` and sets up the primary Jetpack Compose navigation graph, integrating feature modules.
- Implements core service interfaces defined in `:core-common` (e.g., `FirebaseUserIdProvider`, `FirebaseStorageImageUploadService`).
- Manages app-level DI and configuration.
- Supports localization (Telugu/English).
- Focuses on performance and resource optimization for low-end devices and 2G networks.

## Build & Run
- Open in Android Studio (JDK 11+)
- Sync Gradle, build, and run on emulator/device

## Testing
- Unit: `./gradlew test`
- UI: `./gradlew connectedAndroidTest`

## Contribution
- Follow project-wide guidelines in `DEVELOPER_ONBOARDING.md`
- Add/maintain tests and documentation for new features

---
