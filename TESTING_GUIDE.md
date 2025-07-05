# 🧪 ROOSTER TESTING GUIDE

**Comprehensive Testing Strategy for Krishna District Poultry Management System**

## 🎯 **TESTING OVERVIEW**

The Rooster testing strategy ensures enterprise-grade quality through multi-layered testing
approaches covering unit tests, integration tests, UI tests, and performance validation.

### **Testing Pyramid**

```
    🔺 E2E Tests (5%)
   🔺🔺 UI Tests (15%)
  🔺🔺🔺 Integration Tests (25%)
 🔺🔺🔺🔺 Unit Tests (55%)
```

---

## 🛠️ **TEST SETUP**

### **Prerequisites**

- **Android Studio Narwhal** (2025.1.1) or newer
- **JDK 11** or higher
- **Android SDK** with API levels 24-34
- **Firebase Test Lab** account (optional)

### **Quick Test Setup**

```bash
# Clone repository
git clone <repository>
cd rooster-poultry-management

# Run all tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Generate test reports
./gradlew jacocoTestReport
```

---

## 🧩 **UNIT TESTING**

### **Framework Configuration**

```kotlin
// build.gradle (Module: core-common)
dependencies {
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.6.1'
    testImplementation 'org.mockito.kotlin:mockito-kotlin:4.0.0'
    testImplementation 'kotlinx-coroutines-test:1.6.4'
    testImplementation 'app.cash.turbine:turbine:0.12.1'
}
```

### **Repository Testing**

```kotlin
// UserRepositoryTest.kt
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {
    
    @Mock
    private lateinit var remoteDataSource: UserRemoteDataSource
    
    @Mock
    private lateinit var localDataSource: UserLocalDataSource
    
    @Mock
    private lateinit var authService: AuthService
    
    private lateinit var repository: UserRepositoryImpl
    
    @Before
    fun setup() {
        repository = UserRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            authService = authService
        )
    }
    
    @Test
    fun `getCurrentUser returns user from local cache when available`() = runTest {
        // Given
        val expectedUser = createTestUser()
        whenever(localDataSource.getCurrentUser()).thenReturn(expectedUser)
        
        // When
        val result = repository.getCurrentUser().first()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        verify(localDataSource).getCurrentUser()
        verifyNoInteractions(remoteDataSource)
    }
    
    @Test
    fun `updateUser saves to both local and remote`() = runTest {
        // Given
        val user = createTestUser()
        whenever(remoteDataSource.updateUser(user)).thenReturn(Result.success(user))
        
        // When
        val result = repository.updateUser(user)
        
        // Then
        assertTrue(result.isSuccess)
        verify(localDataSource).saveUser(user)
        verify(remoteDataSource).updateUser(user)
    }
    
    private fun createTestUser() = User(
        id = "test_user_123",
        email = "test@rooster.com",
        phoneNumber = "+91-9876543210",
        role = UserRole.FARMER,
        profile = UserProfile(
            name = "Test Farmer",
            location = "Vijayawada"
        ),
        preferences = UserPreferences(),
        farmIds = listOf("farm_123")
    )
}
```

### **ViewModel Testing**

```kotlin
// FarmViewModelTest.kt
@RunWith(MockitoJUnitRunner::class)
class FarmViewModelTest {
    
    @Mock
    private lateinit var farmRepository: FarmRepository
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: FarmViewModel
    
    @Before
    fun setup() {
        viewModel = FarmViewModel(farmRepository)
    }
    
    @Test
    fun `loadFarms updates UI state with farm data`() = runTest {
        // Given
        val farms = listOf(createTestFarm())
        whenever(farmRepository.getCurrentUserFarms())
            .thenReturn(flowOf(Result.success(farms)))
        
        // When
        viewModel.loadFarms()
        
        // Then
        viewModel.uiState.test {
            val emission = awaitItem()
            assertEquals(farms, emission.farms)
            assertFalse(emission.isLoading)
            assertNull(emission.error)
        }
    }
    
    @Test
    fun `loadFarms handles error state correctly`() = runTest {
        // Given
        val error = Exception("Network error")
        whenever(farmRepository.getCurrentUserFarms())
            .thenReturn(flowOf(Result.failure(error)))
        
        // When
        viewModel.loadFarms()
        
        // Then
        viewModel.uiState.test {
            val emission = awaitItem()
            assertTrue(emission.farms.isEmpty())
            assertFalse(emission.isLoading)
            assertEquals("Network error", emission.error)
        }
    }
}
```

### **Use Case Testing**

