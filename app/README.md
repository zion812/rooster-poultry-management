# Rooster Poultry Management – Android App Module

## Purpose
The main Android application for rural poultry management, built with Kotlin, Jetpack Compose, and Hilt.

## Structure
- `src/main/java/com/example/rooster/` – Main app code (UI, ViewModels, DI, features)
- `src/test/java/` – Unit tests
- `src/androidTest/java/` – UI/instrumentation tests

## Key Features
- Modular architecture (feature modules, core modules)
- MVVM pattern with Hilt for DI
- Localization (Telugu/English)
- Performance and resource optimization for low-end devices

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
