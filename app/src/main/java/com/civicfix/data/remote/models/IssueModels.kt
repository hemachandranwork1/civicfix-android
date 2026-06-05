package com.civicfix.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IssueDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val priority: String,
    @Json(name = "image_url")    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    @Json(name = "vote_count")   val voteCount: Int = 0,
    @Json(name = "user_id")      val userId: Int,
    @Json(name = "reporter_name") val reporterName: String? = null,
    @Json(name = "created_at")   val createdAt: String,
    @Json(name = "updated_at")   val updatedAt: String? = null,
    val comments: List<CommentDto>? = null
)

@JsonClass(generateAdapter = true)
data class CommentDto(
    val id: Int,
    @Json(name = "issue_id")    val issueId: Int,
    @Json(name = "user_id")     val userId: Int,
    val content: String,
    @Json(name = "author_name") val authorName: String,
    @Json(name = "created_at")  val createdAt: String
)

@JsonClass(generateAdapter = true)
data class TimelineDto(
    val id: Int,
    @Json(name = "issue_id")        val issueId: Int,
    @Json(name = "old_status")      val oldStatus: String? = null,
    @Json(name = "new_status")      val newStatus: String,
    @Json(name = "changed_by_name") val changedByName: String,
    val note: String? = null,
    @Json(name = "created_at")      val createdAt: String
)

@JsonClass(generateAdapter = true)
data class VoteResponse(val voted: Boolean)

@JsonClass(generateAdapter = true)
data class CommentRequest(val content: String)
