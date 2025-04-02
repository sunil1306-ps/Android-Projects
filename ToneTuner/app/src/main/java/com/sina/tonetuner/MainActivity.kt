package com.sina.tonetuner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sina.tonetuner.navigation.Navigation
import com.sina.tonetuner.ui.theme.ToneTunerTheme
import com.sina.tonetuner.viewmodels.ImageViewModel
import com.sina.tonetuner.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Image captured successfully, no need to handle URI as it's already saved
            viewModel.cameraImageUri.value?.let {
                viewModel.handleGalleryResult(it) // Update the UI with the captured image
            }
        }
    }

    // Register a launcher for the gallery intent
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            viewModel.handleGalleryResult(uri)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch the camera
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            launchCamera()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Initialize the ViewModel
    private val viewModel: MainViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToneTunerTheme {
                navController = rememberNavController()
                Navigation(
                    viewModel,
                    imageViewModel,
                    galleryLauncher,
                    ::checkCameraPermission,
                    navController
                )
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, launch the camera
                launchCamera()
            }
            else -> {
                // Request the camera permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val uri = viewModel.captureImage(this) // Get the URI for the new image
        uri?.let {
            viewModel.handleGalleryResult(it) // Store it in the ViewModel before launching the camera

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, it)
            }
            cameraLauncher.launch(intent)
        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToneTunerTheme {

    }
}