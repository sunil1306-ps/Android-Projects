package com.pstudio.blip.ui.theme.navigation

import android.app.Activity
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pstudio.blip.ui.theme.screens.ChatScreen
import com.pstudio.blip.ui.theme.screens.HomeScreen
import com.pstudio.blip.ui.theme.screens.LogInScreen
import com.pstudio.blip.ui.theme.screens.SignUpScreen
import com.pstudio.blip.ui.theme.screens.SplashScreen
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatViewModel
import com.pstudio.blip.viewmodels.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BlipApp(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "splashscreen",
        modifier = modifier
    ) {
        composable("splashscreen") {
            SplashScreen(navController, authViewModel)
        }
        composable("loginscreen") {
            LogInScreen(navController, authViewModel)
        }
        composable("signupscreen") {
            SignUpScreen(navController, authViewModel)
        }
        composable("homescreen") {
            HomeScreen(navController, authViewModel, userViewModel, chatViewModel)
        }
        composable(
            route = "chatscreen/{friendId}/{friendUserName}",
            arguments = listOf(
                navArgument("friendId") {type = NavType.StringType},
                navArgument("friendUserName") {type = NavType.StringType}
            )
        ) {backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            val friendUsername = backStackEntry.arguments?.getString("friendUserName") ?: "Noname"

            ChatScreen(friendId, friendUsername, navController, authViewModel, chatViewModel)
        }
    }

}