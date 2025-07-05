package com.example.rooster.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches LoginCredentials in OpenAPI
@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

// Matches UserRegistration in OpenAPI
@Serializable
data class UserRegistrationRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("full_name") val fullName: String? = null
)

// Matches TokenResponse in OpenAPI
@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int
)

// Matches RefreshTokenInput in OpenAPI
@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)

// Matches User schema in OpenAPI (for /auth/me and register response)
@Serializable
data class UserDto(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("roles") val roles: List<String> = emptyList()
)

// Generic Error Response DTO - Matches ErrorResponse in OpenAPI
// This could be a top-level DTO if used by other API services as well.
// For now, placing it here for auth-related errors.
// Consider moving to a common `com.example.rooster.core.network.dto.common` package if widely used.
@Serializable
data class ApiErrorResponseDto(
    @SerialName("message") val message: String,
    @SerialName("error_code") val errorCode: String,
    @SerialName("details") val details: Map<String, List<String>>? = null // For validation errors primarily
)
