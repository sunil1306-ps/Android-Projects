package com.pstudio.blip.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pstudio.blip.R
import com.pstudio.blip.ui.theme.BlipTheme
import com.pstudio.blip.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(navController: NavHostController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                Toast.makeText(context, "Signup successful", Toast.LENGTH_SHORT).show()
                navController.navigate("homescreen")
            }
            is AuthViewModel.AuthState.Error -> {
                Toast.makeText(context, (authState as AuthViewModel.AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
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
                modifier = modifier.fillMaxSize().imePadding()
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
                        value = username,
                        onValueChange = { username = it },
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
                        isError = false
                    )

                    Spacer(modifier = modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
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
                        value = password,
                        onValueChange = { password = it },
                        shape = RoundedCornerShape(15.dp),
                        label = {
                            Text(text = "Password", color = Color.White)
                        },
                        modifier = modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        textStyle = TextStyle(
                            color = Color.White
                        ),
                        singleLine = true,
                        isError = false
                    )

                    Spacer(modifier = modifier.height(30.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = modifier.fillMaxWidth()
                    ) {

                        if (authState is AuthViewModel.AuthState.Loading) {
                            CircularProgressIndicator()
                        } else {
                            OutlinedButton(
                                onClick = {
                                    if (email.isBlank() || password.isBlank() || username.isBlank()) {
                                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        authViewModel.signUp(email, password, username)
                                    }
                                },
                                border = BorderStroke(2.dp, Color.Black),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 10.dp,
                                    pressedElevation = 20.dp
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(255, 152, 0, 255)
                                ),
                                modifier = modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text(
                                    text = "SignUp",
                                    color = Color.Black
                                )
                            }
                        }

                    }

                }

                Spacer(modifier = modifier.weight(0.5f))

            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SignUpPreview() {
    //BlipTheme { SignUpScreen() }
}