package com.civicfix.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatarUrl: String? = null,
    val createdAt: String? = null
)
