package com.lifelog.app.domain.usecase.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.lifelog.app.data.local.dao.RecordDao
import com.lifelog.app.data.local.dao.CategoryDao
import com.lifelog.app.data.local.dao.ActivityDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordDao: RecordDao,
    private val categoryDao: CategoryDao,
    private val activityDao: ActivityDao
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportToJson(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val categories = categoryDao.getAllOnce()
            val activities = activityDao.getAllOnce()
            val records = recordDao.getAllRecords()
            val exportDir = File(context.getExternalFilesDir(null), "lifelog_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val file = File(exportDir, "lifelog_export_$timestamp.json")

            val exportData = mapOf(
                "version" to "1.0.0",
                "exportTime" to LocalDateTime.now().toString(),
                "categories" to categories,
                "activities" to activities,
                "records" to records
            )

            file.writeText(gson.toJson(exportData))
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class BackupDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun backup(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath("lifelog.db")
            if (!dbFile.exists()) return@withContext Result.failure(Exception("数据库文件不存在"))

            val backupDir = File(context.getExternalFilesDir(null), "lifelog_backups")
            if (!backupDir.exists()) backupDir.mkdirs()

            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val backupFile = File(backupDir, "lifelog_backup_$timestamp.db")

            dbFile.copyTo(backupFile, overwrite = true)

            // 同时备份 WAL 和 SHM 文件
            val walFile = File(dbFile.parent, "${dbFile.name}-wal")
            val shmFile = File(dbFile.parent, "${dbFile.name}-shm")
            if (walFile.exists()) walFile.copyTo(File(backupDir, "${backupFile.name}-wal"), overwrite = true)
            if (shmFile.exists()) shmFile.copyTo(File(backupDir, "${backupFile.name}-shm"), overwrite = true)

            Result.success(backupFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restore(backupPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backupFile = File(backupPath)
            if (!backupFile.exists()) return@withContext Result.failure(Exception("备份文件不存在"))

            val dbFile = context.getDatabasePath("lifelog.db")
            backupFile.copyTo(dbFile, overwrite = true)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBackupFiles(): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "lifelog_backups")
        return if (backupDir.exists()) {
            backupDir.listFiles()?.filter { it.extension == "db" }?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        } else emptyList()
    }
}
