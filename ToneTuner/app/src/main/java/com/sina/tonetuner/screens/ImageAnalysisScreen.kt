package com.sina.tonetuner.screens


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.sina.tonetuner.viewmodels.ImageViewModel
import com.sina.tonetuner.viewmodels.MainViewModel


@Composable
fun ImageAnalysisScreen(
    checkCameraPermission: () -> Unit,
    viewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val cameraImageUri by viewModel.cameraImageUri.collectAsState()
    val galleryImageUri by viewModel.galleryImageUri.collectAsState()
    val imageUri = cameraImageUri?: galleryImageUri

    BackHandler {
        viewModel.setUri()
        navController.navigate("mainscreen"){
            popUpTo("imageanalysis"){
                inclusive = true
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ){

        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(500.dp)
                .width(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        )

        Spacer(modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.setUri()
                navController.navigate("mainscreen"){
                    popUpTo("imageanalysis"){
                        inclusive = true
                    }
                }
                checkCameraPermission()
            },

            modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(text = "ReTake")
        }
    }
}