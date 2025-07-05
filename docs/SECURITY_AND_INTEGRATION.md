# Rooster Poultry Management – Security & Integration Overview

---

## 1. Authentication & Authorization
- **Mobile App:** Primarily uses Firebase Authentication for user sign-in and ID token generation.
- **Backend (`backend/` - Node.js/Express):** Protected API endpoints (e.g., for price prediction, payment order creation) verify Firebase ID Tokens sent in the `Authorization` header using the `firebase-admin` SDK.
- **Cloud Functions (`cloud/` - Parse Cloud Code):** Parse Cloud Functions can leverage the authenticated Parse User (`request.user`) for authorization. For functions called by the authenticated mobile app (if the app also authenticates with Parse directly) or by the `backend/` server (which might use a master key or specific roles), Parse Class-Level Permissions (CLPs) and Access Control Lists (ACLs) on Parse Objects are the primary mechanisms for enforcing user roles and permissions.

---

## 2. Data Privacy & Compliance
- Sensitive data (user info, animal records) is encrypted in transit (HTTPS) and at rest (Firebase/DB).
- No hardcoded secrets in codebase; use `.env` files and secret managers.
- Regularly audit dependencies for vulnerabilities.

---

## 3. Rate Limiting & Input Validation
- **Backend:** Uses middleware (e.g., `express-rate-limit`, `joi`) to prevent abuse and validate inputs.
- **Cloud:** Validate all user input before processing auction/marketplace actions.

---

## 4. Integration Flows
- **App ↔ Firebase Services (Direct):** App directly interacts with Firebase Auth, Firestore, Realtime Database, and Storage for features like farm management, marketplace listings, community content, and image uploads. Access is controlled by Firebase Security Rules.
- **App ↔ Custom Backend (`backend/` - Node.js):** App sends REST API requests (HTTPS) to the Node.js backend for services like price prediction and payment orchestration. These requests are authenticated using Firebase ID Tokens.
- **Custom Backend (`backend/`) ↔ Parse Platform (Back4App):** The Node.js backend uses the Parse SDK (or direct REST calls via `axios` as seen in `parseService.js`) to interact with the Parse database (e.g., for historical price data) and potentially invoke Parse Cloud Functions. This communication would typically use Parse Application ID and REST API Key / Master Key.
- **Custom Backend (`backend/`) ↔ Razorpay:** The Node.js backend integrates with Razorpay SDK for creating payment orders and verifying signatures. Razorpay webhooks are also received by this backend.
- **App ↔ Parse Cloud Functions (`cloud/`):** While not explicitly detailed for all features, the app could potentially call Parse Cloud Functions directly if it also manages a Parse User session. The auction system heavily relies on Parse Cloud Code.
- **Notifications:** Firebase Cloud Messaging (FCM) for push notifications, likely triggered by backend services or Cloud Functions.

---

## 5. Security Best Practices
- Rotate API keys and secrets regularly.
- Use HTTPS/TLS everywhere.
- Principle of least privilege for all roles and service accounts.
- Log and monitor authentication, errors, and suspicious activity.

---

## 6. TODO / Next Steps
- Document detailed auth flows (diagrams, sequence charts).
- Add automated security scanning to CI pipeline.
- Expand security rules for cloud and backend as needed.
- Review and update privacy policy for compliance.
