package com.pstudio.blip.utilclasses

import android.content.Context
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CloudinaryDownloader(private val context: Context) {

    private val client = OkHttpClient()

    /**
     * Downloads, decrypts and saves a file
     * @param cloudinaryUrl Secure URL from Cloudinary
     * @param iv Initialization Vector (from upload context)
     * @param originalMime Original MIME type (from upload context)
     * @param originalName Original filename (from upload context)
     * @param callback Returns (success, localUri, error)
     */

    suspend fun downloadAndDecrypt(
        isSender: Boolean,
        cloudinaryUrl: String,
        iv: String,
        originalMime: String,
        originalName: String,
        onProgress: suspend (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {

        val mediaIv = FileEncryptionUtil.decodeIv(iv)

        try {
            val targetDir = getStorageDirForMimeType(isSender, originalName, originalMime)
            val safeName = originalName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(originalMime) ?: "bin"
            val file = File(targetDir, if (safeName.contains(".")) safeName else "$safeName.$extension")

            if (file.exists()) {
                Log.d("CloudinaryDownload", "File already exists at ${file.absolutePath}")
                return@withContext Result.success(file)
            }

            val request = Request.Builder().url(cloudinaryUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.d("CloudinaryDownload", "Download failed: ${response.code}")
                    return@withContext Result.failure(Exception("Download failed: ${response.code}"))
                }

                val contentLength = response.body?.contentLength() ?: -1L
                if (contentLength <= 0) {
                    return@withContext Result.failure(Exception("Invalid content length"))
                }

                val byteStream = ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var downloaded = 0L

                val inputStream = response.body?.byteStream()
                    ?: return@withContext Result.failure(Exception("Empty response"))

                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    byteStream.write(buffer, 0, read)
                    downloaded += read

                    // Emit progress
                    val progress = downloaded.toFloat() / contentLength
                    onProgress(progress)
                }

                val encryptedBytes = byteStream.toByteArray()
                Log.d("CloudinaryDownload", "Encrypted size: ${encryptedBytes.size}")

                val decryptedBytes = FileEncryptionUtil.decryptFileFromBytes(encryptedBytes, mediaIv)
                Log.d("CloudinaryDownload", "Decrypted size: ${decryptedBytes.size}")

                FileOutputStream(file).use { it.write(decryptedBytes) }

                Result.success(file)
            }
        } catch (e: Exception) {
            Log.d("CloudinaryDownload", "Error: $e")
            Result.failure(e)
        }
    }


    private fun getStorageDirForMimeType(isSender: Boolean, name: String, mimeType: String): File {
        val type = when {
            mimeType.startsWith("image") -> "Images"
            mimeType.startsWith("video") -> "Videos"
            mimeType.startsWith("audio") -> "Audio"
            mimeType.startsWith("application") -> "Documents"
            else -> "Other"
        }
        val baseDir = File(context.getExternalFilesDir(null), "Blip/$type")
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
        return baseDir

    }

}





