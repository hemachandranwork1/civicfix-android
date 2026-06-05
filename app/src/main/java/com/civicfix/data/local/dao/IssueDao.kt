package com.civicfix.data.local.dao

import androidx.room.*
import com.civicfix.data.local.entity.IssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues ORDER BY createdAt DESC")
    fun getAllIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE userId = :userId ORDER BY createdAt DESC")
    fun getMyIssues(userId: Int): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE id = :id")
    suspend fun getIssueById(id: Int): IssueEntity?

    @Upsert
    suspend fun upsertAll(issues: List<IssueEntity>)

    @Upsert
    suspend fun upsert(issue: IssueEntity)

    @Query("DELETE FROM issues")
    suspend fun clearAll()
}
