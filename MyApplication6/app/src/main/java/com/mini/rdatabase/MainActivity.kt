package com.mini.rdatabase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mini.rdatabase.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getData()

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val result = viewModel.response.value ) {

                        is DataState.Loading -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Black)
                            }
                        }

                        is DataState.Success -> {

                            viewModel.messages = result.data
                            MainScreen()
                            Log.e("sss", "${result.data}")
                        }

                        is DataState.Failure -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = result.message)
                            }
                        }
                        else -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Error Fetching Data")
                            }
                        }

                    }
                }
            }
        }

    }


//    private fun getData() {
//        databaseReference?.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                Log.e("msg", "onDataChange: $messages")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("msg", "onCancelled: ${error.toException()}")
//            }
//
//        })
//    }


}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val message by remember {
        mutableStateOf(viewModel.messages)
    }

    Column(
        modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
                .background(Color.LightGray)
        ) {
            items(items = message) {
                MessageItem(message = it)
            }
        }


        InputBox(
            leftSend = {msg, isSenderMe ->
                viewModel.setData(msg, isSenderMe)
                viewModel.id++
                viewModel.inputMessage.value = ""
            },
            rightSend = {msg, isSenderMe ->
                viewModel.setData(msg, isSenderMe)
                viewModel.id++
                viewModel.inputMessage.value = ""
            }
        )

    }

}

@Composable
fun MessageItem(message: Message, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isSenderMe!!) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = modifier.padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray
            )
        ) {
            Text(
                text = message.message!!,
                fontSize = 16.sp,
                modifier = modifier.padding(8.dp),
                color = Color.White
            )
        }
    }
}


@Composable
fun InputBox(
    leftSend: (msg: String, isSenderMe: Boolean) -> Unit,
    rightSend: (msg: String, isSenderMe: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {


    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp, max = 150.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        var message by remember {
            mutableStateOf(viewModel.inputMessage.value)
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            shape = RoundedCornerShape(120.dp),
            placeholder = {
                Text(
                    text = "Message",
                    fontSize = 14.sp

                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = modifier
                        .rotate(180.0F)
                        .clickable(enabled = message != "") {
                            leftSend(message, false)
                            Log.e("msg", "$message")
                        }
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = modifier.clickable(enabled = message != "") {
                        rightSend(message, true)
                        Log.e("msg", "$message")
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
//        MainScreen()
    }
}