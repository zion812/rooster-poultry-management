# Rooster Poultry Management – Architecture Overview

---

## 1. System Diagram (Textual)

```
+-------------------+          +-------------------+          +--------------------+
|    Android App    | <------> |     Backend API   | <------> |   Cloud Functions  |
| (Kotlin, Compose) |   REST   |  (Node.js, REST)  |  events  | (Parse, Auctions)  |
+-------------------+          +-------------------+          +--------------------+
       |  ^                      |  ^                      |  ^
       |  |                      |  |                      |  |
    Firebase, FCM             Docker, DB                Parse DB, Security
```

---

## 2. Module Breakdown

- **app/**: Main Android application module. Responsible for initializing global services (Hilt, Parse SDK, Firebase), setting up the main navigation graph (integrating feature module graphs), providing concrete implementations for core service interfaces (e.g., `FirebaseUserIdProvider`, `FirebaseStorageImageUploadService`), and currently hosting some UI screens/logic not yet fully modularized.
- **core/**: Shared libraries for common utilities (`core-common`), networking (`core-network`), navigation contracts (`core:navigation`), analytics (`core:analytics`), and search (`core:search`).
- **feature/**: Self-contained feature modules like `feature-farm`, `feature-marketplace`, `feature-community`, each with its own data, domain, and presentation layers.
- **backend/**: Node.js REST API server using Express.js. This is the primary custom backend for specialized services like poultry price prediction (using data from Parse) and payment orchestration (via Razorpay). It authenticates requests using Firebase ID tokens (verified with `firebase-admin`).
- **cloud/**: Houses Parse Cloud Code (`main.js`, `liveStreamingFunctions.js`) running on Back4App for serverless logic such as auctions, live streaming, token/coin system, and various utility functions. Also contains Firebase security rules (`firestore.rules`, `realtime-database.rules.json`) for data accessed directly by the app from Firebase.
- **tests/**: Test suites, including Playwright for UI tests.
- **docs/**: Project documentation.

---

## 3. Data Flow Example

1.  **Offline-first data (e.g., `feature-farm`):** User records data -> Saved to local Room DB (`needsSync=true`) -> `FarmDataSyncWorker` uploads to Firebase (Firestore/RTDB).
2.  **Price Prediction:** App requests prediction from `backend/` (Node.js API) with Firebase JWT -> `backend/` authenticates token, calls `pricePredictor.js` -> `pricePredictor.js` uses `parseService.js` to fetch historical data from Parse (Back4App) -> Prediction returned to app.
3.  **Payment Processing:** App requests order creation from `backend/` -> `backend/` uses `razorpayService.js` to create Razorpay order -> App completes payment with Razorpay SDK -> App sends verification data to `backend/` -> `backend/` verifies signature. Razorpay webhooks also hit `backend/`.
4.  **Other Operations:** For some features, the app might interact directly with Firebase services (e.g., `feature-marketplace` listings on Firestore). The `backend/` can also interact with Firebase Admin SDK for its own needs or delegate tasks to Parse Cloud Functions (`cloud/`).
5.  **Notifications:** Firebase Cloud Messaging (FCM) is used for push notifications.
6. Results/data are returned to the app; notifications can be sent via Firebase Cloud Messaging (FCM).

---

## 4. Key Technologies
- **Android:** Kotlin, Jetpack Compose, Hilt (DI), WorkManager (background tasks), Room (local DB), Retrofit/OkHttp (networking), Coil (image loading).
- **Backend (Primary API):** Node.js, Express.js, Docker.
- **Cloud Services:**
    - **Firebase:** Firestore, Realtime Database, Authentication, Storage, FCM, Crashlytics, Analytics. Security rules for these are managed alongside cloud code.
    - **Parse Platform (Back4App):** SDK initialized in app. Parse Cloud Code (`main.js`, `liveStreamingFunctions.js` in `cloud/`) runs serverless logic for auctions, live streaming, token system, etc., interacting with its own Parse Database.
- **Testing:** JUnit, Espresso/Compose, Jest, Playwright
- **CI/CD:** GitHub Actions

---

## 5. Security & Compliance
- Auth via Firebase or custom JWT
- Rate limiting and input validation on backend
- Security rules in cloud functions

---

## 6. Extending the System
- Add new features as modules in `feature/`
- Expand backend endpoints as needed
- Add cloud functions for new marketplace/auction logic

---

*For detailed diagrams, see the docs/ directory or request a specific diagram (UML, sequence, etc.).*
