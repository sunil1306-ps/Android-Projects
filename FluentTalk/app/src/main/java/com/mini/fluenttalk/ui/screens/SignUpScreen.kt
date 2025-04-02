package com.mini.fluenttalk.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mini.fluenttalk.R
import com.mini.fluenttalk.SetStatusBarColor
import com.mini.fluenttalk.viewModels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(signUp: () -> Unit, loginvm: LoginViewModel, navController: NavController, modifier: Modifier = Modifier) {


    SetStatusBarColor(color = Color(27, 29, 56, 255))

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.final_logo),
                        contentDescription = "Logo",
                        modifier = modifier
                            .fillMaxHeight()
                            .padding(start = 10.dp, top = 10.dp),
                        contentScale = ContentScale.Fit
                    )

                }

                Spacer(modifier = modifier.weight(0.5f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = loginvm.name,
                        onValueChange = {
                            loginvm.name = it
                            loginvm.isError = loginvm.name.length > 10
                        },
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(text = "Username", color = Color.White)
                        },
                        modifier = modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        singleLine = true,
                        isError = loginvm.isError
                    )

                    Spacer(modifier = modifier.height(20.dp))

                    OutlinedTextField(
                        value = loginvm.email,
                        onValueChange = { loginvm.email = it },
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(text = "Email", color = Color.White)
                        },
                        modifier = modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = modifier.height(20.dp))

                    OutlinedTextField(
                        value = loginvm.password,
                        onValueChange = {
                            loginvm.password = it
                            loginvm.isError = loginvm.password.length > 8
                        },
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(text = "Password", color = Color.White)
                        },
                        modifier = modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        singleLine = true,
                        isError = loginvm.isError
                    )

                    Spacer(modifier = modifier.height(30.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = modifier.fillMaxWidth()
                    ) {

                        OutlinedButton(
                            onClick =
                            {
                                signUp()
                            },
                            border = BorderStroke(2.dp, Color.Black),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp,
                                pressedElevation = 20.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(255, 152, 0, 255)
                            ),
                            modifier = modifier.fillMaxWidth()
//                            enabled = !loginvm.isError && loginvm.num != "" && loginvm.email != ""
                        ) {
                            Text(
                                text = "SignUp",
                                color = Color.Black
                            )
                        }

                    }

                }

                Spacer(modifier = modifier.weight(0.5f))

            }
        }
    }

}