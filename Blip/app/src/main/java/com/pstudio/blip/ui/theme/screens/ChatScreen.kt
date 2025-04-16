package com.pstudio.blip.ui.theme.screens

import CloudinaryUploader
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.pstudio.blip.R
import com.pstudio.blip.SetStatusBarColor
import com.pstudio.blip.data.Message
import com.pstudio.blip.utilclasses.CloudinaryDownloader
import com.pstudio.blip.utilclasses.FileEncryptionUtil
import com.pstudio.blip.utilclasses.copyUriToInternalStorage
import com.pstudio.blip.utilclasses.handlePickedFile
import com.pstudio.blip.utilclasses.isPermissionGranted
import com.pstudio.blip.utilclasses.openDecryptedFile
import com.pstudio.blip.utilclasses.requestManageExternalStoragePermission
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun ChatScreen(
    friendId: String,
    friendUsername: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val listState = rememberLazyListState()
    val messages =  chatViewModel.chats[friendId] ?: emptyList()
    val editingMessage = chatViewModel.editingMessage.value
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val authState by authViewModel.authState.collectAsState()
    val username = (authState as? AuthViewModel.AuthState.Success)?.username?: "Unknown"
    val isOnline = chatViewModel.isOnline.collectAsState().value

    var selectedMessageId by remember { mutableStateOf<String?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var messageOffset by remember { mutableStateOf(Offset.Zero) }
    var messageText by remember { mutableStateOf("") }
    var isIntialRender by remember { mutableStateOf(true) }
    var openPicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf("") }

    SetStatusBarColor(Color.Black)

    LaunchedEffect (friendId) {
        chatViewModel.listenForMessages(friendId, currentUserId)
        chatViewModel.setActiveChatUserId(friendId)
        chatViewModel.listenToUserOnlineStatus(friendId)
        chatViewModel.markMessagesAsSeen(currentUserId, friendId)
    }

    LaunchedEffect (isKeyboardOpen) {
        if (isKeyboardOpen && messages.isNotEmpty()) {
            delay(250)
            listState.scrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(friendId) {
        onDispose {
            chatViewModel.setActiveChatUserId(null)
            chatViewModel.removeUserOnlineStatusListener(friendId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            if (isIntialRender) {
                listState.scrollToItem(messages.size - 1)
                isIntialRender = false
            } else {
                listState.scrollToItem(messages.size - 1)
            }
        }
    }

    LaunchedEffect(editingMessage) {
        messageText = editingMessage?.message ?: ""
        if (editingMessage == null) {
            selectedMessageId = null
            showMenu = false
        }
    }

    BackHandler {
        if (editingMessage != null) {
            chatViewModel.cancelEditing()
            selectedMessageId = null
            showMenu = false
        } else {
            navController.popBackStack("homescreen", false)
        }
    }

    if (openPicker) {
        FilePicker(
            friendId,
            username,
            chatViewModel
        ) { uri, mimeType ->
            openPicker = false
        }
    }

    Scaffold(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            ContactHeader(isOnline, friendUsername, { navController.navigate("homeScreen") })
        }
    ) {
        Column(
            modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
        ) {

            Box(modifier = modifier
                .fillMaxSize()
                .weight(1f)
            ) {
                LazyColumn(
                    modifier
                        .padding(top = 68.dp)
                        .fillMaxSize()
                        .padding(vertical = 10.dp),
                    state = listState
                ) {
                    items(messages) {message ->

                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    // Save the position of this message on screen
                                    val position = coordinates.localToWindow(Offset.Zero)
                                    messageOffset = position
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            selectedMessageId = message.messageId
                                            showMenu = true
                                        }
                                    )
                                }
                        ) {
                            Column {
                                message.replyTo?.let { replied ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp),
                                        horizontalArrangement = if (message.senderId == currentUserId) Arrangement.End else Arrangement.Start
                                    ) {
                                        Text(
                                            text ="Replying to: ${replied.message}",
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                MessageItem(
                                    currentUserId,
                                    message.seen,
                                    message.mediaIv,
                                    message,
                                    message.senderId == currentUserId,
                                    { chatViewModel.startReplying(message) },
                                    it
                                )
                            }

                            if (showMenu && selectedMessageId == message.messageId) {
                                DropdownMenu(
                                    expanded = true,
                                    onDismissRequest = {
                                        showMenu = false
                                        selectedMessageId = null
                                    },
                                    offset = DpOffset(
                                        x = 150.dp, // Adjust X and Y based on your bubble alignment
                                        y = 0.dp
                                    )
                                ) {
                                    if (currentUserId == message.senderId) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                showMenu = false
                                                chatViewModel.startEditingMessage(message)
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            showMenu = false
                                            chatViewModel.deleteMessage(friendId, message.messageId)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Reply") },
                                        onClick = {
                                            showMenu = false
                                            chatViewModel.startReplying(message)
                                        }
                                    )
                                }
                            }

                        }
                    }
                }
            }
            chatViewModel.replyingToMessage.value?.let { replyingMessage ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Replying to:",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        Text(
                            replyingMessage.message,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                    IconButton(onClick = { chatViewModel.cancelReplying() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cancel Reply",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }
            Box(modifier.fillMaxWidth()) {
                ////////////////////////////////////////////////Input Box/////////////////////////////////////////////////////////
                Row(
                    modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = {
                            messageText = it
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = {
                            Text(
                                text = "Message",
                                fontSize = 14.sp

                            )
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (isPermissionGranted(context)) {
                                    openPicker = true
                                } else {
                                    requestManageExternalStoragePermission(context)
                                    Toast.makeText(context, "Please allow storage permission", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Email, contentDescription = "Attach File")
                            }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "",
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                modifier = modifier.clickable {
                                    if (messageText.isNotEmpty()) {
                                        if (editingMessage != null) {
                                            chatViewModel.editMessage(friendId, messageText.trim())
                                        } else {
                                            chatViewModel.sendMessage(friendId, username, messageText.trim())
                                        }
                                        messageText = ""
                                        selectedMessageId = null
                                        showMenu = false
                                    }
                                }
                            )
                        },
                        modifier = modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            unfocusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    )
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }

        }

    }

}

@Composable
fun ContactHeader(
    isOnline: Boolean,
    userName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val indicator = if (isOnline) Color.Green else Color.Gray

    Card(
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 30.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
        )
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = modifier.clickable { onClick() },
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Spacer(modifier = modifier.width(10.dp))

                Image(
                    painter = painterResource(id = R.drawable.doreamon),
                    contentDescription = "Profile Pic",
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .size(50.dp)
                        .shadow(20.dp, CircleShape, true, Color.Black, Color.Black)
                        .clip(CircleShape)
                        .border(2.dp, indicator, CircleShape)
                )
            }

            Text(
                text = userName,
                modifier = modifier
                    .padding(start = 10.dp, end = 20.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )

        }
    }

}

