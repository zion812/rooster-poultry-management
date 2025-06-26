package com.example.rooster.core.common.models

/**
 * Data class for bid validation results or general input validation.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?, // Should be a resource ID or a non-localized key for later localization
)
