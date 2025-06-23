# Testing Guidelines for Rooster Poultry Management App

## Purpose
This document outlines conventions, best practices, and next steps for expanding and maintaining the test suite for the Android app.

---

## Directory Structure
- `app/src/test/java/` – Unit tests (JUnit, MockK/Mockito)
- `app/src/androidTest/java/` – UI/Instrumentation tests (Espresso, Compose)

---

## Conventions
- Test classes should match the name of the class under test, suffixed with `Test`.
- Use `@Test` for unit tests, `@RunWith(AndroidJUnit4::class)` for instrumentation tests.
- Use dependency injection and mocking for isolating units.

---

## Next Steps
1. Scaffold tests for all ViewModels, UseCases, and Repositories.
2. Add UI tests for major screens (Compose/Espresso).
3. Integrate JaCoCo for code coverage.
4. Run tests and coverage on CI.

---

## Example Unit Test
```kotlin
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleViewModelTest {
    @Test
    fun testExample() {
        assertEquals(4, 2 + 2)
    }
}
```

---

## Running Tests
- In Android Studio: Right-click test class or directory > Run
- CLI: `./gradlew test` (unit) or `./gradlew connectedAndroidTest` (UI)

---

## Code Coverage
- To be enabled via JaCoCo (see project build.gradle.kts)

---

*Keep this document updated as the test suite evolves.*
