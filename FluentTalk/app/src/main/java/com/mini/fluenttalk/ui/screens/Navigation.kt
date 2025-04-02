package com.mini.fluenttalk.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mini.fluenttalk.viewModels.LoginViewModel
import com.mini.fluenttalk.viewModels.mainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(dest: String, navController: NavHostController, signOut: () -> Unit, logIn: () -> Unit, signUp: () -> Unit, loginvm: LoginViewModel, viewModel: mainViewModel) {

    NavHost(
        navController = navController,
        startDestination = dest
    ) {
        composable("splashScreen") {
            SplashScreen(loginvm, navController = navController)
        }
        composable("logInScreen") {
            LogInScreen(logIn, loginvm, navController = navController)
        }
        composable("signUpScreen") {
            SignUpScreen(signUp, loginvm, navController)
        }
        composable("homeScreen") {
            HomeScreen(signOut, loginvm, viewModel, navController = navController)
        }
        composable("chatScreen") {
            ChatScreen(viewModel, navController = navController)
        }
    }

}