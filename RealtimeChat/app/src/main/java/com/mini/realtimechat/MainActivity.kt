package com.mini.realtimechat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mini.realtimechat.ui.theme.RealtimeChatTheme
import kotlin.math.log

class MainActivity : ComponentActivity() {

    private  var firebaseDatabase: FirebaseDatabase? = null
    private  var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealtimeChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

        firebaseDatabase = Firebase.database
        databaseReference = firebaseDatabase?.getReference("users")
        setData()

    }

    private fun setData() {
        databaseReference?.child("message")?.setValue("Hello World!")
    }

}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    Column(
        modifier.fillMaxSize().background(Color.LightGray).padding(vertical = 10.dp),
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
            items(messages) {message ->
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isSenderMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = modifier.padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray
                        )
                    ) {
                        Text(
                            text = message.msg,
                            fontSize = 16.sp,
                            modifier = modifier.padding(8.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }


        InputBox(leftSend = { /*TODO*/ }, rightSend = { /*TODO*/ })

    }

}


@Composable
fun InputBox(leftSend: () -> Unit, rightSend: () -> Unit, modifier: Modifier = Modifier) {


    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp, max = 150.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        OutlinedTextField(
            value = "",
            onValueChange = { },
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
                        .clickable {
                            leftSend()
                        }
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = modifier.clickable {
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


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RealtimeChatTheme {
        MainScreen()
    }
}

