package com.civicfix.domain.model

data class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val priority: String,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val voteCount: Int,
    val userId: Int,
    val reporterName: String?,
    val createdAt: String,
    val updatedAt: String?
)

data class TimelineEntry(
    val id: Int,
    val issueId: Int,
    val oldStatus: String?,
    val newStatus: String,
    val changedByName: String,
    val note: String?,
    val createdAt: String
)

data class Comment(
    val id: Int,
    val issueId: Int,
    val userId: Int,
    val content: String,
    val authorName: String,
    val createdAt: String
)
