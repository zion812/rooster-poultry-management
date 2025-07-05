package com.example.rooster.core.common.performance

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring system for the Rooster Poultry Management app.
 * Tracks memory usage, performance metrics, and potential memory leaks.
 */
@Singleton
class PerformanceMonitor @Inject constructor() {

    private val monitoringScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    private val _memoryWarnings = MutableStateFlow<List<MemoryWarning>>(emptyList())
    val memoryWarnings: StateFlow<List<MemoryWarning>> = _memoryWarnings.asStateFlow()

    private var isMonitoring = false
    private val memoryThresholdMB = 100 // Warning threshold in MB

    /**
     * Starts performance monitoring
     */
    fun startMonitoring() {
        if (isMonitoring) return

        isMonitoring = true
        monitoringScope.launch {
            while (isMonitoring) {
                updatePerformanceMetrics()
                delay(MONITORING_INTERVAL_MS)
            }
        }
    }

    /**
     * Stops performance monitoring
     */
    fun stopMonitoring() {
        isMonitoring = false
    }

    /**
     * Updates current performance metrics
     */
    private fun updatePerformanceMetrics() {
        val runtime = Runtime.getRuntime()
        val totalMemoryMB = runtime.totalMemory() / 1024 / 1024
        val freeMemoryMB = runtime.freeMemory() / 1024 / 1024
        val usedMemoryMB = totalMemoryMB - freeMemoryMB
        val maxMemoryMB = runtime.maxMemory() / 1024 / 1024

        val metrics = PerformanceMetrics(
            totalMemoryMB = totalMemoryMB,
            usedMemoryMB = usedMemoryMB,
            freeMemoryMB = freeMemoryMB,
            maxMemoryMB = maxMemoryMB,
            memoryUsagePercentage = (usedMemoryMB.toDouble() / maxMemoryMB * 100).toFloat(),
            timestamp = System.currentTimeMillis()
        )

        _performanceMetrics.value = metrics

        // Check for memory warnings
        checkMemoryWarnings(metrics)
    }

    /**
     * Checks for memory-related warnings and issues
     */
    private fun checkMemoryWarnings(metrics: PerformanceMetrics) {
        val warnings = mutableListOf<MemoryWarning>()

        // High memory usage warning
        if (metrics.memoryUsagePercentage > 80) {
            warnings.add(
                MemoryWarning(
                    type = MemoryWarningType.HIGH_MEMORY_USAGE,
                    message = "Memory usage is at ${metrics.memoryUsagePercentage.toInt()}%",
                    severity = if (metrics.memoryUsagePercentage > 90) MemoryWarningSeverity.CRITICAL else MemoryWarningSeverity.HIGH,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Potential memory leak detection (simplified)
        if (metrics.usedMemoryMB > memoryThresholdMB) {
            val previousMetrics = _performanceMetrics.value
            if (previousMetrics.usedMemoryMB > 0 &&
                metrics.usedMemoryMB > previousMetrics.usedMemoryMB + 10
            ) { // 10MB increase
                warnings.add(
                    MemoryWarning(
                        type = MemoryWarningType.POTENTIAL_MEMORY_LEAK,
                        message = "Memory usage increased by ${metrics.usedMemoryMB - previousMetrics.usedMemoryMB}MB",
                        severity = MemoryWarningSeverity.MEDIUM,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }

        // Update warnings list (keep only recent warnings)
        val currentWarnings = _memoryWarnings.value.toMutableList()
        currentWarnings.addAll(warnings)

        // Keep only warnings from the last hour
        val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
        val recentWarnings = currentWarnings.filter { it.timestamp > oneHourAgo }

        _memoryWarnings.value = recentWarnings
    }

    /**
     * Forces garbage collection and memory cleanup
     */
    fun forceMemoryCleanup() {
        System.gc()
        updatePerformanceMetrics()
    }

    /**
     * Gets memory optimization suggestions
     */
    fun getMemoryOptimizationSuggestions(): List<OptimizationSuggestion> {
        val suggestions = mutableListOf<OptimizationSuggestion>()
        val metrics = _performanceMetrics.value

        if (metrics.memoryUsagePercentage > 70) {
            suggestions.add(
                OptimizationSuggestion(
                    title = "High Memory Usage",
                    description = "Consider clearing caches and unused data",
                    priority = OptimizationPriority.HIGH,
                    action = OptimizationAction.CLEAR_CACHES
                )
            )
        }

        if (_memoryWarnings.value.any { it.type == MemoryWarningType.POTENTIAL_MEMORY_LEAK }) {
            suggestions.add(
                OptimizationSuggestion(
                    title = "Potential Memory Leaks",
                    description = "Check for unclosed resources and strong references",
                    priority = OptimizationPriority.CRITICAL,
                    action = OptimizationAction.CHECK_MEMORY_LEAKS
                )
            )
        }

        return suggestions
    }

    /**
     * Clears all performance warnings
     */
    fun clearWarnings() {
        _memoryWarnings.value = emptyList()
    }

    companion object {
        private const val MONITORING_INTERVAL_MS = 5000L // 5 seconds
    }
}

/**
 * Current performance metrics
 */
data class PerformanceMetrics(
    val totalMemoryMB: Long = 0,
    val usedMemoryMB: Long = 0,
    val freeMemoryMB: Long = 0,
    val maxMemoryMB: Long = 0,
    val memoryUsagePercentage: Float = 0f,
    val timestamp: Long = 0
)

/**
 * Memory warning information
 */
data class MemoryWarning(
    val type: MemoryWarningType,
    val message: String,
    val severity: MemoryWarningSeverity,
    val timestamp: Long
)

/**
 * Types of memory warnings
 */
enum class MemoryWarningType {
    HIGH_MEMORY_USAGE,
    POTENTIAL_MEMORY_LEAK,
    LOW_AVAILABLE_MEMORY,
    GC_PRESSURE
}

/**
 * Severity levels for memory warnings
 */
enum class MemoryWarningSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Memory optimization suggestions
 */
data class OptimizationSuggestion(
    val title: String,
    val description: String,
    val priority: OptimizationPriority,
    val action: OptimizationAction
)

/**
 * Priority levels for optimization suggestions
 */
enum class OptimizationPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Types of optimization actions
 */
enum class OptimizationAction {
    CLEAR_CACHES,
    CHECK_MEMORY_LEAKS,
    RESTART_APP,
    OPTIMIZE_IMAGES,
    CLEANUP_DATABASE
}