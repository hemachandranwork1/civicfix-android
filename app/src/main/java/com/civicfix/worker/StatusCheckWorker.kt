package com.civicfix.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.civicfix.data.remote.ApiService
import com.civicfix.data.repository.AuthRepository
import com.civicfix.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

@HiltWorker
class StatusCheckWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val api:      ApiService,
    private val authRepo: AuthRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        authRepo.token.firstOrNull() ?: return Result.success()
        return try {
            val issues = api.getMyIssues()
            val prefs  = context.getSharedPreferences("worker_cache", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            issues.forEach { issue ->
                val key    = "status_${issue.id}"
                val cached = prefs.getString(key, null)
                if (cached != null && cached != issue.status) {
                    NotificationHelper.showStatusUpdate(
                        context, issue.title, issue.status, issue.id
                    )
                }
                editor.putString(key, issue.status)
            }
            editor.apply()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<StatusCheckWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "status_check",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
