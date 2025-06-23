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

- **app/**: Main Android app (features, UI, DI, navigation)
- **core/**: Shared logic (core-common, core-network)
- **feature/**: Feature modules (feature-farm, etc.)
- **backend/**: Node.js REST API (price prediction, user data)
- **cloud/**: Parse Cloud Functions (marketplace, auctions)
- **tests/**: Playwright and other test suites
- **docs/**: Documentation

---

## 3. Data Flow Example

1. User interacts with Android app (e.g., records poultry data)
2. App sends REST API request to backend
3. Backend processes, stores, or forwards request to cloud functions
4. Cloud functions handle serverless logic (e.g., auction events)
5. Results/data returned to app; notifications sent via Firebase/FCM

---

## 4. Key Technologies
- **Android:** Kotlin, Jetpack Compose, Hilt, WorkManager
- **Backend:** Node.js, Express, Firebase Admin, Docker
- **Cloud:** Parse, Node.js
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
