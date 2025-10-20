package com.pstudio.blip.ui.theme.screens

import CloudinaryUploader
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.pstudio.blip.utilclasses.AESUtils
import com.pstudio.blip.utilclasses.FileEncryptionUtil
import com.pstudio.blip.utilclasses.copyFileToCustomDirectory
import com.pstudio.blip.utilclasses.handlePickedFile
import com.pstudio.blip.viewmodels.ChatViewModel
import android.widget.VideoView
import android.widget.MediaController
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import com.pstudio.blip.R

@Composable
fun PreviewScreen(
    encodedUri: String,
    friendId: String,
    userName: String,
    chatViewModel: ChatViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val uri = Uri.parse(encodedUri)
    val uploader = CloudinaryUploader()
    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
    val originalName = context.getFileName(uri) ?: "file_${System.currentTimeMillis()}"
    val isVideo = remember(mimeType) { mimeType.startsWith("video/") }
    val isImage = remember(mimeType) { mimeType.startsWith("image/") }
    var sendAsStealth by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .navigationBarsPadding()
        .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {

        when {
            isImage -> {
                SubcomposeAsyncImage(
                    model = uri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
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
                        .fillMaxWidth()
                        //.aspectRatio(16 / 9f)
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_attach_file_24),
                        contentDescription = "File",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = originalName,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("No preview" , color = Color.White)
                Spacer(Modifier.width(10.dp))
                Checkbox(
                    checked = sendAsStealth,
                    onCheckedChange = { sendAsStealth = it }
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {

                        val result = copyFileToCustomDirectory(context, uri, getMessageTypeFromMimeType(mimeType))
                        try {
                            result?.let { file ->
                                val absPath = file.file.absolutePath
                                handlePickedFile(context, file.uri) { encryptedBytes, iv ->
                                    val mediaIv = FileEncryptionUtil.encodeIv(iv)

                                    val messageId = chatViewModel.sendMessage(
                                        receiverId = friendId,
                                        senderUserName = userName,
                                        messageText = "",
                                        messageType = getMessageTypeFromMimeType(mimeType),
                                        mediaIv = mediaIv,
                                        mimeType = mimeType,
                                        fileName = originalName,
                                        localUri = absPath,
                                        isStealth = sendAsStealth
                                    )!!

                                    uploader.uploadByteArray(
                                        fileBytes = encryptedBytes,
                                        iv = iv,
                                        mimeType = mimeType,
                                        fileName = originalName,
                                        unsignedPreset = "blip_preset",
                                        onProgress = { progress ->
                                            chatViewModel.setUploadProgress(messageId, progress)
                                        },
                                        onComplete = { success, url, error ->
                                            if (success) {
                                                chatViewModel.removeUploadProgress(messageId)
                                                val (encryptedUrl, newIv) = AESUtils.encrypt(url ?: "")
                                                chatViewModel.updateMediaUrlForMessage(
                                                    receiverId = friendId,
                                                    messageId = messageId,
                                                    newUrl = encryptedUrl,
                                                    iv = newIv
                                                )
                                            } else {
                                                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                                                return@uploadByteArray
                                            }
                                        }
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to copy media", Toast.LENGTH_SHORT).show()
                        }

                        navController.popBackStack()
                    }
                ) {
                    Text("Send")
                }
            }
        }

    }
}