```kotlin
// CreateFarmUseCaseTest.kt
class CreateFarmUseCaseTest {
    
    @Mock
    private lateinit var farmRepository: FarmRepository
    
    @Mock
    private lateinit var userRepository: UserRepository
    
    private lateinit var useCase: CreateFarmUseCase
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = CreateFarmUseCase(farmRepository, userRepository)
    }
    
    @Test
    fun `execute creates farm successfully`() = runTest {
        // Given
        val farmRequest = CreateFarmRequest(
            name = "Test Farm",
            location = Location(16.5062, 80.6480, "Vijayawada"),
            infrastructure = FarmInfrastructure(coops = 5, feedStorage = "10 tons")
        )
        val expectedFarm = Farm.fromRequest(farmRequest, "user_123")
        
        whenever(userRepository.getCurrentUserId()).thenReturn("user_123")
        whenever(farmRepository.createFarm(any())).thenReturn(Result.success(expectedFarm))
        
        // When
        val result = useCase.execute(farmRequest)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedFarm, result.getOrNull())
        verify(farmRepository).createFarm(any())
    }
}
```

---

## 🔗 **INTEGRATION TESTING**

### **Database Integration Tests**

```kotlin
// FarmDaoTest.kt
@RunWith(AndroidJUnit4::class)
@SmallTest
class FarmDaoTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: RoosterDatabase
    private lateinit var dao: FarmDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RoosterDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.farmDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetFarm() = runTest {
        // Given
        val farm = createTestFarmEntity()
        
        // When
        dao.insertFarm(farm)
        val retrievedFarm = dao.getFarmById(farm.id)
        
        // Then
        assertEquals(farm, retrievedFarm)
    }
    
    @Test
    fun getFarmsByOwner() = runTest {
        // Given
        val owner1Farms = listOf(
            createTestFarmEntity(id = "farm1", ownerId = "owner1"),
            createTestFarmEntity(id = "farm2", ownerId = "owner1")
        )
        val owner2Farm = createTestFarmEntity(id = "farm3", ownerId = "owner2")
        
        // When
        dao.insertAll(owner1Farms + owner2Farm)
        val result = dao.getFarmsByOwner("owner1")
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.ownerId == "owner1" })
    }
}
```

### **API Integration Tests**

```kotlin
// MarketplaceApiTest.kt
@RunWith(AndroidJUnit4::class)
@MediumTest
class MarketplaceApiTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: MarketplaceApiService
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(MarketplaceApiService::class.java)
    }
    
    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `getListings returns successful response`() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setBody(loadJsonFromAssets("marketplace_listings_response.json"))
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        
        // When
        val response = apiService.getListings("BIRDS", "Krishna", 1)
        
        // Then
        assertTrue(response.isSuccessful)
        val listings = response.body()?.data?.listings
        assertNotNull(listings)
        assertTrue(listings!!.isNotEmpty())
    }
    
    @Test
    fun `createListing sends correct request`() = runTest {
        // Given
        val listing = CreateListingRequest(
            product = ProductInfo(
                type = "BROILER_BIRDS",
                breed = "BROILER",
                quantity = 1000
            ),
            price = PriceInfo(amount = 200, currency = "INR", unit = "per_kg")
        )
        mockWebServer.enqueue(MockResponse().setResponseCode(201))
        
        // When
        apiService.createListing(listing)
        
        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/listings", request.path)
        
        val requestBody = Gson().fromJson(request.body.readUtf8(), CreateListingRequest::class.java)
        assertEquals(listing, requestBody)
    }
}
```

---

## 📱 **UI TESTING**

### **Compose UI Tests**

```kotlin
// FarmScreenTest.kt
@RunWith(AndroidJUnit4::class)
@LargeTest
class FarmScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    private lateinit var viewModel: FarmViewModel
    
    private val testFarms = listOf(
        Farm(
            id = "farm1",
            name = "Krishna Poultry Farm",
            location = Location(16.5062, 80.6480, "Vijayawada"),
            totalBirds = 5000
        )
    )
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }
    
    @Test
    fun farmScreen_displaysLoadingState() {
        // Given
        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(FarmUiState(isLoading = true))
        )
        
        // When
        composeTestRule.setContent {
            RoosterTheme {
                FarmScreen(viewModel = viewModel)
            }
        }
        
        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }
    
    @Test
    fun farmScreen_displaysFarmList() {
        // Given
        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(FarmUiState(farms = testFarms, isLoading = false))
        )
        
        // When
        composeTestRule.setContent {
            RoosterTheme {
                FarmScreen(viewModel = viewModel)
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Krishna Poultry Farm").assertIsDisplayed()
        composeTestRule.onNodeWithText("5000 birds").assertIsDisplayed()
    }
    
    @Test
    fun farmScreen_handlesRefreshAction() {
        // Given
        whenever(viewModel.uiState).thenReturn(
            MutableStateFlow(FarmUiState(farms = testFarms))
        )
        
        // When
        composeTestRule.setContent {
            RoosterTheme {
                FarmScreen(viewModel = viewModel)
            }
        }
        
        // Perform refresh
        composeTestRule.onNodeWithTag("refresh_button").performClick()
        
        // Then
        verify(viewModel).refreshFarms()
    }
}
```

