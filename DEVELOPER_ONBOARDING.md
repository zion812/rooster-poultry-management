# Rooster Poultry Management – Developer Onboarding Guide

---

## 1. Project Architecture Overview

```
rooster-poultry-management-3/
├── app/                # Main Android app (Kotlin, Compose, Hilt)
│   └── src/main/java/com/example/rooster/...
├── core/               # Shared logic (core-common, core-network, navigation, search, analytics)
├── feature/            # Feature modules (e.g., feature-farm, feature-marketplace, feature-community)
├── backend/            # Node.js REST API (price prediction, payment orchestration, Parse interaction)
├── cloud/              # Parse Cloud Code (auctions, live streaming, etc.) & Firebase Rules
├── tests/              # Playwright UI tests (web)
├── scripts/, docs/, tools/ # Automation, documentation, utilities
└── ...
```

---

## 2. Build & Run Instructions

### Android App
- **Requirements:** JDK 11+, Android Studio, Node.js (for backend/cloud/devtools)
- **Build:**
  1. Open project in Android Studio.
  2. Sync Gradle and build the app (`Build > Make Project`).
  3. Run on emulator or device (`Run > Run 'app'`).
- **Localization:** Telugu/English supported; switch via in-app settings.

### Backend (Node.js API)
- **Install:**
  1. `cd backend`
  2. `npm install`
- **Run:**
  - Production: `npm start`
  - Development (auto-reload): `npm run dev`
- **Test:** `npm test`
- **Docker:** Use `docker-compose up` for full stack deployment.

### Cloud Functions (Parse)
- **Install:**
  1. `cd cloud`
  2. `npm install`
- **Deploy:** `npm run deploy` (requires Parse CLI and credentials for Parse Cloud Code).
- **Note on Firebase Rules:** This directory also contains `firestore.rules` and `realtime-database.rules.json`. These Firebase security rules are typically deployed using the Firebase CLI (e.g., `firebase deploy --only firestore:rules`).

### Playwright UI Tests
- **Install:** `npm install` (in project root or as per Playwright docs)
- **Run:** `npx playwright test`
- **Config:** See `playwright.config.ts` (tests run headless, target `tests/` directory)

---

## 3. Contribution Guidelines
- **Branching:** Use feature branches for new work. Main branch is protected.
- **Commits:** Write clear, descriptive commit messages.
- **Code Style:** Follow Kotlin/Java best practices for Android, ESLint/Prettier for JS.
- **Testing:** Add/maintain tests for new features (Kotlin unit tests, Playwright for UI, Jest for backend).
- **Pull Requests:** Submit PRs with a clear description and reference related issues.
- **CI/CD:** Use provided scripts for building, testing, and deployment.

---

## 4. Testing Setup
- **Android:** Unit/UI tests in `app/src/test` and `app/src/androidTest`.
- **Backend:** Jest/Supertest (see `backend/package.json`).
- **Web UI:** Playwright (`tests/navigation.spec.ts` as example).
  - Sample: Checks visibility and navigation of Community, Fowl, Marketplace tabs, and Verify Transfer button.
  - Config: `playwright.config.ts` (headless, 60s timeout, viewport 1280x800).

---

## 5. Useful Scripts & Automation
- **Optimization:** Shell scripts for fixing imports, icons, compilation errors, and resource optimization.
- **Deployment:** `deploy-to-github.sh`, Docker, Parse CLI for cloud functions.

---

## 6. Support & Documentation
- **Dictionary:** See `PROJECT_DICTIONARY.md` for project terms and architecture.
- **Docs:** See `docs/` for additional guides and technical documentation.
- **Contact:** Use project GitHub issues or team communication channels for help.

---

Welcome to the Rooster Poultry Management project! Contribute, learn, and help rural farmers thrive with technology.
