package com.pstudio.blip.ui.theme.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.pstudio.blip.R
import com.pstudio.blip.SetStatusBarColor
import com.pstudio.blip.data.Message
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    Scaffold(
        modifier.fillMaxSize()
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
                                    message.seen,
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
fun ContactHeader(isOnline: Boolean, userName: String, onClick: () -> Unit, modifier: Modifier = Modifier) {

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
    seen: Boolean,
    message: Message,
    isSenderMe: Boolean,
    onDrag: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    val color = if (seen) Color.Green else Color.White
    val dragOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 100f


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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.message,
                    color = if (isSystemInDarkTheme()) if (isSenderMe) Color.White else Color.White else if (isSenderMe) Color.White else Color.White,
                    modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 7.dp, bottom = 7.dp),
                    fontFamily = FontFamily.Monospace
                )
                if (isSenderMe) {
                    Box(
                        modifier.size(10.dp)
                            .background(color)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }

}

@Composable
fun InputBox(chatViewModel: ChatViewModel, receiverId: String, modifier: Modifier = Modifier) {
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
