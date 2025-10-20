package com.pstudio.blip.utilclasses

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import com.pstudio.blip.data.CopiedFile
import java.io.File
import java.io.FileOutputStream


fun copyFileToCustomDirectory(
    context: Context,
    uri: Uri,
    mediaType: String,
    flag: String = ""
): CopiedFile? {
    try {
        val resolver = context.contentResolver

        // Get original file name
        val originalName = getFileNameFromUri(context, uri) ?: return null

        val downloadsFolder = if (flag.isEmpty())
            File(context.getExternalFilesDir(null), "Blip/$mediaType")
        else
            File(Environment.getExternalStorageDirectory(), "Blip/$mediaType")

        if (!downloadsFolder.exists()) {
            downloadsFolder.mkdirs()
        }

        val destFile = File(downloadsFolder, originalName)

        resolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Get URI from file
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            destFile
        )

        return CopiedFile(destFile, fileUri)

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("FileCopy", "Error copying file: ${e.message}")
        return null
    }
}


fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                result = cursor.getString(nameIndex)
            }
        }
    }
    if (result == null) {
        result = uri.path?.substringAfterLast('/')
    }
    return result
}

fun isFileSizeWithinLimit(context: Context, uri: Uri, maxSizeInMB: Int = 10): Boolean {
    val fileSizeInBytes = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
        it.length
    } ?: return false

    val fileSizeInMB = fileSizeInBytes / (1024 * 1024)
    return fileSizeInMB <= maxSizeInMB
}

fun uriToTempFile(context: Context, uri: Uri, onSuccess: (success: Boolean, file: File?) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return
        val tempFile = File.createTempFile("temp_", null, context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        onSuccess(true, tempFile)
    } catch (e: Exception) {
        onSuccess(false, null)
    }
}