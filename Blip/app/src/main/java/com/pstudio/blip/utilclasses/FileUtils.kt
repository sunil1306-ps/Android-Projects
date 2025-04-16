package com.pstudio.blip.utilclasses

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun openDecryptedFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }

    context.startActivity(intent)
}

fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val targetDir = File(context.filesDir, "Blip/Media") // or your preferred location
        if (!targetDir.exists()) targetDir.mkdirs()

        val safeName = fileName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
        val targetFile = File(targetDir, safeName)

        FileOutputStream(targetFile).use { output ->
            inputStream.copyTo(output)
        }

        targetFile // Return the copied file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}