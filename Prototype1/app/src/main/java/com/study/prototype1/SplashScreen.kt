package com.study.prototype1

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.prototype1.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    mAuth: FirebaseAuth,
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
        delay(1500L)
        //Check if user is signedIn and navigate accordingly
        if (mAuth.currentUser == null) {
            navController.navigate("sign_in") {
                popUpTo("splash_screen") {
                    inclusive = true
                }
            }
        } else {
            navController.navigate("year") {
                popUpTo("splash_screen") {
                    inclusive = true
                }
            }
        }
    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.logo_white),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}