@Composable
fun MessageItem(
    currentUserId: String,
    seen: Boolean,
    mediaIv: String,
    message: Message,
    isSenderMe: Boolean,
    onDrag: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val color = if (seen) Color.Green else Color.White
    val dragOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 100f
    val downloadingState = remember { mutableStateOf(false) }
    val downloadedUri = remember { mutableStateOf<Uri?>(null) }
    val downloadedFile = remember { mutableStateOf<File?>(null) }
    val downloader = remember { CloudinaryDownloader(context) }
    val coroutineScope = rememberCoroutineScope()

    val isReceiver = !isSenderMe
    val hasLocalUri = message.localUri.isNotEmpty() && message.senderId == currentUserId



    LaunchedEffect(downloadedFile.value) {
        downloadedFile.value?.let { file ->
            downloadedUri.value = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Clean up files when no longer needed
            downloadedFile.value?.delete()
        }
    }

    Row(
        modifier = Modifier
            .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .pointerInput(message.messageId) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffsetX.value > threshold) {
                            onDrag()
                        }
                        // Animate back to original position
                        scope.launch {
                            dragOffsetX.animateTo(0f)
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        // Allow right swipe only (can customize)
                        val newOffset = dragOffsetX.value + dragAmount
                        scope.launch {
                            dragOffsetX.snapTo(newOffset.coerceAtLeast(0f))
                        }
                    }
                )
            },
        horizontalArrangement = if (isSenderMe) {
            Arrangement.End
        } else if(!isSenderMe) {
            Arrangement.Start
        }
        else Arrangement.SpaceBetween
    ) {
        Card(
            modifier
                .padding(
                    start = if (isSenderMe) 60.dp else 20.dp,
                    end = if (isSenderMe) 20.dp else 60.dp
                )
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isSenderMe) Color.Gray else Color(53, 54, 58, 255)
            )
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {

                when (message.messageType) {
                    "text" -> {
                        Text(
                            text = message.message,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    "image" -> {
                        val imageModifier = Modifier
//                            .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (hasLocalUri) {
                                    val file = File(message.localUri ?: return@clickable) // Guard against null
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    openFile(context, uri = Uri.parse(uri.toString()), mimeType = message.mimeType)
                                } else if (downloadedFile.value != null) {
                                    openFile(context, file = downloadedFile.value!!, mimeType = message.mimeType)
                                }
                            }

                        if (hasLocalUri) {
                            AsyncImage(
                                model = Uri.parse(message.localUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = imageModifier
                            )
                        } else if (downloadedFile.value != null) {
                            AsyncImage(
                                model = downloadedFile.value,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = imageModifier
                            )
                        } else {
                            Box(modifier = imageModifier.background(Color.DarkGray)) {
                                if (downloadingState.value) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                } else if (isReceiver) {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                downloadingState.value = true
                                                val result = downloader.downloadAndDecrypt(
                                                    message.message, message.mediaIv, message.mimeType, message.fileName
                                                )
                                                downloadingState.value = false
                                                result.onSuccess { downloadedFile.value = it }
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Download", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    if (hasLocalUri) {
                                        openFile(context, uri = Uri.parse(message.localUri), mimeType = message.mimeType)
                                    } else if (downloadedFile.value != null) {
                                        openFile(context, file = downloadedFile.value!!, mimeType = message.mimeType)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Text(
                                text = message.fileName,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                            if (isReceiver && downloadedFile.value == null && !downloadingState.value) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            downloadingState.value = true
                                            val result = downloader.downloadAndDecrypt(
                                                message.message, message.mediaIv, message.mimeType, message.fileName
                                            )
                                            downloadingState.value = false
                                            result.onSuccess { downloadedFile.value = it }
                                        }
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Download", tint = Color.White)
                                }
                            }

                            if (downloadingState.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(start = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
                if (isSenderMe) {
                    Box(
                        modifier = modifier
                            .padding(4.dp)
                            .size(7.dp)
                            .background(color, shape = CircleShape)
                    )
                }
            }
        }
    }

}

@Composable
fun FilePicker(
    friendId: String,
    userName: String,
    chatViewModel: ChatViewModel,
    onFileSelected: (Uri, String) -> Unit
) {
    val context = LocalContext.current
    val uploader = CloudinaryUploader()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Determine MIME type
            val originalName = context.getFileName(it) ?: "file_${System.currentTimeMillis()}"
            val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
            var absPath = ""

            val copiedFile = copyUriToInternalStorage(context, uri, originalName)
            copiedFile?.let { file ->
                absPath = file.absolutePath
            }

            Log.d("mimeType", originalName)
            Log.d("mimeType", mimeType)

            handlePickedFile(context, uri) { encryptedBytes, iv ->
                val mediaIv = FileEncryptionUtil.encodeIv(iv)
                uploader.uploadByteArray(encryptedBytes, iv, mimeType, originalName, "blip_preset"){ success, url, error ->

                    if (success) {
                        Log.d("uploadStatus", "File uploaded successfully. URL: $url")
                        Log.d("uploadStatus", mediaIv)
                        chatViewModel.sendMessage(
                            friendId,
                            userName,
                            url ?: return@uploadByteArray,
                            getMessageTypeFromMimeType(mimeType),
                            mediaIv,
                            mimeType,
                            originalName,
                            absPath
                        )
                    } else {
                        Log.d("uploadStatus", "Error uploading file: $error")
                    }

                }
            }


        }
    }

    // Call this when you want to open picker
    LaunchedEffect(Unit) {
        launcher.launch(arrayOf(
            "image/*",
            "video/*",
            "audio/*",
            "application/pdf",
            "application/msword",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" // .pptx
        ))
    }
}


