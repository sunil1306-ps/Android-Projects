package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practice.ui.theme.PracticeTheme
import com.example.practice.ui.theme.Screens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screens.First.name
                    ) {
                        composable(Screens.First.name) {
                            FirstScreen(
                                navController = navController
                            )
                        }
                        composable(Screens.Second.name) {
                            SecondScreen(
                                navController = navController
                            )
                        }
                        composable(Screens.Third.name) {
                            ThirdScreen(
                                navController = navController
                            )
                        }
                        composable(Screens.Forth.name) {
                            ForthScreen(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


