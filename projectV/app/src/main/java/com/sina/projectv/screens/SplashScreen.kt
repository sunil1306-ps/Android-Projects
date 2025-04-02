package com.sina.projectv.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sina.projectv.R
import com.sina.projectv.ui.theme.ProjectVTheme
import com.sina.projectv.viewmodels.LoginViewModel
import com.sina.projectv.viewmodels.UserDataViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: LoginViewModel,
    navController: NavHostController
) {

    val dest = "splashscreen"
    val scale = remember {
        Animatable(0f)
    }

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

        if (viewModel.isUserLoggedIn) {
            navController.navigate("homescreen") {
                popUpTo("splashscreen") {
                    inclusive = true
                }
            }
        } else {
            navController.navigate("loginscreen") {
                popUpTo("splashscreen") {
                    inclusive = true
                }
            }
        }

    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
                .size(40.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    ProjectVTheme {
//        SplashScreen()
    }
}