package com.civicfix.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token: String,
    val user: UserDto
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null
)
