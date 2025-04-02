package com.sina.projectv.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sina.projectv.R
import com.sina.projectv.ui.theme.ProjectVTheme
import com.sina.projectv.viewmodels.LoginViewModel
import com.sina.projectv.viewmodels.UserDataViewModel

@Composable
fun SignUpScreen(
    viewModel: LoginViewModel,
    dataViewModel: UserDataViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileno by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repass by remember { mutableStateOf("") }
    val isUserLoggedIn = viewModel.isUserLoggedIn
    val isDataFetched = dataViewModel.isDataFetched
    val isLoading by viewModel.loadingState.collectAsState()
    val isDataLoading by dataViewModel.loadingState.collectAsState()

    val isScreenLoading = isLoading && isDataLoading

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result)
    }

    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn) {
            navController.navigate("homescreen") {
                popUpTo("signupscreen") { inclusive = true }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.download),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )

        if (isScreenLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {

            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = modifier
                        .padding(15.dp, 0.dp, 15.dp, 15.dp)
                        .background(Color.Transparent)
                ) {
                    Text(
                        text = "Welcome!",
                        fontSize = 35.sp,
                        modifier = modifier.padding(bottom = 10.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.7f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column {
                            Spacer(modifier.height(20.dp))

                            fun validateAndSetInput(input: String, maxLength: Int): String {
                                return if (input.length <= maxLength) input.filter { it.isLetterOrDigit() } else input
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = validateAndSetInput(it, 50) },
                                placeholder = { Text(text = "Name") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            OutlinedTextField(
                                value = mobileno,
                                onValueChange = { mobileno = it.filter { char -> char.isDigit() } },
                                placeholder = { Text(text = "Mobile Number") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                },
                                placeholder = { Text(text = "Email") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text(text = "Password") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            OutlinedTextField(
                                value = repass,
                                onValueChange = { repass = it },
                                placeholder = { Text(text = "Re-Enter Password") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                            Spacer(modifier.height(20.dp))

                            OutlinedButton(
                                onClick = {
                                    if (password == repass) {
                                        viewModel.createUser(name, email, mobileno, password)
                                    } else {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text(text = "Agree & Continue", color = Color.Black, fontSize = 15.sp)
                            }

                            Spacer(modifier.height(15.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Text(text = "or")
                            }
                            Spacer(modifier.height(15.dp))

                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithGoogle(context, googleSignInLauncher)
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google_icon__1_),
                                    contentDescription = "Google",
                                    modifier = modifier.size(20.dp)
                                )
                                Spacer(modifier.padding(10.dp))
                                Text(text = "Continue with Google", color = Color.Black, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    ProjectVTheme {
//        SignUpScreen()
    }
}