### **Navigation Testing**

```kotlin
// NavigationTest.kt
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navController: TestNavHostController
    
    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            RoosterNavHost(navController = navController)
        }
    }
    
    @Test
    fun navHost_verifyStartDestination() {
        composeTestRule
            .onNodeWithContentDescription("Farm Dashboard")
            .assertIsDisplayed()
    }
    
    @Test
    fun navHost_navigateToMarketplace() {
        // Navigate to marketplace
        composeTestRule
            .onNodeWithStringId(R.string.marketplace)
            .performClick()
        
        // Verify destination
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("marketplace", route)
    }
}
```

---

## 📊 **PERFORMANCE TESTING**

### **Database Performance Tests**

```kotlin
// DatabasePerformanceTest.kt
@RunWith(AndroidJUnit4::class)
class DatabasePerformanceTest {
    
    private lateinit var database: RoosterDatabase
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RoosterDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
    
    @Test
    fun measureBulkInsertPerformance() = runTest {
        val farms = generateTestFarms(1000)
        
        val startTime = System.currentTimeMillis()
        database.farmDao().insertAll(farms)
        val endTime = System.currentTimeMillis()
        
        val duration = endTime - startTime
        println("Bulk insert of 1000 farms took: ${duration}ms")
        
        // Assert reasonable performance (adjust threshold as needed)
        assertTrue("Bulk insert took too long: ${duration}ms", duration < 5000)
    }
    
    @Test
    fun measureQueryPerformance() = runTest {
        // Insert test data
        val farms = generateTestFarms(10000)
        database.farmDao().insertAll(farms)
        
        val startTime = System.currentTimeMillis()
        val results = database.farmDao().searchFarmsByLocation("Krishna")
        val endTime = System.currentTimeMillis()
        
        val duration = endTime - startTime
        println("Query of 10000 farms took: ${duration}ms")
        
        assertTrue("Query took too long: ${duration}ms", duration < 1000)
        assertTrue("Should return results", results.isNotEmpty())
    }
}
```

### **Memory Usage Tests**

```kotlin
// MemoryLeakTest.kt
@RunWith(AndroidJUnit4::class)
class MemoryLeakTest {
    
    @Test
    fun farmViewModel_doesNotLeakMemory() {
        val mockRepository = mock<FarmRepository>()
        whenever(mockRepository.getCurrentUserFarms())
            .thenReturn(flowOf(Result.success(emptyList())))
        
        val initialMemory = Runtime.getRuntime().totalMemory()
        
        // Create and destroy multiple ViewModels
        repeat(100) {
            val viewModel = FarmViewModel(mockRepository)
            viewModel.loadFarms()
            // Simulate ViewModel cleanup
        }
        
        // Force garbage collection
        System.gc()
        Thread.sleep(100)
        
        val finalMemory = Runtime.getRuntime().totalMemory()
        val memoryIncrease = finalMemory - initialMemory
        
        // Assert memory increase is reasonable (adjust threshold as needed)
        assertTrue(
            "Memory leak detected: ${memoryIncrease} bytes",
            memoryIncrease < 10_000_000 // 10MB threshold
        )
    }
}
```

---

## 🚀 **E2E TESTING**

### **User Journey Tests**

```kotlin
// FarmerJourneyTest.kt
@RunWith(AndroidJUnit4::class)
@LargeTest
class FarmerJourneyTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun completeFarmerJourney() {
        // Login as farmer
        onView(withId(R.id.email_input))
            .perform(typeText("test.farmer@rooster.com"))
        
        onView(withId(R.id.password_input))
            .perform(typeText("TestPass123"))
        
        onView(withId(R.id.login_button))
            .perform(click())
        
        // Navigate to farm dashboard
        onView(withText("Farms"))
            .perform(click())
        
        // Create new farm
        onView(withId(R.id.add_farm_button))
            .perform(click())
        
        onView(withId(R.id.farm_name_input))
            .perform(typeText("Test Farm"))
        
        onView(withId(R.id.location_input))
            .perform(typeText("Test Location"))
        
        onView(withId(R.id.save_farm_button))
            .perform(click())
        
        // Verify farm was created
        onView(withText("Test Farm"))
            .check(matches(isDisplayed()))
        
        // Add flock to farm
        onView(withText("Test Farm"))
            .perform(click())
        
        onView(withId(R.id.add_flock_button))
            .perform(click())
        
        onView(withId(R.id.breed_spinner))
            .perform(click())
        
        onView(withText("Broiler"))
            .perform(click())
        
        onView(withId(R.id.quantity_input))
            .perform(typeText("1000"))
        
        onView(withId(R.id.save_flock_button))
            .perform(click())
        
        // Verify flock was added
        onView(withText("1000 Broiler"))
            .check(matches(isDisplayed()))
    }
}
```

