package com.example.rooster

import androidx.compose.runtime.saveable.listSaver

/**
 * State Serialization Utilities
 *
 * This file contains custom Savers and best practices for using rememberSaveable
 * with complex data types that aren't natively serializable to Bundle.
 *
 * Common Issue: IllegalArgumentException when using MutableState with complex objects
 * in rememberSaveable. The default SaveableStateRegistry can only serialize primitive
 * types and Parcelable objects to Bundle.
 */

/**
 * Custom Saver for ClosedFloatingPointRange<Float>
 *
 * Use this when you need to persist price ranges or similar float ranges
 * across configuration changes.
 *
 * Example usage:
 * ```
 * val priceRange by rememberSaveable(saver = FloatRangeSaver) { 0f..1000f }
 * ```
 *
 * Alternative approach (used in EnhancedSearchScreen):
 * Store start and end values separately:
 * ```
 * var priceRangeStart by rememberSaveable { mutableStateOf(0f) }
 * var priceRangeEnd by rememberSaveable { mutableStateOf(1000f) }
 * val priceRange = priceRangeStart..priceRangeEnd
 * ```
 */
val FloatRangeSaver =
    listSaver<ClosedFloatingPointRange<Float>, Float>(
        save = { range -> listOf(range.start, range.endInclusive) },
        restore = { list -> list[0]..list[1] },
    )

/**
 * Custom Saver for Set<String>
 *
 * Use this for saving sets of strings (like selected breeds, categories, etc.)
 *
 * Example usage:
 * ```
 * var selectedBreeds by rememberSaveable(saver = StringSetSaver) { setOf() }
 * ```
 */
val StringSetSaver =
    listSaver<Set<String>, String>(
        save = { set -> set.toList() },
        restore = { list -> list.toSet() },
    )

/**
 * Custom Saver for List of custom data classes
 *
 * Generic approach for saving lists of serializable data.
 * The data class should have simple properties (primitives, strings).
 */
inline fun <reified T> createListSaver(
    crossinline save: (T) -> List<Any?>,
    crossinline restore: (List<Any?>) -> T,
) = listSaver<List<T>, List<Any?>>(
    save = { list -> list.map { save(it) } },
    restore = { list -> list.map { restore(it) } },
)

/**
 * Best Practices for rememberSaveable:
 *
 * 1. PREFER PRIMITIVE TYPES: Use String, Int, Float, Boolean, Long directly
 * 2. AVOID COMPLEX OBJECTS: Don't store custom classes, ranges, collections directly
 * 3. DECOMPOSE COMPLEX STATE: Break down complex objects into simple properties
 * 4. USE CUSTOM SAVERS: For unavoidable complex types, create custom Savers
 * 5. CONSIDER `remember` INSTEAD: If persistence isn't needed, use `remember`
 *
 * WRONG ❌:
 * ```
 * var priceRange by rememberSaveable { mutableStateOf(0f..1000f) }
 * var selectedItems by rememberSaveable { mutableStateOf(setOf("item1", "item2")) }
 * var customObject by rememberSaveable { mutableStateOf(CustomClass()) }
 * ```
 *
 * CORRECT ✅:
 * ```
 * // Option 1: Decompose into simple types
 * var priceStart by rememberSaveable { mutableStateOf(0f) }
 * var priceEnd by rememberSaveable { mutableStateOf(1000f) }
 * val priceRange = priceStart..priceEnd
 *
 * // Option 2: Use custom Saver
 * var selectedItems by rememberSaveable(saver = StringSetSaver) { setOf() }
 *
 * // Option 3: Use remember if persistence not needed
 * var customObject by remember { mutableStateOf(CustomClass()) }
 * ```
 */

/**
 * Error Prevention Helper
 *
 * If you encounter "IllegalArgumentException: MutableState containing X cannot be saved",
 * this means you're trying to use a non-serializable type with rememberSaveable.
 *
 * Solutions:
 * 1. Check if the type is truly needed to be saved across config changes
 * 2. If yes, create a custom Saver or decompose into primitives
 * 3. If no, use `remember` instead of `rememberSaveable`
 * 4. For ranges specifically, use the pattern shown in EnhancedSearchScreen.kt
 */
