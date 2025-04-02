package com.study.prototype1


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.study.prototype1.downloadmanagerguide.AndroidDownloader
import com.study.prototype1.viewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigation(
    signOutClicked: () -> Unit,
    mAuth: FirebaseAuth,
    signInClicked: () -> Unit,
    viewModel: MainViewModel,
    startDest: String,
    downloader: AndroidDownloader,
    context: Context
) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController,
        startDestination = startDest) {
        //Splash Screen
        composable("splash_screen") {
            SplashScreen(
                mAuth,
                navController = navController
            )
        }
        //SignIn Screen
        composable("sign_in") {
            var_screen.SignInScreen(signInClicked = { signInClicked() }, navController)
        }
        //Year Selection
        composable("year") {
            var_screen.yearScreen({ signOutClicked() }, mAuth.currentUser!!.photoUrl!!, navController = navController, profileName = mAuth.currentUser!!.displayName!!)
        }
        //Brach Selection
        composable("branch") {
            var_screen.branchScreen(navController = navController)
        }
        //Subject Selection
        composable("subject") {
            var_screen.subjectScreen(navController = navController)
        }
        //Documents Screen
        composable("docs") {
            var_screen.docsScreen(viewModel, downloader, context, navController)
        }
    }

}