### **Marketplace E2E Test**

```kotlin
// MarketplaceJourneyTest.kt
@RunWith(AndroidJUnit4::class)
@LargeTest
class MarketplaceJourneyTest {
    
    @Test
    fun buyerCanPurchaseFromMarketplace() {
        // Setup mock data
        setupMockMarketplaceData()
        
        // Login as buyer
        loginAs("buyer")
        
        // Navigate to marketplace
        navigateToMarketplace()
        
        // Search for products
        searchForProduct("Broiler")
        
        // Select a listing
        selectListing("Krishna Poultry Farm - Broiler")
        
        // Place order
        placeOrder(quantity = 100)
        
        // Complete payment
        completePayment()
        
        // Verify order confirmation
        verifyOrderConfirmation()
    }
}
```

---

## 🔧 **TEST UTILITIES**

### **Test Data Builders**

```kotlin
// TestDataBuilders.kt
object TestDataBuilders {
    
    fun createTestUser(
        id: String = "test_user_${UUID.randomUUID()}",
        role: UserRole = UserRole.FARMER,
        name: String = "Test User"
    ) = User(
        id = id,
        email = "test@rooster.com",
        phoneNumber = "+91-9876543210",
        role = role,
        profile = UserProfile(name = name, location = "Test Location"),
        preferences = UserPreferences(),
        farmIds = emptyList()
    )
    
    fun createTestFarm(
        id: String = "test_farm_${UUID.randomUUID()}",
        ownerId: String = "test_owner",
        name: String = "Test Farm"
    ) = Farm(
        id = id,
        name = name,
        ownerId = ownerId,
        location = Location(16.5062, 80.6480, "Test Location"),
        totalBirds = 1000,
        flocks = emptyList(),
        infrastructure = FarmInfrastructure(
            coops = 5,
            feedStorage = "10 tons",
            waterSystem = "Automated"
        )
    )
    
    fun createTestListing(
        id: String = "test_listing_${UUID.randomUUID()}",
        sellerId: String = "test_seller"
    ) = ProductListing(
        id = id,
        sellerId = sellerId,
        product = ProductInfo(
            type = "BROILER_BIRDS",
            breed = "BROILER",
            quantity = 1000,
            age = 42
        ),
        price = PriceInfo(amount = 180, currency = "INR", unit = "per_kg"),
        location = "Test Location",
        status = ListingStatus.ACTIVE,
        createdAt = Timestamp.now()
    )
}
```

### **Mock Extensions**

```kotlin
// MockExtensions.kt
fun MockWebServer.enqueueSuccess(jsonResponse: String) {
    enqueue(
        MockResponse()
            .setBody(jsonResponse)
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
    )
}

fun MockWebServer.enqueueError(code: Int, message: String) {
    enqueue(
        MockResponse()
            .setResponseCode(code)
            .setBody("""{"error": "$message"}""")
    )
}

fun loadJsonFromAssets(fileName: String): String {
    return javaClass.classLoader
        ?.getResourceAsStream("assets/$fileName")
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: throw IllegalArgumentException("File not found: $fileName")
}
```

---

## 📋 **TEST EXECUTION**

### **Running Tests**

```bash
# Unit tests only
./gradlew test

# Unit tests with coverage
./gradlew testDebugUnitTestCoverageVerification

# Integration tests
./gradlew connectedAndroidTest

# Specific module tests
./gradlew :core:core-common:test
./gradlew :feature:feature-auth:connectedAndroidTest

# Generate test report
./gradlew jacocoTestReport
open build/reports/jacoco/jacocoTestReport/html/index.html
```

### **CI/CD Integration**

```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - run: ./gradlew test jacocoTestReport
      - uses: codecov/codecov-action@v3
        with:
          file: build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
          
  instrumentation-tests:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - run: ./gradlew connectedAndroidTest
```

---

## 📊 **TEST METRICS**

### **Coverage Targets**

- **Unit Tests**: 80% line coverage minimum
- **Integration Tests**: 70% critical path coverage
- **UI Tests**: 60% user journey coverage
- **Overall**: 75% combined coverage

### **Performance Benchmarks**

- **Database queries**: < 100ms for simple operations
- **API calls**: < 2s response time
- **UI rendering**: < 16ms per frame
- **App startup**: < 3s cold start

### **Quality Gates**

- All tests must pass before merge
- Coverage decrease > 5% blocks deployment
- Performance regression > 20% requires investigation
- Memory leaks detected fail the build

---

**🧪 ROOSTER TESTING GUIDE**
**Ensuring Enterprise-Grade Quality Through Comprehensive Testing**

*Building reliable solutions for Krishna District's poultry community.*