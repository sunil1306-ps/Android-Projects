package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.FirstScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SecondScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()

                   NavHost(
                       navController = navController,
                        startDestination =Screens.First.name
                   ) {
                        composable(Screens.First.name) {
                            FirstScreen(navContoller = navController)
                        }
                       composable(Screens.Second.name) {
                           SecondScreen(navController = navController)
                       }
                   }

                }
            }
        }
    }
}

