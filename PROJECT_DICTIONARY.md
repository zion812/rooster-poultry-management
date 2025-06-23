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
- **core-common:** Shared utilities, constants, extension functions, and result wrappers.
- **core-network:** Network abstraction layer, including Retrofit/OkHttp setup, network utilities, and dependency injection.

---

## Feature Modules
- **feature-farm:** Implements all poultry farm management logic.
  - **data/**: Handles data sources (local/remote), repositories.
  - **domain/**: Business logic, use cases, domain models.
  - **ui/**: Compose screens and workflows (e.g., animal registry, growth, mortality, vaccination).
  - **di/**: Hilt dependency injection for feature components.

---

## Backend
- **Purpose:** REST API for price prediction and poultry-related services.
- **Stack:** Node.js, Express.js, Firebase Admin, Docker.
- **Key Files:**
  - `server.js`: Main API server.
  - `config/`, `middleware/`, `services/`: Configurations, middleware, and business logic.
  - `Dockerfile`, `docker-compose.yml`: Containerization.
  - `.env.example`: Environment variable template.
- **Testing:** Jest, Supertest.

---

## Cloud Functions
- **Purpose:** Serverless logic for marketplace and auction features.
- **Stack:** Parse Cloud Code (Node.js), deployed via `parse deploy`.
- **Key Files:**
  - `main.js`: Main cloud function entry.
  - `liveStreamingFunctions.js`: Live auction/streaming logic.
  - `security/`: Security rules/scripts.

---

## Common Terms & Patterns
- **MVVM:** Model-View-ViewModel architecture for UI logic separation.
- **Hilt:** Dependency injection framework for Android.
- **Jetpack Compose:** UI toolkit for building native Android UIs.
- **Repository Pattern:** Abstracts data access for testability and flexibility.
- **Use Case:** Encapsulates a single business action or workflow.
- **WorkManager:** Android background task scheduler.
- **Firebase:** Used for notifications (FCM), real-time data sync.
- **Parse:** Backend-as-a-Service for cloud logic and data storage.

---

## Development Practices
- **Modularity:** Each business area is encapsulated in its own module.
- **Localization:** Telugu and English, runtime switching.
- **Testing:** Unit and UI tests, Playwright for UI automation.
- **Performance:** Real-time monitoring and optimization for low-resource environments.
- **CI/CD:** Scripts for deployment, optimization, and maintenance.

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
