# Rooster Poultry Management – Onboarding FAQ & Troubleshooting

---

## 1. How do I set up the project locally?
- Clone the repo
- Install JDK 11+, Android Studio, Node.js (for backend/cloud)
- Run `./gradlew build` in project root
- For backend/cloud, run `npm install` in respective directories

## 2. How do I run the Android app?
- Open in Android Studio
- Sync Gradle and build
- Run on emulator or device (API 24+)

## 3. How do I run tests?
- Unit: `./gradlew test`
- UI: `./gradlew connectedAndroidTest`
- Backend: `npm test` in `backend/`
- Playwright: `npx playwright test`

## 4. How do I generate code coverage reports?
- Run `./gradlew jacocoTestReport` (see `build/jacocoHtml` for HTML report)

## 5. What if Gradle sync fails?
- Check JDK version (must be 11)
- Invalidate caches/restart Android Studio
- Run `./gradlew --refresh-dependencies`

## 6. How do I deploy the backend/cloud?
- Backend: `docker-compose up` or `npm start`
- Cloud: `npm run deploy` (Parse CLI required)

## 7. How do I add a new feature/module?
- Create a new directory in `feature/` or `core/`
- Register dependencies in DI modules
- Add tests and documentation

## 8. Who do I contact for help?
- Use GitHub issues or team communication channels

---

*Keep this FAQ updated as new onboarding or troubleshooting issues arise.*
