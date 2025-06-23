# Jetpack Compose Fixes for Rooster Poultry Management App

This document outlines the fixes required to resolve the Jetpack Compose compilation errors in the Rooster Poultry Management app.

## 1. Common Issues and Fixes

### A. Missing Imports
Add these imports at the top of your Compose files:

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.rooster.models.*
import com.example.rooster.ui.components.*
```

### B. Composable Function Requirements
Ensure all functions using Compose UI elements are annotated with `@Composable`:

```kotlin
@Composable
fun MyComponent() {
    // Compose UI code here
}
```

### C. Modifier Usage
When using `weight()`, always use it with Modifier:

```kotlin
// Incorrect
weight(1f)

// Correct
Modifier.weight(1f)
```

### D. Type Inference Issues
For generic functions, specify type parameters explicitly:

```kotlin
// Incorrect
someFunction()

// Correct
someFunction<String>()
```

## 2. Specific File Fixes

### CartScreen.kt
1. Add missing imports:
```kotlin
import com.example.rooster.models.*
import com.example.rooster.ui.components.StandardScreenLayout
```

2. Ensure all functions are marked as @Composable:
```kotlin
@Composable
fun CartItemCard(...) {
    // ... UI code
}
```

### ChatScreen.kt
1. Add missing imports:
```kotlin
import com.example.rooster.models.*
import com.example.rooster.ui.components.StandardScreenLayout
```

2. Fix weight modifier:
```kotlin
// Before
weight(1f)

// After
Modifier.weight(1f)
```

### DiagnosticsScreen.kt
1. Add missing imports:
```kotlin
import com.example.rooster.models.*
import com.example.rooster.ui.components.StandardScreenLayout
```

2. Ensure proper Composable usage:
```kotlin
@Composable
fun DiagnosticsScreen() {
    StandardScreenLayout() {
        // ... UI code
    }
}
```

### FarmerHomeScreenFixed.kt
1. Add missing imports:
```kotlin
import com.example.rooster.models.*
import com.example.rooster.ui.components.StandardScreenLayout
```

2. Fix item references:
```kotlin
// Before
item()

// After
items(items = listOf()) { item ->
    // ... UI code
}
```

## 3. Common Error Patterns

### Error: Unresolved reference 'StandardScreenLayout'
- Fix: Add import `import com.example.rooster.ui.components.StandardScreenLayout`

### Error: @Composable invocations can only happen from the context of a @Composable function
- Fix: Ensure the parent function is marked with `@Composable`

### Error: Expression 'weight' of type 'kotlin.Float' cannot be invoked as a function
- Fix: Use `Modifier.weight()` instead of just `weight()`

### Error: Not enough information to infer type argument
- Fix: Specify the type parameter explicitly

## 4. Best Practices

1. Always add `@Composable` annotation to functions that use Compose UI
2. Use proper Modifier chains
3. Specify type parameters when needed
4. Keep imports organized and complete
5. Follow consistent naming conventions

## 5. Verification Steps

After making these fixes:
1. Clean and rebuild the project
2. Check for remaining compilation errors
3. Test the UI components
4. Verify that all screens render correctly
5. Test navigation between screens

If issues persist, check:
1. Gradle dependencies
2. Kotlin version compatibility
3. Material3 theme setup
4. Component tree structure
