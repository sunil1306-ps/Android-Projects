package com.mini.fluenttalk.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mini.fluenttalk.R
import com.mini.fluenttalk.SetStatusBarColor
import com.mini.fluenttalk.ui.theme.FluentTalkTheme
import com.mini.fluenttalk.viewModels.LoginViewModel
import com.mini.fluenttalk.viewModels.mainViewModel
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(signOut: () -> Unit, loginvm: LoginViewModel, viewModel: mainViewModel, modifier: Modifier = Modifier, navController: NavController) {

    var searchString by remember{ mutableStateOf("") }
    val contacts by remember { mutableStateOf(viewModel.userList) }


    
    SetStatusBarColor(color = Color.White)

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 20.dp)
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
                modifier = modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = modifier
                        .border(2.dp, Color.Transparent, CircleShape)
                        .padding(start = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.man),
                        contentDescription = "Profile Pic",
                        modifier = modifier.fillMaxHeight()
                    )
                }

                Text(
                    text = if (loginvm.user?.displayName != null) loginvm.user!!.displayName!! else "No Name",
                    modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = modifier.padding(10.dp)
                    .clickable {
                        if (loginvm.user != null) {
                            signOut()
                        }

                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color(255, 152, 0, 255)
                )
            ) {
                Text(
                    text = "FluentTalk",
                    fontSize = 20.sp,
                    modifier = modifier.padding(start = 10.dp, end = 10.dp),
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
                value = searchString,
                onValueChange = { searchString = it},
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text(
                    text = "Search...",
                    fontSize = 14.sp

                )},
                trailingIcon = {
                    Box {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Search"
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
                        Color(255, 152, 0, 255),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add",
                    modifier = modifier.clickable{
                        if (searchString != "") {
                            loginvm.addUser(searchString)
                            searchString = ""
                        }
                    }
                )
            }
        }

        Text(
            text = "Favourites",
            modifier = modifier.padding(start = 20.dp, top = 10.dp)
        )
        Spacer(modifier = modifier.height(10.dp))
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
                        Text(
                            text = "name",
                            modifier = modifier.padding(bottom = 10.dp),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = modifier.width(15.dp))
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        ) {
            items(contacts) {contact ->
                ContactCard(
                    viewModel,
                    contact.name!!,
                    onClick = {
                        navController.navigate("chatScreen")
                        viewModel.selectedContact = contact.name!!
                        viewModel.getData()
                })
            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContactCard(viewModel: mainViewModel, userName: String, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
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
                    painter = painterResource(id = R.drawable.man),
                    contentDescription = "Profile Pic",
                    modifier = modifier.height(40.dp)
                )
            }

            Column {
                Text(
                    text = userName,
                    modifier.padding(start = 20.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = if (viewModel.tempList.size != 0) "${viewModel.tempList[viewModel.tempList.size - 1].text}" else "Say Hello..!",
                    modifier = modifier.padding(start = 20.dp),
                    fontSize = 14.sp
                )

            }
        }

        Text(
            text = "${LocalDate.now()}"
        )

    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    FluentTalkTheme {
//        HomeScreen(navController = navController)
    }
}