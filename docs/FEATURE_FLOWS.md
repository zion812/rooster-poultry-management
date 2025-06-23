# Rooster Poultry Management – Feature Flowcharts & User Journeys

---

## 1. Animal Registration Flow (Textual)

1. User opens the app and selects "Register Animal"
2. Fills in animal details (breed, age, weight, etc.)
3. Submits form
4. App sends data to backend via REST API
5. Backend validates and stores data
6. Confirmation sent to app; UI updates

---

## 2. Marketplace Auction Flow (Textual)

1. User navigates to "Marketplace"
2. Views list of active auctions
3. Selects an auction to view details
4. Places a bid
5. App sends bid to backend/cloud function
6. Cloud function processes bid, updates auction status
7. Real-time update sent to all participants

---

## 3. Vaccination Tracking Flow (Textual)

1. User selects "Vaccination" in farm management
2. Views list of animals due for vaccination
3. Marks animals as vaccinated
4. App updates backend and/or local database
5. Backend/cloud confirms update; UI reflects new status

---

*For visual diagrams, use PlantUML, draw.io, or Lucidchart. These textual flows can be used as a basis for creating sequence/activity diagrams in the future.*
