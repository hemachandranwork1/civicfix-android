package com.civicfix.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.civicfix.data.local.dao.IssueDao
import com.civicfix.data.local.entity.IssueEntity

@Database(entities = [IssueEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun issueDao(): IssueDao
}
