package com.sina.projectv.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.sina.projectv.viewmodels.MediaViewModel

@Composable
fun MediaPlayerScreen(
    context: Context,
    mediaViewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {

    val activity = context as Activity
    var isLandscape = remember { mutableStateOf(false) }


    DisposableEffect(Unit) {
        onDispose {
            mediaViewModel.stopPlayer(context)
        }
    }


    val isPlaying by remember { derivedStateOf {
        mediaViewModel.isPlaying
    } }

    Box(modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    player = mediaViewModel.getPlayer()
                }
            },
            modifier = modifier.fillMaxSize()
        )

        Button(
            onClick = {
                if (isLandscape.value) {
                    mediaViewModel.setPortraitOrientation(activity)
                    isLandscape.value = false
                } else {
                    mediaViewModel.setLandscapeOrientation(activity)
                    isLandscape.value = true
                }
            },
            modifier = modifier.align(Alignment.BottomEnd)
        ) {
            Text(text = "R")
        }
    }

}