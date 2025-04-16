import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.pstudio.blip.utilclasses.FileEncryptionUtil
import java.io.File
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
        callback: (Boolean, String?, String?) -> Unit
    ) {
        try {

            val encodedIv = URLEncoder.encode(FileEncryptionUtil.encodeIv(iv), "UTF-8")
            val context = "iv=$encodedIv&original_mime=${URLEncoder.encode(mimeType, "UTF-8")}"

            MediaManager.get().upload(fileBytes)
                .unsigned(unsignedPreset)
                .option("resource_type", "raw") // Force raw upload
                .option("filename", "$fileName.dat")
                .option("tags", "encrypted")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                        val url = resultData["secure_url"] as? String
                        callback(true, url, null)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        callback(false, null, "Error ${error.code}: ${error.description}")
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        callback(false, null, "Rescheduled: ${error.description}")
                    }
                })
                .dispatch()
        } catch (e: Exception) {
            callback(false, null, "Exception: ${e.localizedMessage}")
        }
    }

    // Generate a unique filename with extension
    private fun generateFileName(mimeType: String): String {
        val timestamp = System.currentTimeMillis()
        return when (mimeType) {
            "image/jpeg" -> "img_$timestamp.jpeg"
            "image/png" -> "img_$timestamp.png"
            "video/mp4" -> "vid_$timestamp.mp4"
            else -> "file_$timestamp.dat"
        }
    }

    // Determine resource type from MIME
    private fun getResourceType(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> "image"
            mimeType.startsWith("video/") -> "video"
            mimeType.startsWith("audio/") -> "audio"
            else -> "auto"
        }
    }
}