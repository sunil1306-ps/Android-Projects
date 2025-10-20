import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.pstudio.blip.utilclasses.FileEncryptionUtil
import com.pstudio.blip.utilclasses.getFileNameFromUri
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CloudinaryUploader {

    /**
     * Uploads ByteArray to Cloudinary and returns URL via callback
     * @param context Android context
     * @param fileBytes ByteArray of the file
     * @param mimeType MIME type (e.g., "image/jpeg")
     * @param unsignedPreset Your Cloudinary unsigned preset name
     * @param callback Returns (success, url, error)
     */

    @OptIn(ExperimentalEncodingApi::class)
    fun uploadByteArray(
        fileBytes: ByteArray,
        iv: ByteArray,
        mimeType: String,
        fileName: String,
        unsignedPreset: String,
        onProgress: (Float) -> Unit,
        onComplete: (Boolean, String?, String?) -> Unit
    ) {
        try {
            val contextString = "iv=${URLEncoder.encode(FileEncryptionUtil.encodeIv(iv), "UTF-8")}&original_mime=${URLEncoder.encode(mimeType, "UTF-8")}"
            val file = createTempEncryptedFile(fileBytes, fileName)

            MediaManager.get().upload(file.absolutePath)
                .unsigned(unsignedPreset)
                .option("resource_type", "raw")
                .option("filename", "$fileName.dat")
                .option("tags", "encrypted")
                .option("context", contextString)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = if (totalBytes == 0L) 0f else (bytes.toFloat() / totalBytes.toFloat())
                        onProgress(progress)
                    }

                    override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                        val url = resultData["secure_url"] as? String
                        file.delete() // Clean up temp file
                        onComplete(true, url, null)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        file.delete()
                        onComplete(false, null, "Error ${error.code}: ${error.description}")
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        file.delete()
                        onComplete(false, null, "Rescheduled: ${error.description}")
                    }
                })
                .dispatch()

        } catch (e: Exception) {
            onComplete(false, null, "Exception: ${e.localizedMessage}")
        }
    }

    private fun createTempEncryptedFile(fileBytes: ByteArray, fileName: String): File {
        val tempFile = File.createTempFile(fileName, ".dat")
        FileOutputStream(tempFile).use { output ->
            output.write(fileBytes)
        }
        return tempFile
    }

    fun uploadImageToCloudinary(
        context: Context,
        uri: Uri,
        unsignedPreset: String,
        onProgress: (Float) -> Unit,
        onComplete: (Boolean, String, String) -> Unit
    ) {
        try {
            // 1. Read file from URI
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.readBytes() ?: run {
                onComplete(false, "", "Failed to read file from URI")
                return
            }
            inputStream.close()

            // 2. Get file metadata
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = getFileNameFromUri(context, uri) ?: "image_${System.currentTimeMillis()}"

            // 3. Create temp file
            val tempFile = createTempFile(fileBytes, fileName)

            // 4. Prepare Cloudinary upload parameters
            val contextString = "original_mime=${URLEncoder.encode(mimeType, "UTF-8")}" +
                    "&original_filename=${URLEncoder.encode(fileName, "UTF-8")}"

            // 5. Upload to Cloudinary
            MediaManager.get().upload(tempFile.absolutePath)
                .unsigned(unsignedPreset)
                .option("resource_type", "auto")
                .option("filename", fileName)
                .option("tags", "profile_picture")
                .option("context", contextString)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = if (totalBytes == 0L) 0f else (bytes.toFloat() / totalBytes.toFloat())
                        onProgress(progress)
                    }

                    override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                        tempFile.delete()
                        val url = resultData["secure_url"] as? String
                        val publicId = resultData["public_id"] as? String
                        if (url != null && publicId != null) {
                            onComplete(true, url, "")
                        } else {
                            onComplete(true, "", "Missing url")
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        tempFile.delete()
                        onComplete(false, "", error.description)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        tempFile.delete()
                        onComplete(false, "", error.description)
                    }
                })
                .dispatch()

        } catch (e: Exception) {
            onComplete(false, "", e.message.toString())
        }
    }

    // Helper function to create temp file
    private fun createTempFile(fileBytes: ByteArray, fileName: String): File {
        val extension = fileName.substringAfterLast(".", "")
        val tempFile = File.createTempFile("upload_", ".$extension")
        FileOutputStream(tempFile).use { output ->
            output.write(fileBytes)
        }
        return tempFile
    }


}