package com.civicfix.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.civicfix.data.local.dao.IssueDao
import com.civicfix.data.local.entity.IssueEntity
import com.civicfix.data.remote.ApiService
import com.civicfix.data.remote.models.CommentRequest
import com.civicfix.data.remote.models.IssueDto
import com.civicfix.domain.model.Issue
import com.civicfix.domain.model.TimelineEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val api:       ApiService,
    private val dao:       IssueDao,
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) {
    val allIssues: Flow<List<Issue>> =
        dao.getAllIssues().map { list -> list.map { it.toDomain() } }

    fun myIssues(userId: Int): Flow<List<Issue>> =
        dao.getMyIssues(userId).map { list -> list.map { it.toDomain() } }

    suspend fun refreshIssues(
        category: String? = null,
        status:   String? = null,
        search:   String? = null
    ) {
        val issues = api.getIssues(category, status, search)
        dao.upsertAll(issues.map { it.toEntity() })
    }

    suspend fun refreshMyIssues() {
        val issues = api.getMyIssues()
        dao.upsertAll(issues.map { it.toEntity() })
    }

    suspend fun getIssueDetail(id: Int): Result<Issue> {
        return try {
            val dto = api.getIssue(id)
            dao.upsert(dto.toEntity())
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            val cached = dao.getIssueById(id)
            if (cached != null) Result.success(cached.toDomain())
            else Result.failure(e)
        }
    }

    suspend fun getTimeline(id: Int): Result<List<TimelineEntry>> {
        return try {
            val list = api.getTimeline(id)
            Result.success(list.map {
                TimelineEntry(
                    it.id, it.issueId, it.oldStatus,
                    it.newStatus, it.changedByName, it.note, it.createdAt
                )
            })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createIssue(
        title:       String,
        description: String,
        category:    String,
        lat:         Double,
        lng:         Double,
        address:     String,
        imageFile:   File?
    ): Result<Issue> {
        return try {
            val toRb = { s: String -> s.toRequestBody("text/plain".toMediaType()) }
            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "image", it.name,
                    it.asRequestBody("image/*".toMediaType())
                )
            }
            val dto = api.createIssue(
                toRb(title), toRb(description), toRb(category),
                toRb(lat.toString()), toRb(lng.toString()), toRb(address),
                imagePart
            )
            dao.upsert(dto.toEntity())
            Result.success(dto.toDomain())
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun voteIssue(id: Int): Result<Boolean> {
        return try {
            val resp = api.voteIssue(id)
            Result.success(resp.voted)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addComment(issueId: Int, content: String): Result<Unit> {
        return try {
            api.addComment(issueId, CommentRequest(content))
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}

private fun IssueDto.toEntity() = IssueEntity(
    id, title, description, category, status, priority,
    imageUrl, latitude, longitude, address, voteCount, userId,
    reporterName, createdAt, updatedAt
)

private fun IssueDto.toDomain() = Issue(
    id, title, description, category, status, priority,
    imageUrl, latitude, longitude, address, voteCount, userId,
    reporterName, createdAt, updatedAt
)

private fun IssueEntity.toDomain() = Issue(
    id, title, description, category, status, priority,
    imageUrl, latitude, longitude, address, voteCount, userId,
    reporterName, createdAt, updatedAt
)
