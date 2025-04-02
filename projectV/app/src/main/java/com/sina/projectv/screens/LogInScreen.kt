package com.sina.projectv.screens


import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    dataViewModel: UserDataViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isUserLoggedIn = viewModel.isUserLoggedIn
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
                popUpTo("loginscreen") {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()

    ) {
        Image(
            painterResource(R.drawable.download),
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
                modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column(
                    modifier
                        .padding(15.dp, 0.dp, 15.dp, 15.dp)
                        .background(Color.Transparent)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp, 25.dp, 10.dp)
                    ){
                        Text(
                            text = "Hi",
                            fontSize = 35.sp
                        )
                    }
                    Spacer(modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.7f), // Adjust transparency as needed
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Column {
                            Spacer(modifier.height(20.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = { Text(text = "Email") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                            )
                            Spacer(modifier.height(10.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text(text = "Password") },
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color(43, 43, 43, 1)
                                ),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                            )
                            Spacer(modifier.height(20.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithEmailAndPassword(email, password)
                                    email = ""
                                    password = ""
                                    Log.d("fdata", "${dataViewModel.isDataFetched}")
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                            ) {
                                Text(text = "Continue", color = Color.Black, fontSize = 15.sp)
                            }
                            Spacer(modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Text(text = "or")
                            }
                            Spacer(modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.signInWithGoogle(context, googleSignInLauncher)
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .padding(10.dp, 0.dp, 10.dp, 0.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google_icon__1_),
                                    contentDescription = "Google",
                                    modifier = modifier.size(20.dp)
                                )
                                Spacer(modifier.padding(10.dp))
                                Text(text = "Continue with Google", color = Color.Black, fontSize = 15.sp)
                            }
                            Spacer(modifier.height(10.dp))
                            Row(
                                modifier.padding(start = 10.dp)
                            ) {
                                Text(text = "Don't have an account? ")
                                Text(
                                    text = "Sign up",
                                    color = Color.Green,
                                    modifier = modifier.clickable {
                                        navController.navigate("signupscreen")
                                    }
                                )
                            }
                            Spacer(modifier.height(10.dp))
                            Text(text = "Forgot your password?", modifier = modifier.padding(start = 10.dp))
                            Spacer(modifier.height(15.dp))
                        }
                    }

                }
            }

        }

    }


}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProjectVTheme {
//        LoginScreen()
    }
}