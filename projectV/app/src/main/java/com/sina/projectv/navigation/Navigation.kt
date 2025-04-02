package com.sina.projectv.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sina.projectv.screens.CourseScreen
import com.sina.projectv.screens.HomeScreen
import com.sina.projectv.screens.LoginScreen
import com.sina.projectv.screens.MediaPlayerScreen
import com.sina.projectv.screens.SignUpScreen
import com.sina.projectv.screens.SplashScreen
import com.sina.projectv.viewmodels.LoginViewModel
import com.sina.projectv.viewmodels.MediaViewModel
import com.sina.projectv.viewmodels.UserDataViewModel

@Composable
fun Navigation(
    context: Context,
    viewModel: LoginViewModel,
    dataViewModel: UserDataViewModel,
    mediaVieModel: MediaViewModel,
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = "splashscreen"
    ) {
        composable("splashscreen") {
            SplashScreen(viewModel, navController)
        }
        composable("loginscreen") {
            LoginScreen(viewModel, dataViewModel, navController)
        }
        composable("signupscreen") {
            SignUpScreen(viewModel, dataViewModel, navController)
        }
        composable("homescreen") {
            HomeScreen(viewModel, dataViewModel, mediaVieModel, navController)
        }
        composable("coursescreen") {
            CourseScreen(context, mediaVieModel, navController)
        }
        composable("mediaplayerscreen") {
            MediaPlayerScreen(context, mediaVieModel)
        }
    }

}