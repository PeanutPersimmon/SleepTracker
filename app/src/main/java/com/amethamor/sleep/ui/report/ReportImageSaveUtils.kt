package com.amethamor.sleep.ui.report

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

fun saveBitmapToPictures(
    context: Context,
    bitmap: Bitmap,
    fileName: String
): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveWithMediaStore(context, bitmap, fileName)
    } else {
        saveToAppPictures(context, bitmap, fileName)
    }
}

private fun saveWithMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/SleepReports")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ?: error("Failed to create MediaStore item")
    resolver.openOutputStream(uri)?.use { stream ->
        check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
    } ?: error("Failed to open output stream")
    values.clear()
    values.put(MediaStore.Images.Media.IS_PENDING, 0)
    resolver.update(uri, values, null, null)
    return uri
}

private fun saveToAppPictures(context: Context, bitmap: Bitmap, fileName: String): Uri {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SleepReports")
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, fileName)
    file.outputStream().use { stream ->
        check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
    }
    return Uri.fromFile(file)
}
