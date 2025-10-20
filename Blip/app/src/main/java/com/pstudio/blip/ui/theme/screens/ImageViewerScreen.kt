package com.pstudio.blip.ui.theme.screens

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import android.widget.VideoView
import android.widget.MediaController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.pstudio.blip.R
import com.pstudio.blip.viewmodels.ChatViewModel
import com.pstudio.blip.viewmodels.SaveState


@Composable
fun ImageViewerScreen(
    imageUri: String,
    onSaveClicked: () -> Unit,
    onBack: () -> Unit,
    chatViewModel: ChatViewModel
) {
    val context = LocalContext.current
    val uri = remember(imageUri) { Uri.parse(imageUri) }
    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
    val originalName = context.getFileName(uri) ?: "file_${System.currentTimeMillis()}"
    val isVideo = remember(mimeType) { mimeType.startsWith("video/") }
    val isImage = remember(mimeType) { mimeType.startsWith("image/") }
    val saveState by chatViewModel.saveState.collectAsState()

    val scale = remember { Animatable(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale.value * zoomChange).coerceIn(1f, 5f)
        coroutineScope.launch {
            scale.snapTo(newScale)
        }
        offset.value += panChange
    }

    val saveIcon = when (saveState) {
        is SaveState.Idle -> R.drawable.baseline_download_24
        is SaveState.Saving -> R.drawable.baseline_downloading_24
        is SaveState.Saved -> R.drawable.baseline_download_done_24
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        when {
            isImage -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    coroutineScope.launch {
                                        if (scale.value > 1f) {
                                            scale.animateTo(1f)
                                            offset.value = Offset.Zero
                                        } else {
                                            scale.animateTo(2.5f)
                                        }
                                    }
                                }
                            )
                        }
                        .transformable(state = transformableState)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale.value,
                                scaleY = scale.value,
                                translationX = offset.value.x,
                                translationY = offset.value.y
                            )
                    )
                }
            }

            isVideo -> {
                AndroidView(
                    factory = { context ->
                        VideoView(context).apply {
                            setVideoURI(uri)
                            setMediaController(MediaController(context).apply {
                                setAnchorView(this@apply)
                            })
                            setOnPreparedListener { it.isLooping = true }
                            requestFocus()
                            start()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .align(Alignment.Center)
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        IconButton(
            onClick = onSaveClicked,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(painter = painterResource(saveIcon), contentDescription = "Save", tint = Color.White)
        }

    }

}

