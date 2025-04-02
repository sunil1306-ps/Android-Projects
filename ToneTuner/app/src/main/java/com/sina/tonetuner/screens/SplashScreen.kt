package com.sina.tonetuner.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, modifier: Modifier = Modifier) {

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
        navController.navigate("mainscreen") {
            popUpTo("splashscreen") {
                inclusive = true
            }
        }
    }

    // Text
    Box(contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()) {
        Text(
            text = "ToneTuner",
            fontSize = 20.sp,
            color = Color.Blue
        )
    }

}