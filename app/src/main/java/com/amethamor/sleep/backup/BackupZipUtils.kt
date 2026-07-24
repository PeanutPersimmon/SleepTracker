package com.amethamor.sleep.backup

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupZipUtils {

    fun zipDirectory(sourceDir: File, destinationUri: Uri, context: Context): Boolean {
        return try {
            val outputStream = context.contentResolver.openOutputStream(destinationUri) ?: return false
            outputStream.use {
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    zipDirectoryRecursive(sourceDir, sourceDir.name, zipOut)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun zipDirectoryRecursive(directory: File, baseName: String, zipOut: ZipOutputStream) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            val entryName = "$baseName/${file.name}"
            if (file.isDirectory) {
                zipDirectoryRecursive(file, entryName, zipOut)
            } else {
                FileInputStream(file).use { fis ->
                    BufferedInputStream(fis).use { bis ->
                        val zipEntry = ZipEntry(entryName)
                        zipOut.putNextEntry(zipEntry)
                        bis.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
        }
    }

    fun unzipFile(zipUri: Uri, destinationDir: File, context: Context): Boolean {
        return try {
            val destinationRoot = destinationDir.canonicalFile
            val inputStream = context.contentResolver.openInputStream(zipUri) ?: return false
            inputStream.use {
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry
                    while (entry != null) {
                        val file = File(destinationRoot, entry.name).canonicalFile
                        if (!file.isInside(destinationRoot)) {
                            throw SecurityException("Blocked unsafe zip entry: ${entry.name}")
                        }
                        if (entry.isDirectory) {
                            file.mkdirs()
                        } else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                BufferedOutputStream(fos).use { bos ->
                                    zipIn.copyTo(bos)
                                }
                            }
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteDirectory(directory: File): Boolean {
        return try {
            if (directory.isDirectory) {
                directory.listFiles()?.forEach { file ->
                    deleteDirectory(file)
                }
            }
            directory.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun copyFile(source: File, destination: File): Boolean {
        return try {
            val parent = destination.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }
            source.copyTo(destination, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun File.isInside(directory: File): Boolean {
        return this == directory || path.startsWith(directory.path + File.separator)
    }
}