@Composable
fun InputBox(
    chatViewModel: ChatViewModel,
    receiverId: String,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
            },
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    text = "Message",
                    fontSize = 14.sp

                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = modifier.clickable {
                        if (message.isNotEmpty()) {
                            //chatViewModel.sendMessage(receiverId, message)
                            message = ""
                        }
                    }
                )
            },
            modifier = modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                unfocusedIndicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        )
    }

}

fun openFile(
    context: Context,
    file: File? = null,
    uri: Uri? = null,
    mimeType: String?
) {
    try {
        val authority = "${context.packageName}.fileprovider" // Dynamic authority
        val contentUri = when {
            uri != null -> uri // Use provided URI (e.g., sender-side)
            file != null -> {
                FileProvider.getUriForFile(context, authority, file)
            }
            else -> {
                Toast.makeText(context, "File or URI required", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val finalMimeType = mimeType ?: file?.let { getMimeType(it) } ?: "*/*"

        Log.d("FileOpen", "Attempting to open: ${file?.absolutePath}")
        Log.d("FileOpen", "Content URI: $contentUri")
        Log.d("FileOpen", "MIME type: $finalMimeType")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, finalMimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Verify intent can be handled
        val resolveInfo = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo.isNotEmpty()) {
            context.startActivity(Intent.createChooser(intent, "Open with..."))
        } else {
            // Fallback to generic view
            val fallback = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (fallback.resolveActivity(context.packageManager) != null) {
                context.startActivity(fallback)
            } else {
                Toast.makeText(context, "No app found to open this file", Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: Exception) {
        Log.e("FileOpen", "Error opening file", e)
        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

fun getMimeType(
    file: File
): String {
    return when (file.extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "pdf" -> "application/pdf"
        "mp4" -> "video/mp4"
        else -> "*/*"
    }
}

fun getMessageTypeFromMimeType(
    mime: String
): String {
    return when {
        mime.startsWith("image") -> "image"
        mime.startsWith("video") -> "video"
        mime.startsWith("audio") -> "audio"
        else -> "file"
    }
}

fun Context.getFileName(uri: Uri): String? {
    return when (uri.scheme) {
        "content" -> {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } else null
            }
        }
        "file" -> uri.lastPathSegment
        else -> null
    }
}

fun Context.getFileViewIntent(uri: Uri, mimeType: String): Intent {
    val contentUri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        File(uri.path ?: "")
    )
    return Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(contentUri, mimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
}