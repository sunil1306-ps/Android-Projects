package com.pstudio.blip.ui.theme.screens

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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.google.firebase.auth.FirebaseAuth
import com.pstudio.blip.R
import com.pstudio.blip.SetStatusBarColor
import com.pstudio.blip.data.Message
import com.pstudio.blip.data.MessageStatus
import com.pstudio.blip.utilclasses.CloudinaryDownloader
import com.pstudio.blip.utilclasses.isFileSizeWithinLimit
import com.pstudio.blip.utilclasses.isPermissionGranted
import com.pstudio.blip.utilclasses.requestManageExternalStoragePermission
import com.pstudio.blip.utilclasses.uriToTempFile
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val listState = rememberLazyListState()
    val messages =  chatViewModel.chats[friendId] ?: emptyList()
    val editingMessage = chatViewModel.editingMessage.value
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val authState by authViewModel.authState.collectAsState()
    val username = (authState as? AuthViewModel.AuthState.Success)?.username?: "Unknown"
    val isOnline = chatViewModel.isOnline.collectAsState().value
    val isTyping = chatViewModel.isFriendTyping[friendId] == true
    val textFieldFocusState = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    var selectedMessageId by remember { mutableStateOf<String?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var messageOffset by remember { mutableStateOf(Offset.Zero) }
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var isIntialRender by remember { mutableStateOf(true) }
    var openPicker by remember { mutableStateOf(false) }
    var openCamera by remember { mutableStateOf(false) }
    var showCameraOptions by remember { mutableStateOf(false) }
    var openVideo by remember { mutableStateOf(false) }
    var showValidationDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf("") }
    var typingJob by remember { mutableStateOf<Job?>(null) }
    var highlightedMessageId by remember { mutableStateOf<String?>(null) }

    SetStatusBarColor(Color.Black)

    LaunchedEffect (friendId) {
        chatViewModel.listenForMessages(friendId, currentUserId)
        chatViewModel.setActiveChatUserId(friendId)
        chatViewModel.listenToUserOnlineStatus(friendId) {}
        chatViewModel.markMessagesAsSeen(friendId, currentUserId)
        chatViewModel.observeTypingStatus(friendId)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (!chatViewModel.isUserInBackground.value && chatViewModel.activeChatUserId.value == friendId) {
                    chatViewModel.markMessagesAsSeen(friendId, currentUserId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        openPicker = false
        openCamera = false
        openVideo = false
    }

    if (openPicker) {
        FilePicker(
            navController = navController,
            friendId = friendId,
            userName = username,
            onDismiss = {
                openPicker = false
            }
        )
    }

    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { showValidationDialog = false },
            title = { Text("Inappropriate Content") },
            text = { Text("Your message contains words that are not allowed. Please rephrase it.") },
            confirmButton = {
                TextButton(onClick = { showValidationDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (openCamera) {
        CameraCaptureLauncher(
            openCamera = true,
            onImageCaptured = { capturedUri ->
                navController.navigate("preview_screen/${Uri.encode(capturedUri.toString())}/$friendId/$username")
                openCamera = false
            },
            onDismiss = {
                openCamera = false
            }
        )
    }

    if (openVideo) {
        VideoCaptureLauncher(
            openVideo = true,
            onVideoCaptured = { capturedUri ->
                navController.navigate("preview_screen/${Uri.encode(capturedUri.toString())}/$friendId/$username")
                openVideo = false
            },
            onDismiss = {
                openVideo = false
            }
        )
    }

    if (showCameraOptions) {
        AlertDialog(
            onDismissRequest = { showCameraOptions = false },
            title = { Text("Choose Action") },
            text = {
                Column {
                    TextButton(onClick = {
                        showCameraOptions = false
                        openCamera = true
                    }) {
                        Text("Take Photo 📸")
                    }
                    TextButton(onClick = {
                        showCameraOptions = false
                        openVideo = true
                    }) {
                        Text("Record Video 🎥")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    fun LazyListState.isScrolledToEnd(index: Int = 2): Boolean {
        return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - index
    }

    fun LazyListState.isInRange(): Boolean {
        return layoutInfo.visibleItemsInfo.lastOrNull()?.index in (layoutInfo.totalItemsCount - 10)..<layoutInfo.totalItemsCount
    }

    LaunchedEffect (isKeyboardOpen) {
        if (isKeyboardOpen && messages.isNotEmpty() && listState.isInRange()) {
            delay(180)
            listState.scrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(showMenu) {
        if (showMenu) {
            // When menu shows, keep keyboard open
            focusRequester.requestFocus()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Clean up focus when leaving screen
            focusManager.clearFocus()
        }
    }

    DisposableEffect(friendId) {
        onDispose {
            chatViewModel.setActiveChatUserId(null)
            chatViewModel.removeUserOnlineStatusListener(friendId)
        }
    }

    LaunchedEffect(messages.size) {
        if (isIntialRender) {
            listState.scrollToItem(messages.size - 1)
            isIntialRender = false
        } else if (messages.isNotEmpty() && listState.isScrolledToEnd() && !isIntialRender) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    LaunchedEffect(isTyping) {
        if (listState.isScrolledToEnd() && isTyping) {
            // Only auto-scroll if user is already near the bottom
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    LaunchedEffect(editingMessage) {
        messageText = TextFieldValue(
            text = editingMessage?.message ?: "",
            selection = TextRange(editingMessage?.message?.length ?: 0)
        )
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

    Scaffold(
        modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            ContactHeader(isOnline, friendUsername, { navController.navigate("homeScreen") }, isTyping)
        }
    ) {
        Column(
            modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .windowInsetsPadding(WindowInsets.ime)
        ) {

            Box(modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .padding(top = 62.dp)
            ) {
                LazyColumn(
                    modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp)
                        .imePadding(),
                    state = listState
                ) {

                    items(messages, key = {it.messageId}) {message ->

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
                                            highlightedMessageId = message.messageId
                                            showMenu = true
                                            textFieldFocusState.value = true
                                            focusRequester.requestFocus()
                                        },
                                        onPress = {
                                            //focusRequester.requestFocus()
                                        }
                                    )
                                }
                        ) {
                            Column {
                                message.replyTo?.let { replied ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 2.dp),
                                        horizontalArrangement = if (message.senderId == currentUserId) Arrangement.End else Arrangement.Start
                                    ) {
                                        val replyText = if (replied.messageType != "text") replied.fileName else replied.message
                                        Text(
                                            text = "Replying to: $replyText",
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = modifier.clickable {
                                                coroutineScope.launch {
                                                    val repliedIndex = messages.indexOfFirst {
                                                        it.messageId == replied.messageId
                                                    }

                                                    if (repliedIndex != -1) {
                                                        // Smooth scroll to the message
                                                        listState.animateScrollToItem(
                                                            index = repliedIndex,
                                                            scrollOffset = -100 // Adds padding above
                                                        )

                                                        // Highlight temporarily
                                                        highlightedMessageId = replied.messageId
                                                        delay(1500)
                                                        highlightedMessageId = null
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                val isHighlighted = message.messageId == highlightedMessageId
                                MessageItem(
                                    navController,
                                    chatViewModel,
                                    currentUserId,
                                    message.mediaIv,
                                    message,
                                    message.senderId == currentUserId,
                                    { chatViewModel.startReplying(message) },
                                    isHighlighted,
                                    it
                                )
                            }

                            if (showMenu && selectedMessageId == message.messageId) {
                                focusRequester.requestFocus()
                                DropdownMenu(
                                    expanded = true,
                                    onDismissRequest = {
                                        showMenu = false
                                        highlightedMessageId = null
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
                                                highlightedMessageId = null
                                                chatViewModel.startEditingMessage(message)
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            showMenu = false
                                            highlightedMessageId = null
                                            chatViewModel.deleteMessage(friendId, message.messageId)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Reply") },
                                        onClick = {
                                            showMenu = false
                                            highlightedMessageId = null
                                            chatViewModel.startReplying(message)
                                        }
                                    )
                                }
                            }

                        }
                    }
                    if (isTyping) {
                        item {
                            TypingIndicatorBubble()
                        }
                    }
                }
                if (!listState.isScrolledToEnd(1)) {
                    Box(
                        modifier = modifier.size(35.dp)
                            .background(Color.Gray, CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
                                }
                            }
                            .align(Alignment.BottomCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = modifier.size(30.dp)
                        )
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
                    Column(
                        modifier.weight(0.8f)
                    ) {
                        Text(
                            "Replying to:",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        Text(
                            text = if (replyingMessage.messageType != "text")
                                replyingMessage.fileName
                            else replyingMessage.message,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                    IconButton(onClick = { chatViewModel.cancelReplying() }, modifier.weight(0.2f)) {
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
                        modifier = modifier.fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { state ->
                                textFieldFocusState.value = state.isFocused
                            },
                        onValueChange = {
                            messageText = it
                            typingJob?.cancel()
                            chatViewModel.setTypingStatus(friendId, true)

                            typingJob = coroutineScope.launch {
                                delay(1000L) // stop typing after 1s of no input
                                chatViewModel.setTypingStatus(friendId, false)
                            }
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
                                Icon(
                                    painter = painterResource(R.drawable.baseline_attach_file_24),
                                    contentDescription = "Attach File"
                                )
                            }
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = modifier.padding(end = 12.dp)
                            ) {
                                // New icon added here
                                Icon(
                                    painter = painterResource(R.drawable.baseline_photo_camera_24),
                                    contentDescription = "Capture Image",
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                    modifier = Modifier.clickable {
                                        showCameraOptions = true
                                    }
                                )

                                // Original send icon
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send message",
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                    modifier = Modifier.clickable {
                                        if (messageText.text.isNotEmpty()) {
                                            if (editingMessage != null) {
                                                chatViewModel.editMessage(friendId, messageText.text.trim())
                                            } else {
                                                chatViewModel.sendMessage(friendId, username, messageText.text.trim())
                                            }
                                            messageText = TextFieldValue("")
                                            selectedMessageId = null
                                            showMenu = false
                                        }
                                    }
                                )
                            }
                        },
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
    isTyping: Boolean,
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

            Column {
                Text(
                    text = userName,
                    modifier = modifier
                        .padding(start = 10.dp, end = 20.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Spacer(modifier = modifier.height(5.dp))
                if (isTyping) {
                    Text(
                        text = "Typing...",
                        modifier = modifier
                            .padding(start = 10.dp, end = 20.dp),
                        fontSize = 12.sp,
                    )
                }
            }

        }
    }

}

@Composable
fun TextMessageItem(message: Message, modifier: Modifier = Modifier) {
    Text(
        text = message.message,
        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
        modifier = modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun UploadProgress(uploadProgress: Float, modifier: Modifier = Modifier) {
    if (uploadProgress in 0f..1f) {
        Box(
            modifier = Modifier
                .sizeIn(maxHeight = 35.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { uploadProgress },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f), // Makes it square, so it scales nicely
                strokeWidth = 2.dp,
                color = Color(0xFF4CAF50), // Progress bar color (green)
                trackColor = Color(0xFFBDBDBD) // Background track color (light gray)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .sizeIn(maxHeight = 35.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f), // Makes it square, so it scales nicely
                strokeWidth = 2.dp,
                color = Color(0xFF4CAF50), // Progress bar color (green)
                trackColor = Color(0xFFBDBDBD) // Background track color (light gray)
            )
        }
    }
}

@Composable
fun DownloadProgress(downloadProgress: Float, modifier: Modifier = Modifier) {
    if (downloadProgress in 0f..1f) {
        Box(
            modifier = Modifier
                .sizeIn(maxHeight = 35.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { downloadProgress },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f), // Makes it square, so it scales nicely
                strokeWidth = 2.dp,
                color = Color(0xFF4CAF50), // Progress bar color (green)
                trackColor = Color(0xFFBDBDBD) // Background track color (light gray)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .sizeIn(maxHeight = 35.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f), // Makes it square, so it scales nicely
                strokeWidth = 2.dp,
                color = Color(0xFF4CAF50), // Progress bar color (green)
                trackColor = Color(0xFFBDBDBD) // Background track color (light gray)
            )
        }
    }
}

@Composable
fun ImageMessageItem(
    context: Context,
    currentUserId: String,
    message: Message,
    downloadedFile: MutableState<File?>,
    downloadingState: MutableState<Boolean>,
    navController: NavHostController,
    isSenderMe: Boolean,
    uploadProgress: Float,
    downloadProgress: Float,
    chatViewModel: ChatViewModel,
    downloader: CloudinaryDownloader,
    modifier: Modifier = Modifier
) {
    val receiverUriExists = message.receiverLocalUri.isNotEmpty() && message.senderId != currentUserId
    val senderUriExists = message.localUri.isNotEmpty() && message.senderId == currentUserId
    val isReceiver = !isSenderMe
    val coroutineScope = rememberCoroutineScope()
    val showRecoveryDialog = remember { mutableStateOf(false) }

    val fileToOpen = when {
        senderUriExists -> File(message.localUri)
        receiverUriExists -> File(message.receiverLocalUri)
        downloadedFile.value != null -> downloadedFile.value!!
        else -> null
    }

    val fileUri = fileToOpen?.let {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
    }

    val imageModifier = Modifier
        .sizeIn(
            minWidth = 250.dp,
            maxWidth = 250.dp,
            minHeight = 250.dp,
            maxHeight = 250.dp
        )
        .clip(RoundedCornerShape(8.dp))
        .clickable(enabled = fileUri != null) {
//            fileUri?.let {
//                //openFile(context, uri = it, mimeType = message.mimeType)
//                val encodeUri = Uri.encode(it.toString())
//                navController.navigate("image_viewer/$encodeUri")
//            }
            showRecoveryDialog.value = true
        }
        .border(4.dp, Color.Gray, RoundedCornerShape(8.dp))

    if (showRecoveryDialog.value) {
        CheckAndRecoverMissingMedia(
            context,
            isSenderMe,
            message,
            fileUri!!,
            downloader,
            chatViewModel,
            downloadedFile,
            downloadingState,
        ) { uri ->
            val encodedUri = Uri.encode(uri.toString())
            navController.navigate("image_viewer/$encodedUri")
        }
    }

    if (message.stealth) {
        // Show as an attachment-style stealth image (no preview)
        Row(
            modifier = Modifier
                .clickable(enabled = fileUri != null) {
//                    fileUri?.let {
//                        val encodedUri = Uri.encode(it.toString())
//                        navController.navigate("image_viewer/$encodedUri")
//                    }
                    showRecoveryDialog.value = true
                }
                .background(Color.DarkGray, RoundedCornerShape(8.dp))
                .padding(10.dp)
                .fillMaxSize()
                .size(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = modifier.weight(0.7f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_image_24),
                    contentDescription = "Stealth Image",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message.fileName,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (isSenderMe) {
                if (message.status == MessageStatus.SENDING) {
                    Box(modifier.weight(0.3f)) {
                        UploadProgress(uploadProgress)
                    }
                }
            }

            if (isReceiver && !receiverUriExists && downloadedFile.value == null && !downloadingState.value) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            downloadingState.value = true
                            val result = downloader.downloadAndDecrypt(
                                isSenderMe, message.message, message.mediaIv, message.mimeType, message.fileName
                            ) { progress ->
                                chatViewModel.setDownloadProgress(message.messageId, progress)
                                Log.d("progress", progress.toString())
                            }
                            downloadingState.value = false
                            result.onSuccess { file ->
                                downloadedFile.value = file
                                chatViewModel.setReceiverUri(
                                    message.senderId,
                                    message.receiverId,
                                    message.messageId,
                                    file.absolutePath
                                )
                            }
                            result.onFailure {
                                Toast.makeText(context, "Failed to download media", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = modifier.weight(0.3f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_download_24),
                        contentDescription = "Download",
                        tint = Color.White
                    )
                }
            }

            if (downloadingState.value) {
                Box(modifier.weight(0.3f)) {
                    DownloadProgress(downloadProgress)
                }
            }
        }
    } else {
        // Normal image (non-stealth) display logic
        Box(contentAlignment = Alignment.Center) {
            when {
                senderUriExists -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(message.localUri))
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .size(coil.size.Size.ORIGINAL)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }

                receiverUriExists -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(message.receiverLocalUri))
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .size(coil.size.Size.ORIGINAL)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }

                downloadedFile.value != null -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(downloadedFile.value)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .size(coil.size.Size.ORIGINAL)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }

                else -> {
                    Box(
                        modifier = imageModifier.background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (downloadingState.value) {
                            DownloadProgress(downloadProgress)
                        } else if (isReceiver) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        downloadingState.value = true
                                        val result = downloader.downloadAndDecrypt(
                                            isSenderMe, message.message, message.mediaIv, message.mimeType, message.fileName
                                        ) { progress ->
                                            chatViewModel.setDownloadProgress(message.messageId, progress)
                                            Log.d("progress", progress.toString())
                                        }
                                        downloadingState.value = false
                                        result.onSuccess { file ->
                                            downloadedFile.value = file
                                            chatViewModel.setReceiverUri(
                                                message.senderId,
                                                message.receiverId,
                                                message.messageId,
                                                file.absolutePath
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_download_24),
                                    contentDescription = "Download",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
            if (isSenderMe) {
                if (message.status == MessageStatus.SENDING) {
                    UploadProgress(uploadProgress)
                }
            }
        }
    }

}

@Composable
fun VideoMessageItem(
    context: Context,
    currentUserId: String,
    message: Message,
    downloadedFile: MutableState<File?>,
    downloadingState: MutableState<Boolean>,
    navController: NavHostController,
    isSenderMe: Boolean,
    uploadProgress: Float,
    downloadProgress: Float,
    chatViewModel: ChatViewModel,
    downloader: CloudinaryDownloader,
    modifier: Modifier = Modifier
) {

    val receiverUriExists = message.receiverLocalUri.isNotEmpty() && message.senderId != currentUserId
    val senderUriExists = message.localUri.isNotEmpty() && message.senderId == currentUserId
    val isReceiver = !isSenderMe
    val coroutineScope = rememberCoroutineScope()
    val showRecoveryDialog = remember { mutableStateOf(false) }

    val fileToOpen = when {
        senderUriExists -> File(message.localUri)
        receiverUriExists -> File(message.receiverLocalUri)
        downloadedFile.value != null -> downloadedFile.value!!
        else -> null
    }

    val fileUri = fileToOpen?.let {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
    }

    val imageModifier = Modifier
        .sizeIn(
            minWidth = 250.dp,
            maxWidth = 250.dp,
            minHeight = 250.dp,
            maxHeight = 250.dp
        )
        .clip(RoundedCornerShape(8.dp))
        .border(4.dp, Color.Gray, RoundedCornerShape(8.dp))

    val videoModifier = Modifier
        .sizeIn(
            minWidth = 250.dp,
            maxWidth = 250.dp,
            minHeight = 250.dp,
            maxHeight = 250.dp
        )
        .clip(RoundedCornerShape(8.dp))
        .border(4.dp, Color.Gray, RoundedCornerShape(8.dp))

    Box(
        contentAlignment = Alignment.Center
    ) {
        if (showRecoveryDialog.value) {
            CheckAndRecoverMissingMedia(
                context,
                isSenderMe,
                message,
                fileUri!!,
                downloader,
                chatViewModel,
                downloadedFile,
                downloadingState,
            ) { uri ->
                val encodedUri = Uri.encode(uri.toString())
                navController.navigate("image_viewer/$encodedUri")
            }
        }
        when {
            senderUriExists -> {
                VideoThumbnail(
                    videoUri = Uri.parse(message.localUri),
                    modifier = imageModifier,
                    onClick = {
                        showRecoveryDialog.value = true
                    }
                )
            }

            receiverUriExists -> {
                VideoThumbnail(
                    videoUri = Uri.parse(message.receiverLocalUri),
                    modifier = imageModifier,
                    onClick = {
                        showRecoveryDialog.value = true
                    }
                )
            }

            downloadedFile.value != null -> {
                val uri = downloadedFile.value!!.toUri()
                VideoThumbnail(
                    videoUri = uri,
                    modifier = imageModifier,
                    onClick = {
                        showRecoveryDialog.value = true
                    }
                )
            }

            else -> {
                Box(
                    modifier = videoModifier.background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (downloadingState.value) {
                        DownloadProgress(downloadProgress)
                    } else if (isReceiver) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    downloadingState.value = true
                                    val result = downloader.downloadAndDecrypt(
                                        isSenderMe, message.message, message.mediaIv, message.mimeType, message.fileName
                                    ) { progress ->
                                        chatViewModel.setDownloadProgress(message.messageId, progress)
                                        Log.d("progress", progress.toString())
                                    }
                                    downloadingState.value = false
                                    result.onSuccess { file ->
                                        downloadedFile.value = file
                                        chatViewModel.setReceiverUri(
                                            message.senderId,
                                            message.receiverId,
                                            message.messageId,
                                            file.absolutePath
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_download_24),
                                contentDescription = "Download",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (isSenderMe) {
            if (message.status == MessageStatus.SENDING) {
                UploadProgress(uploadProgress)
            }
        }
    }
}

@Composable
fun FileMessageItem(
    context: Context,
    currentUserId: String,
    message: Message,
    downloadedFile: MutableState<File?>,
    downloadingState: MutableState<Boolean>,
    isSenderMe: Boolean,
    uploadProgress: Float,
    downloadProgress: Float,
    chatViewModel: ChatViewModel,
    downloader: CloudinaryDownloader,
    modifier: Modifier = Modifier
) {

    val receiverUriExists = message.receiverLocalUri.isNotEmpty() && message.senderId != currentUserId
    val senderUriExists = message.localUri.isNotEmpty() && message.senderId == currentUserId
    val isReceiver = !isSenderMe
    val coroutineScope = rememberCoroutineScope()
    val showRecoveryDialog = remember { mutableStateOf(false) }

    val fileToOpen = when {
        senderUriExists -> File(message.localUri)
        receiverUriExists -> File(message.receiverLocalUri)
        downloadedFile.value != null -> downloadedFile.value!!
        else -> null
    }

    val fileUri = fileToOpen?.let {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it)
    }

    if (showRecoveryDialog.value) {
        CheckAndRecoverMissingMedia(
            context,
            isSenderMe,
            message,
            fileUri!!,
            downloader,
            chatViewModel,
            downloadedFile,
            downloadingState,
        ) { uri ->
            openFile(context, uri = uri, mimeType = message.mimeType)
        }
    }

    Row(
        modifier = Modifier
            .sizeIn(minHeight = 80.dp)
            .padding(10.dp)
            .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
            .clickable(enabled = fileUri != null) {
                showRecoveryDialog.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(R.drawable.baseline_attach_file_24), contentDescription = null, tint = Color.White)
        Text(
            text = message.fileName,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(0.7f)
        )

        if (isSenderMe) {
            if (message.status == MessageStatus.SENDING) {
                Box(modifier.weight(0.3f)) {
                    UploadProgress(uploadProgress)
                }
            }
        }

        if (isReceiver && !receiverUriExists && downloadedFile.value == null && !downloadingState.value) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        downloadingState.value = true
                        val result = downloader.downloadAndDecrypt(
                            isSenderMe, message.message, message.mediaIv, message.mimeType, message.fileName
                        ) { progress ->
                            chatViewModel.setDownloadProgress(message.messageId, progress)
                            Log.d("progress", progress.toString())
                        }
                        downloadingState.value = false
                        result.onSuccess { file ->
                            downloadedFile.value = file
                            val absPath = file.absolutePath
                            chatViewModel.setReceiverUri(
                                message.senderId,
                                message.receiverId,
                                message.messageId,
                                absPath
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(0.3f)
            ) {
                Icon(painter = painterResource(R.drawable.baseline_download_24), contentDescription = "Download", tint = Color.White)
            }
        }

        if (downloadingState.value) {
            Box(modifier.weight(0.3f)) {
                DownloadProgress(downloadProgress)
            }
        }
    }
}

@Composable
fun MessageItem(
    navController: NavHostController,
    chatViewModel: ChatViewModel,
    currentUserId: String,
    mediaIv: String,
    message: Message,
    isSenderMe: Boolean,
    onDrag: () -> Unit,
    isHighlighted: Boolean,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val sentIcon = when (message.status) {
        MessageStatus.SENT -> R.drawable.baseline_done_24
        MessageStatus.SENDING -> R.drawable.baseline_access_time_24
        MessageStatus.DELIVERED -> R.drawable.baseline_done_all_24
        MessageStatus.SEEN -> R.drawable.baseline_done_all_blue
        MessageStatus.FAILED -> R.drawable.round_error_outline_24
    }
    val dragOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 150f
    val downloadingState = remember { mutableStateOf(false) }
    val downloadedUri = remember { mutableStateOf<Uri?>(null) }
    val downloadedFile = remember { mutableStateOf<File?>(null) }
    val downloader = remember { CloudinaryDownloader(context) }
    val uploadProgress = chatViewModel.uploadProgressMap[message.messageId] ?: -1f
    val downloadProgress = chatViewModel.downloadProgressMap[message.messageId] ?: -1f

    LaunchedEffect(downloadedFile.value) {
        downloadedFile.value?.let { file ->
            downloadedUri.value = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
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
            }
            .background(
                if (isHighlighted) Color.White.copy(alpha = 0.3f) else Color.Transparent
            )
            .animateContentSize(),
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
                .sizeIn(minWidth = 50.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isSenderMe) Color.Gray else Color(53, 54, 58, 255)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.sizeIn(minWidth = 50.dp)
            ) {

                when (message.messageType) {
                    "text" -> {
                        TextMessageItem(message)
                    }
                    "image" -> {
                        ImageMessageItem(
                            context,
                            currentUserId,
                            message,
                            downloadedFile,
                            downloadingState,
                            navController,
                            isSenderMe,
                            uploadProgress,
                            downloadProgress,
                            chatViewModel,
                            downloader
                        )
                    }

                    "video" -> {
                        VideoMessageItem(
                            context,
                            currentUserId,
                            message,
                            downloadedFile,
                            downloadingState,
                            navController,
                            isSenderMe,
                            uploadProgress,
                            downloadProgress,
                            chatViewModel,
                            downloader
                        )
                    }

                    else -> {
                        FileMessageItem(
                            context,
                            currentUserId,
                            message,
                            downloadedFile,
                            downloadingState,
                            isSenderMe,
                            uploadProgress,
                            downloadProgress,
                            chatViewModel,
                            downloader
                        )
                    }
                }

                if (isSenderMe) {
                    Box(
                        modifier = modifier
                            .size(18.dp)
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(sentIcon),
                            contentDescription = null,
                            modifier.fillMaxSize(),
                            tint = if (message.status == MessageStatus.SEEN) Color(
                                40,
                                255,
                                0,
                                255
                            ) else Color.White
                        )
                    }
                }
            }

        }
    }

}

@Composable
fun CheckAndRecoverMissingMedia(
    context: Context,
    isSenderMe: Boolean,
    message: Message,
    fileUri: Uri,
    downloader: CloudinaryDownloader,
    chatViewModel: ChatViewModel,
    downloadedFile: MutableState<File?>,
    downloadingState: MutableState<Boolean>,
    onUriReady: (Uri) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(fileUri) {
        uriToTempFile(context, fileUri) {success, file ->
            if (success) {
                if (file != null) {
                    if (file.exists()) {
                        onUriReady(fileUri)
                    }
                }
            } else {
                showDialog.value = true
            }
        }

    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("File Missing") },
            text = { Text("This video file is not available on your device. Would you like to re-download it?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    coroutineScope.launch {
                        downloadingState.value = true
                        val result = downloader.downloadAndDecrypt(
                            isSender = isSenderMe,
                            cloudinaryUrl = message.message,
                            iv = message.mediaIv,
                            originalMime = message.mimeType,
                            originalName = message.fileName
                        ) { progress ->
                            chatViewModel.setDownloadProgress(message.messageId, progress)
                        }
                        downloadingState.value = false
                        result.onSuccess { file ->
                            downloadedFile.value = file
                            val absPath = file.absolutePath
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            chatViewModel.setReceiverUri(message.senderId, message.receiverId, message.messageId, absPath)
                            onUriReady(uri)
                        }
                    }
                }) {
                    Text("Download")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun VideoThumbnail(
    videoUri: Uri,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(Color.Black)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(videoUri)
                .videoFrameMillis(1000) // get frame at 1 second
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .size(480) // limit size to improve performance
                .build(),
            imageLoader = ImageLoader.Builder(context)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .build(),
            contentDescription = "Video Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Icon(
            painter = painterResource(id = R.drawable.baseline_play_arrow_24),
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}


@Composable
fun CameraCaptureLauncher(
    openCamera: Boolean,
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri.value != null) {
            onImageCaptured(photoUri.value!!)
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(openCamera) {
        if (openCamera) {
            val file = File.createTempFile("blip_capture_", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            photoUri.value = uri
            cameraLauncher.launch(uri)
        }
    }
}

@Composable
fun VideoCaptureLauncher(
    openVideo: Boolean,
    onVideoCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val videoUri = remember { mutableStateOf<Uri?>(null) }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && videoUri.value != null) {
            onVideoCaptured(videoUri.value!!)
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(openVideo) {
        if (openVideo) {
            val videoFile = File.createTempFile("blip_video_", ".mp4", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                videoFile
            )

            videoUri.value = uri
            videoLauncher.launch(uri)
        }
    }
}

@Composable
fun TypingIndicatorBubble(modifier: Modifier = Modifier) {

    Card(
        modifier
            .padding(
                start = 20.dp,
                end = 60.dp
            )
            .sizeIn(minWidth = 50.dp)
            .clip(RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(
            containerColor =Color(53, 54, 58, 255)
        )
    ) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .background(Color(0xFF444444), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            repeat(3) { index ->
                Dot(index)
            }
        }
    }
}

@Composable
fun Dot(index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        label = "Dot Scale",
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, delayMillis = index * 150),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Color.White, CircleShape)
            .padding(horizontal = 2.dp)
    )
}

@Composable
fun FilePicker(
    navController: NavController,
    friendId: String,
    userName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val isWithInLimits = isFileSizeWithinLimit(context, uri)
            if (!isWithInLimits) {
                Toast.makeText(context, "File size exceeds the limit", Toast.LENGTH_SHORT).show()
            }
            navController.navigate("preview_screen/${Uri.encode(it.toString())}/$friendId/$userName")
        } ?: onDismiss()
    }

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

        val fileExists = File(contentUri.path ?: "").exists()

        if (!fileExists) {
            return
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
