package com.amethamor.sleep.backup

import android.content.Context
import com.amethamor.sleep.data.SleepDatabase
import java.io.File

object DatabaseBackupHelper {

    fun getDatabaseFiles(context: Context): List<File> {
        val dbFile = context.getDatabasePath(SleepDatabase.DATABASE_NAME)
        val walFile = File("${dbFile.absolutePath}-wal")
        val shmFile = File("${dbFile.absolutePath}-shm")
        
        val files = mutableListOf(dbFile)
        if (walFile.exists()) files.add(walFile)
        if (shmFile.exists()) files.add(shmFile)
        
        return files
    }

    fun copyDatabaseFilesToDirectory(context: Context, targetDir: File): Boolean {
        return try {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            
            val dbFiles = getDatabaseFiles(context)
            for (file in dbFiles) {
                val destFile = File(targetDir, file.name)
                if (!BackupZipUtils.copyFile(file, destFile)) {
                    return false
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun copyDatabaseFilesFromDirectory(sourceDir: File, context: Context): Boolean {
        return try {
            val dbFiles = listOf(
                File(sourceDir, SleepDatabase.DATABASE_NAME),
                File(sourceDir, "${SleepDatabase.DATABASE_NAME}-wal"),
                File(sourceDir, "${SleepDatabase.DATABASE_NAME}-shm")
            )

            dbFiles.map { context.getDatabasePath(it.name) }.forEach { destFile ->
                if (destFile.exists() && !destFile.delete()) {
                    return false
                }
            }
            
            for (file in dbFiles) {
                if (file.exists()) {
                    val destFile = context.getDatabasePath(file.name)
                    destFile.parentFile?.mkdirs()
                    if (!BackupZipUtils.copyFile(file, destFile)) {
                        return false
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun backupCurrentDatabaseToDirectory(context: Context, backupDir: File): Boolean {
        return copyDatabaseFilesToDirectory(context, backupDir)
    }
}
