package com.lifelog.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lifelog.app.domain.usecase.data.BackupDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupDataUseCase: BackupDataUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val result = backupDataUseCase.backup()
            if (result.isSuccess) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
