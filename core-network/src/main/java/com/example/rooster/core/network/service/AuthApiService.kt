package com.example.rooster.core.network.service

import com.example.rooster.core.network.dto.LoginRequestDto
import com.example.rooster.core.network.dto.TokenResponseDto
import com.example.rooster.core.network.dto.UserRegistrationRequestDto
import com.example.rooster.core.network.dto.UserDto
import com.example.rooster.core.network.dto.RefreshTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body registrationRequest: UserRegistrationRequestDto): Response<UserDto>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): Response<TokenResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body refreshTokenRequest: RefreshTokenRequestDto): Response<TokenResponseDto>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>
}
