package com.mini.fluenttalk.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mini.fluenttalk.R
import com.mini.fluenttalk.viewModels.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    loginvm: LoginViewModel,
    navController: NavController
) {
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
        //Check if user is signedIn and navigate accordingly
        if (loginvm.user != null) {
            navController.navigate("homeScreen") {
                popUpTo("splashScreen") {
                    inclusive = true
                }
            }
        } else {
            navController.navigate("logInScreen") {
                popUpTo("splashScreen") {
                    inclusive = true
                }
            }
        }
    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.final_logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
                .size(40.dp)
        )
    }
}