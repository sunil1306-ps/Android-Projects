package com.sina.tonetuner.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel: ViewModel() {
    // State to hold the selected image URI
    private val _galleryImageUri = MutableStateFlow<Uri?>(null)
    val galleryImageUri: StateFlow<Uri?> get() = _galleryImageUri
    private val _cameraImageUri = MutableStateFlow<Uri?>(null)
    val cameraImageUri: StateFlow<Uri?> get() = _cameraImageUri

    // Function to capture an image using the camera
    fun captureImage(context: Context): Uri? {
        val imageFile = createImageFile(context)
        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
        context.grantUriPermission(
            context.packageName,
            imageUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        _cameraImageUri.value = imageUri
        return imageUri
    }

    fun setUri() {
        _galleryImageUri.value = null
        _cameraImageUri.value = null
    }

    // Function to select an image from the gallery
    fun selectImageFromGallery(context: Context) {
        viewModelScope.launch {
            // Launch the gallery intent
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            context.startActivity(intent)
        }
    }

    // Function to handle the result from the gallery
    fun handleGalleryResult(result: Uri?) {
        _galleryImageUri.value = result
    }

    // Helper function to create a file for the captured image
    private fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}