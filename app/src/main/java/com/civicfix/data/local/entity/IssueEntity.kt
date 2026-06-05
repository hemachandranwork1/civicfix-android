package com.civicfix.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey val id: Int,
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
