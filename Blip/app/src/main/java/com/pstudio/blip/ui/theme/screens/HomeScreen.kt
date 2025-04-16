package com.pstudio.blip.ui.theme.screens

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pstudio.blip.AppStateManager
import com.pstudio.blip.R
import com.pstudio.blip.SetStatusBarColor
import com.pstudio.blip.ui.theme.BlipTheme
import com.pstudio.blip.utilclasses.formatTimestamp
import com.pstudio.blip.viewmodels.AuthViewModel
import com.pstudio.blip.viewmodels.ChatUiState
import com.pstudio.blip.viewmodels.ChatViewModel
import com.pstudio.blip.viewmodels.FetchState
import com.pstudio.blip.viewmodels.UserViewModel
import java.time.LocalDate
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val application = LocalContext.current.applicationContext as Application
    val authState by authViewModel.authState.collectAsState()
    val username = (authState as? AuthViewModel.AuthState.Success)?.username?: "Unknown"
    val userId = (authState as? AuthViewModel.AuthState.Success)?.userId?: "Unknown"
    val fetchState by userViewModel.fetchState.collectAsState()
    val friendsList by userViewModel.friendsList.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var messageOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedFriendId by remember { mutableStateOf<String?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    SetStatusBarColor(Color.Black)

    LaunchedEffect(userId) {
        userId.let {
            // Only fetch if friends list is empty
            if (userViewModel.friendsList.value.isEmpty()) {
                userViewModel.fetchFriendsList(it)
            }

            // Start the listener
            userViewModel.listenForFriends(it)
            AppStateManager.register(application = application, uid = userId)
        }

        // Load chat messages
        chatViewModel.fetchAllChatsForCurrentUser()
    }


    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding()
            .background(Color.Black)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = modifier.fillMaxHeight()
                    .background(Color.Black),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = modifier
                        .padding(start = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.doreamon),
                        contentDescription = "Profile Pic",
                        contentScale = ContentScale.Crop,
                        modifier = modifier.size(45.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Green, CircleShape)
                    )
                }

                Text(
                    text = username,
                    color = Color.White,
                    modifier = modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = modifier
                    .padding(10.dp)
                    .clickable {
                        authViewModel.logout()
                        userViewModel.clearState()
                        userViewModel.clearFriendListener(userId)
                        navController.navigate("loginscreen") {
                            popUpTo("homescreen") { inclusive = true }
                        }
                        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.Gray
                )
            ) {
                Text(
                    text = "YakYak",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = modifier.padding(start = 10.dp, end = 10.dp)
                        .padding(vertical = 2.dp),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(modifier = modifier.height(20.dp))

        Row(
            modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it},
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text(
                    text = "Search...",
                    fontSize = 14.sp

                )},
                trailingIcon = {
                    Box {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Search",
                            modifier = modifier.clickable {
                                // TODO: Search friend function call space
                            },
                            tint = Color.White
                        )
                    }
                },
                modifier = modifier
                    .weight(1f)
            )
            Spacer(modifier = modifier.width(30.dp))
            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .background(
                        Color.Gray,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add",
                    modifier = modifier.clickable{
                        if (searchText.isNotBlank()) {
                            userViewModel.addFriendByUsername(userId, username, searchText.trim()) {success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    searchText = ""
                                    userViewModel.fetchFriendsList(userId)
                                }
                            }
                        }
                    },
                    tint = Color.White
                )
            }
        }

        Text(
            text = "Favourites",
            color = Color.White,
            modifier = modifier.padding(start = 20.dp, top = 10.dp)
        )
        Spacer(modifier = modifier.height(10.dp))

        when (fetchState) {
            is FetchState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator(
                        modifier = modifier.width(100.dp),
                        color = Color.Red,
                        trackColor = Color.Gray,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            is FetchState.Success -> {
                if (friendsList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No friends found", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Row(
                        modifier = modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 20.dp)
                    ) {
                        repeat(10) {
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 10.dp
                                ),
                            ) {
                                Box(
                                    modifier = modifier
                                        .height(130.dp)
                                        .width(100.dp),
                                    contentAlignment = BottomCenter
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.cute_naruto_jtmjd4ifiqi7a48s),
                                        contentDescription = null,
                                        modifier = modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(15.dp)),
                                        contentScale = ContentScale.Crop,
                                    )

                                }
                            }
                            Spacer(modifier = modifier.width(15.dp))
                        }
                    }
                    Spacer(modifier.height(10.dp))
                    LazyColumn {
                        items(friendsList) { friend ->
                            Box(
                                modifier = modifier
                                    .onGloballyPositioned { coordinates ->
                                        // Save the position of this message on screen
                                        val position = coordinates.localToWindow(Offset.Zero)
                                        messageOffset = position
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                selectedFriendId = friend.uid
                                                showMenu = true
                                            }
                                        )
                                    }
                            ) {

                                val messages = chatViewModel.chats[friend.uid]
                                val lastMessage = messages?.lastOrNull()

                                val messageText = lastMessage?.message ?: "No messages yet"
                                val time = lastMessage?.timestamp?.let { formatTimestamp(it) } ?: ""

                                ContactCard(
                                    userName = friend.username,
                                    onClick = {
                                        navController.navigate("chatscreen/${friend.uid}/${friend.username}")

                                    },
                                    lastMessage = messageText,
                                    timeStamp = time
                                )

                                if (showMenu && selectedFriendId == friend.uid) {
                                    DropdownMenu(
                                        expanded = true,
                                        onDismissRequest = {
                                            showMenu = false
                                            selectedFriendId = null
                                        },
                                        offset = DpOffset(
                                            x = 150.dp, // Adjust X and Y based on your bubble alignment
                                            y = 0.dp
                                        )
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Delete") },
                                            onClick = {
                                                showMenu = false
                                                userViewModel.deleteFriend(userId, friend.uid)
                                            }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
            is FetchState.Error -> {
                val errorMessage = (fetchState as FetchState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $errorMessage", color = Color.Red)
                }
            }
            else -> {}
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContactCard(
    userName: String,
    onClick: () -> Unit,
    lastMessage: String,
    timeStamp: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(5.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = modifier.padding(start = 10.dp)
        ) {
            Box(
                modifier = modifier
                    .border(2.dp, Color.Transparent, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.doreamon),
                    contentDescription = "Profile Pic",
                    contentScale = ContentScale.Crop,
                    modifier = modifier.size(40.dp)
                        .clip(CircleShape)
                )
            }

            Column {
                Text(
                    text = userName,
                    color = Color.White,
                    modifier = modifier.padding(start = 20.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = lastMessage,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier.padding(start = 20.dp),
                    fontSize = 14.sp
                )

            }
        }

        Text(
            text = timeStamp,
            color = Color.White,
            fontSize = 12.sp
        )

    }

}


