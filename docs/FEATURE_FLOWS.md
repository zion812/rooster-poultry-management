# Rooster Poultry Management – Feature Flowcharts & User Journeys

---

## 1. Farm Animal Registration Flow (`feature-farm`) (Textual)

1. User opens the app and navigates to the animal/flock registration section within `feature-farm`.
2. Fills in animal details (breed, age, weight, parentage for lineage, etc.) via `FlockRegistryScreen`.
3. Submits form; `FlockRegistryViewModel` calls `RegisterFlockUseCase`.
4. `FarmRepository` saves data to local Room database (`FlockEntity` with `needsSync=true`).
5. `FarmRepository` attempts to immediately sync data to Firebase (Firestore/Realtime DB via `FirebaseFarmDataSource`).
6. If direct sync fails, `FarmDataSyncWorker` picks up the unsynced data later for background synchronization.
7. Confirmation (typically of local save) sent to UI; UI updates.

---

## 2. Marketplace Listing Creation Flow (`feature-marketplace`) (Textual)

1. User navigates to "Create Listing" in `feature-marketplace`.
2. Fills in listing details (title, description, price, category, poultry specifics) via `CreateListingScreen`.
3. Selects images for the listing; `CreateListingViewModel` uses `ImageUploadService`.
4. `ImageUploadService` (implemented in `:app` module by `FirebaseStorageImageUploadService`) uploads images to Firebase Storage.
5. User submits form; `CreateListingViewModel` calls `ProductListingRepository`.
6. `ProductListingRepository` saves listing data (including image URLs from Firebase Storage) to local Room database (`ProductListingEntity` with `needsSync=true`).
7. `ProductListingRepository` attempts to immediately sync data to Firebase (Firestore via `FirebaseMarketplaceDataSource`).
8. If direct sync fails, a future sync mechanism (e.g., a dedicated worker or manual sync trigger) would handle it.
9. Confirmation (typically of local save and image upload start) sent to UI; UI updates.

---

## 3. Marketplace Auction Flow (Parse Cloud Code) (Textual)

1. User navigates to "Marketplace" and views listings. Listings enabled for auction are indicated.
2. User selects an auction-enabled listing to view details, including auction status (current bid, end time).
3. User places a bid via the app.
4. App sends bid to Parse Cloud Code (`placeEnhancedAuctionBid` function).
5. Parse Cloud Code function:
    a. Validates the bid (auction active, bid amount valid, handles deposits if required).
    b. Records the bid in Parse DB (`EnhancedAuctionBid` class).
    c. Updates the auction's current highest bid and bid count in Parse DB (`EnhancedAuction` class).
    d. May trigger real-time updates to participants (e.g., via Parse LiveQuery or push notifications).
6. Auction completion (`processAuctionCompletion`, `processWinnerPayment` Cloud Functions):
    a. Determines winner, handles payment status (interfacing with backend API payment status).
    b. Manages bidder deposits (forfeiture/refund).
    c. Updates auction status to SETTLED or similar.

---

## 4. Vaccination Tracking Flow (`feature-farm`) (Textual)

1. User selects "Vaccination" in farm management (`feature-farm`).
2. Views list of animals, potentially with vaccination status or due dates.
3. User records a new vaccination for a flock/animal via `VaccinationScreen`.
4. `VaccinationViewModel` calls relevant use case (e.g., `SaveVaccinationRecordsUseCase`).
5. Repository saves data to local Room database (`VaccinationEntity` with `needsSync=true`). (Note: `needsSync` was added; full sync logic for vaccinations may still be TODO).
6. Repository attempts to sync data to Firebase.
7. If direct sync fails, a background worker (similar to `FarmDataSyncWorker`, or an extension of it) would handle synchronization.
8. UI updates to reflect the new vaccination record.

---

*For visual diagrams, use PlantUML, draw.io, or Lucidchart. These textual flows can be used as a basis for creating sequence/activity diagrams in the future.*
