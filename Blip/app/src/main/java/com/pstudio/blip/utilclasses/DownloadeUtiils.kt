package com.pstudio.blip.utilclasses

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
        cloudinaryUrl: String,
        iv: String,
        originalMime: String,
        originalName: String
    ): Result<File> = withContext(Dispatchers.IO) {

        val mediaIv = FileEncryptionUtil.decodeIv(iv)

        try {
            // Set target file path based on MIME type
            val targetDir = getStorageDirForMimeType(originalName, originalMime)
            val safeName = originalName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(originalMime) ?: "bin"
            val file = File(targetDir, if (safeName.contains(".")) safeName else "$safeName.$extension")

            // ✅ Skip download if file already exists
            if (file.exists()) {
                Log.d("CloudinaryDownload", "File already exists at ${file.absolutePath}")
                return@withContext Result.success(file)
            }

            // 1. Download encrypted file
            val request = Request.Builder().url(cloudinaryUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.d("CloudinaryDownload", "Download failed: ${response.code}")
                    return@withContext Result.failure(Exception("Download failed: ${response.code}"))
                }

                val encryptedBytes = response.body?.bytes() ?: run {
                    Log.d("CloudinaryDownload", "empty response")
                    return@withContext Result.failure(Exception("Empty response"))
                }

                Log.d("CloudinaryDownload", "Encrypted size: ${encryptedBytes.size}")

                // 2. Decrypt the file
                val decryptedBytes = FileEncryptionUtil.decryptFileFromBytes(encryptedBytes, mediaIv)
                Log.d("CloudinaryDownload", "Decrypted size: ${decryptedBytes.size}")

                // 3. Save to new path
                FileOutputStream(file).use { it.write(decryptedBytes) }

                // 4. Return saved file
                Result.success(file)
            }
        } catch (e: Exception) {
            Log.d("CloudinaryDownload", "Error: $e")
            Result.failure(e)
        }

    }

    fun getStorageDirForMimeType(name: String, mimeType: String): File {
        val baseDir = File(context.getExternalFilesDir(null), "Blip") // Must match file_paths.xml
        val type = when {
            mimeType.startsWith("image") -> "Images"
            mimeType.startsWith("video") -> "Videos"
            mimeType.startsWith("audio") -> "Audio"
            mimeType.startsWith("application") -> "Documents"
            else -> "Other"
        }
        return File(baseDir, type).apply { mkdirs() }

//        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
//        val safeName = name.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
//        val finalName = if (safeName.contains(".")) safeName else "$safeName.$extension"
//
//        val mediaTypeFolder = when {
//            mimeType.startsWith("image") -> "images"
//            mimeType.startsWith("video") -> "videos"
//            mimeType.startsWith("audio") -> "audio"
//            mimeType == "application/pdf" -> "documents"
//            else -> "others"
//        }
//
//        // Base: /storage/emulated/0/Blip/<mediaType>
//        val blipDir = File(Environment.getExternalStorageDirectory(), "Blip/$mediaTypeFolder").apply {
//            if (!exists()) mkdirs()
//        }
//
//        return File(blipDir, finalName).apply {
//            if (exists()) delete()
//        }

    }


}



