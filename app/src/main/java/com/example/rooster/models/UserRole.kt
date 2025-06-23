package com.example.rooster.models

/**
 * Defines the different user roles within the application.
 */
enum class UserRole {
    FARMER,
    GENERAL, // General marketplace user
    HIGH_LEVEL, // Admin or other privileged user
    UNKNOWN; // Default or error state

    companion object {
        /**
         * Converts a string to a UserRole enum.
         * Defaults to UNKNOWN if the string does not match any role.
         */
        fun fromString(role: String?): UserRole {
            return when (role?.lowercase()) {
                "farmer" -> FARMER
                "general" -> GENERAL
                "high_level", "highlevel" -> HIGH_LEVEL // Allow for variations
                else -> UNKNOWN
            }
        }
    }
}
