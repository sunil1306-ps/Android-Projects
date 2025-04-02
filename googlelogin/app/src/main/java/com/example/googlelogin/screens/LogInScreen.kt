package com.example.googlelogin.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.googlelogin.Details.User
import com.example.googlelogin.R
import com.example.googlelogin.logins.EmailLogin
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogIn(
    logIn: ()->Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = modifier.height(15.dp))
        Row(
            modifier
                .fillMaxWidth()
                .height(75.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.baseline_fingerprint_24),
                contentDescription = null,
                modifier.height(75.dp)
            )
            Text(
                text = "LogIn App",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = modifier.weight(.40f))
        Box(
            modifier = modifier
                .border(2.dp, Color.Black, CircleShape)
                .size(125.dp),
        ){
            Image(painter = painterResource(id = R.drawable.baseline_fingerprint_24),
                modifier = modifier
                    .align(Alignment.Center)
                    .size(100.dp),
                contentDescription = null
            )
        }
        Spacer(modifier = modifier.height(15.dp))
        EmailInput(
            value = emailInput,
            OnValueChange = {
                emailInput = it
                User.Email = it
                            },
            label = "Email",
        )
        Spacer(modifier = modifier.height(15.dp))
        PasswordInput(
            value = passwordInput,
            OnValueChange = {
                passwordInput = it
                User.Password = it
                            },
            label = "Password"
        )
        Spacer(modifier = modifier.height(20.dp))
        OutlinedButton(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 15.dp,
                pressedElevation = 5.dp,
                disabledElevation = 0.dp,
                hoveredElevation = 10.dp,
                focusedElevation = 5.dp,
            ),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Black),
            onClick = logIn,
            modifier = modifier
                .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(
                text = "Log In",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = modifier.weight(0.6f))
    }
}

@Composable
fun EmailInput(value: String, label: String, OnValueChange:(String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = OnValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun PasswordInput(value: String, label: String, OnValueChange:(String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = OnValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}