package com.sina.projectv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sina.projectv.navigation.Navigation
import com.sina.projectv.ui.theme.ProjectVTheme
import com.sina.projectv.viewmodels.LoginViewModel
import com.sina.projectv.viewmodels.MediaViewModel
import com.sina.projectv.viewmodels.UserDataViewModel

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var viewModel: LoginViewModel
    private lateinit var dataViewModel: UserDataViewModel
    private var mediaViewModel: MediaViewModel = MediaViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    viewModel = LoginViewModel()
                    dataViewModel = UserDataViewModel()
                    mediaViewModel = MediaViewModel()
                    mediaViewModel.initializePlayer(this)
                    Navigation(this, viewModel, dataViewModel, mediaViewModel, navController)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaViewModel.pauseMedia()
    }

    override fun onResume() {
        super.onResume()
        mediaViewModel.playMedia(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaViewModel.releasePlayer()
    }

}
