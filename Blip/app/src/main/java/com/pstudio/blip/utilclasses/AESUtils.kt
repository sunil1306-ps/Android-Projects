package com.pstudio.blip.utilclasses

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.pstudio.blip.data.Message
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY = "abcdefghijklmnopqrstuvwxyzabcdef"

    fun encrypt(plainText: String): Pair<String, String> {
        val key: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance(ALGORITHM)

        // Generate random IV for each encryption
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)

        return Pair(encryptedBase64, ivBase64)
    }

    fun decrypt(encryptedText: String, ivString: String): String {
        val key: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance(ALGORITHM)

        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        return String(decryptedBytes)
    }

    fun Message.decryptIfNeeded(): Message {
        return if (!this.iv.isNullOrBlank()) {
            try {
                val decryptedText = decrypt(this.message, this.iv)
                this.copy(message = decryptedText)
            } catch (e: Exception) {
                this // fallback if decryption fails
            }
        } else {
            this // already plain text
        }
    }

}

object FileEncryptionUtil {

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_SIZE = 16 // 128-bit key

    // Replace with your own key (must be 16 bytes)
    private val secretKey: SecretKey = SecretKeySpec("YakYakSunilpooji".toByteArray(), "AES")

    fun encryptFileBytes(fileBytes: ByteArray): Pair<ByteArray, ByteArray> {
        val iv = ByteArray(KEY_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(fileBytes)
        return encrypted to iv
    }

    fun decryptFileFromBytes(encryptedBytes: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(encryptedBytes)
    }

    fun encodeIv(iv: ByteArray): String = Base64.encodeToString(iv, Base64.NO_WRAP)
    fun decodeIv(iv: String): ByteArray = Base64.decode(iv, Base64.NO_WRAP)
}

fun readBytesFromUri(context: Context, uri: Uri): ByteArray {
    val byteArray = context.contentResolver.openInputStream(uri)?.use { input ->
        input.readBytes()
    } ?: ByteArray(0)
    Log.d("uploadStatus", "readBytesFromUri: ${byteArray.size}")
    return byteArray
}

fun handlePickedFile(context: Context, uri: Uri, onEncrypted: (ByteArray, ByteArray) -> Unit) {
    val fileBytes = readBytesFromUri(context, uri)
    Log.d("uploadStatus", "fileBytes: ${fileBytes.size}")
    if (fileBytes.isNotEmpty()) {
        val (encryptedBytes, iv) = FileEncryptionUtil.encryptFileBytes(fileBytes)
        Log.d("uploadStatus", "encryptedFileSize: ${encryptedBytes.size}")
        onEncrypted(encryptedBytes, iv)
    } else {
        Log.e("FilePicker", "Failed to read file")
    }
}