package com.sina.tonetuner.screens

import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sina.tonetuner.viewmodels.MainViewModel
import kotlin.system.exitProcess

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    galleryLauncher: ActivityResultLauncher<Intent>,
    checkCameraPermission: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraImageUri by viewModel.cameraImageUri.collectAsState()
    val galleryImageUri by viewModel.galleryImageUri.collectAsState()
    var backCount by remember { mutableStateOf(0) }

    BackHandler {
        backCount += 1
        if (backCount == 2) {
            exitProcess(1)
        }
        viewModel.setUri()
        Toast.makeText(context, "press again to exit", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Capture Image Button
        Button(
            onClick = {
                checkCameraPermission()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Capture Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select from Gallery Button
        Button(
            onClick = {
                viewModel.selectImageFromGallery(context)
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select from Gallery")
        }

        if (cameraImageUri != null) {
            navController.navigate("imageanalysis") {
                popUpTo("mainscreen") {
                    inclusive = true
                }
            }
        } else if (galleryImageUri != null) {
            LaunchedEffect(galleryImageUri) {
                navController.navigate("imageanalysis") {
                    popUpTo("mainscreen") {
                        inclusive = true
                    }
                }
            }
        }


    }
}

