package com.pstudio.blip.ui.theme.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.pstudio.blip.R
import com.pstudio.blip.SetStatusBarColor
import com.pstudio.blip.ui.theme.BlipTheme
import com.pstudio.blip.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0f) }
    val authState by authViewModel.authState.collectAsState()
    val userId = (authState as? AuthViewModel.AuthState.Success)?.userId?: "Unknown"

    SetStatusBarColor(Color.Black)
    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 2f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(5f).getInterpolation(it)
                })
        )
        delay(1200L)

    }
    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                navController.navigate("homescreen") {
                    popUpTo("splashscreen") { inclusive = true }
                }
            }

            is AuthViewModel.AuthState.Error -> {
                navController.navigate("loginscreen") {
                    popUpTo("splashscreen") { inclusive = true }
                }
            }

            else -> {}
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .statusBarsPadding()
            .background(Color.Black)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier.weight(0.5f))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .scale(scale.value)
                    .size(40.dp)
            )
            Spacer(modifier.weight(0.4f))
            if (authState is AuthViewModel.AuthState.Loading) {
                LinearProgressIndicator(
                    modifier = modifier.width(100.dp),
                    color = Color.Red,
                    trackColor = Color.Gray,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier.height(30.dp))
            }
        }
    }

//    Box(contentAlignment = Alignment.Center,
//        modifier = Modifier.fillMaxSize().background(Color.Black)) {
//        Image(
//            painterResource(id = R.drawable.ic_launcher_foreground),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .scale(scale.value)
//                .size(40.dp)
//        )
//    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    BlipTheme {
        //SplashScreen()
    }
}