package com.mini.fluenttalk.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.mlkit.nl.smartreply.TextMessage
import com.mini.fluenttalk.R
import com.mini.fluenttalk.data.Message
import com.mini.fluenttalk.ui.theme.FluentTalkTheme
import com.mini.fluenttalk.viewModels.mainViewModel
import kotlin.math.abs


@Composable
fun ChatScreen(viewModel: mainViewModel, navController: NavHostController, modifier: Modifier = Modifier) {

    val listState = rememberLazyListState()

    val messages by remember {
        mutableStateOf(viewModel.tempList)
    }
    val size = messages.size + 1

    Scaffold(
        modifier.fillMaxSize(),
        topBar = {
            ContactHeader(viewModel, { navController.navigate("homeScreen") })
        }
    ) {
        Column(
            modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()) {

            Box(modifier = modifier
                .fillMaxSize()
                .weight(1f)) {
                LazyColumn(
                    modifier.padding(it),
                    state = listState
                ) {
                    items(messages) { message ->
                        MessageItem(msg = message.text!!, isSenderMe = message.isSenderMe!!)
                    }
                }

                LaunchedEffect(listState) {
                    listState.scrollToItem(abs(messages.size - 1))
                }

                LaunchedEffect(messages.size) {
                    listState.animateScrollToItem(abs(messages.size - 1))
                }


            }

            Box(modifier = modifier.fillMaxWidth()) {
                InputBox(
                    viewModel,
                    {
                        val newMessage = Message(false, viewModel.inputMessage.trim())
                        viewModel.setData(newMessage, size)
                        if (viewModel.tempList.isNotEmpty()) {
                            viewModel.conversations.add(TextMessage.createForLocalUser(viewModel.inputMessage, System.currentTimeMillis()))
                        }
                        viewModel.inputMessage = ""
                        viewModel.getData()
                    },

                )
            }

        }

    }

}

@Composable
fun ContactHeader(viewModel: mainViewModel, onClick: () -> Unit, modifier: Modifier = Modifier) {

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
                    painter = painterResource(id = R.drawable.man),
                    contentDescription = "Profile Pic",
                    modifier = modifier
                        .height(50.dp)
                        .shadow(20.dp, CircleShape, true, Color.Black, Color.Black)
                        .clip(CircleShape)
                        .border(2.dp, Color.Green, CircleShape)
                )
            }

            Text(
                text = viewModel.selectedContact,
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
fun MessageItem(msg: String, isSenderMe: Boolean, modifier: Modifier = Modifier) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
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
                    top = 7.dp,
                    bottom = 7.dp,
                    end = if (isSenderMe) 20.dp else 60.dp
                )
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isSenderMe) Color(255, 152, 0, 255) else Color(53, 54, 58, 255)
            )
        ) {
            Text(
                text = msg,
                color = if (isSystemInDarkTheme()) if (isSenderMe) Color.Black else Color.White else if (isSenderMe) Color.Black else Color.White,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 7.dp, bottom = 7.dp),
                fontFamily = FontFamily.Monospace
            )
        }
    }

}

@Composable
fun InputBox(viewModel: mainViewModel,/* leftSend: () -> Unit,*/ rightSend: () -> Unit, modifier: Modifier = Modifier) {

    Column(
        modifier.fillMaxWidth()
    ) {
        LazyRow() {

            items(viewModel.suggestionResult) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            viewModel.inputMessage = it
                        }
                ) {
                    Card(
                        modifier
                            .padding(
                                7.dp
                            )
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(255, 152, 0, 255)
                        )
                    ) {
                        Text(
                            text = it,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 7.dp, bottom = 7.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

        }
        Row(
            modifier
                .fillMaxWidth()
                .heightIn(min = 30.dp, max = 150.dp)
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedTextField(
                value = viewModel.inputMessage,
                onValueChange = {
                    viewModel.inputMessage = it
                    if (viewModel.tempList.isNotEmpty()) {
                        viewModel.smartReplyGenerator()
                    }
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
                        imageVector = Icons.Default.Send,
                        contentDescription = "",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = modifier.clickable(enabled = viewModel.inputMessage != "") {
                            rightSend()
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

}




@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    FluentTalkTheme {
//        MessageItem(msg = "hai hello namaste", isSenderMe = true)
    }
}