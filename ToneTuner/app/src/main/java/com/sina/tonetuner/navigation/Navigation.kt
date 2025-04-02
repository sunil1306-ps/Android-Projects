package com.sina.tonetuner.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.sina.tonetuner.screens.ImageAnalysisScreen
import com.sina.tonetuner.screens.MainScreen
import com.sina.tonetuner.screens.SplashScreen
import com.sina.tonetuner.viewmodels.ImageViewModel
import com.sina.tonetuner.viewmodels.MainViewModel

@Composable
fun Navigation(
    viewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    galleryLauncher: ActivityResultLauncher<Intent>,
    checkCameraPermission: () -> Unit,
    navController: NavHostController) {

    NavHost (
        navController = navController,
        startDestination = "splashscreen"
    ) {
        composable("splashscreen") {
            SplashScreen(navController)
        }
        composable("mainScreen") {
            MainScreen(
                viewModel,
                galleryLauncher,
                checkCameraPermission,
                navController
            )
        }
        composable("imageanalysis") {
            ImageAnalysisScreen(
                checkCameraPermission,
                viewModel,
                imageViewModel,
                navController
            )
        }

    }

}