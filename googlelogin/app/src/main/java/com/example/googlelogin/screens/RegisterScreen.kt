package com.example.googlelogin.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.googlelogin.ui.theme.GoogleLoginTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.googlelogin.Details.User
import com.example.googlelogin.R
import com.example.googlelogin.datastate.DataState

@Composable
fun Register(
    navController: NavController,
    register: ()->Unit,
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
        InputEmail(
            value = emailInput,
            OnValueChange = {
                emailInput = it
                User.Email = it
                            },
            label = "Email",
        )
        Spacer(modifier = modifier.height(15.dp))
        InputPassword(
            value = passwordInput,
            OnValueChange = {
                passwordInput = it
                User.Password = it
                            },
            label = "Password"
        )
        Spacer(modifier = modifier.height(15.dp))
        OutlinedButton(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 15.dp,
                pressedElevation = 5.dp,
                disabledElevation = 0.dp,
                hoveredElevation = 10.dp,
                focusedElevation = 5.dp,
            ),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Black),
            onClick = register,
            modifier = modifier
                .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(
                text = "Register",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = modifier.height(10.dp))
        Row {
            Text(text = "Already registered? ")
            Text(
                text = "log in",
                color = Color.Blue,
                modifier = modifier
                    .clickable { navController.navigate(Screens.LogIn.name) }
            )
        }
        Spacer(modifier = modifier.weight(0.6f))
        Text(text = "-------- or --------")
        Spacer(modifier = modifier.height(10.dp))
        OutlinedButton(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 15.dp,
                pressedElevation = 5.dp,
                disabledElevation = 0.dp,
                hoveredElevation = 10.dp,
                focusedElevation = 5.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color(0xFF2755F7)),
            onClick = { /*TODO*/ },
            modifier = modifier
                .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentScale = ContentScale.FillHeight,
                    contentDescription = "Google Logo",
                    modifier = modifier.fillMaxHeight()
                )
                Spacer(modifier = modifier.weight(.40f))
                Text(
                    text = "Continue with Google",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = modifier.weight(.60f))
            }
        }
        Spacer(modifier = modifier.height(15.dp))
    }
}

@Composable
fun InputEmail(value: String, label: String, OnValueChange:(String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = OnValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun InputPassword(value: String, label: String, OnValueChange:(String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = OnValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoogleLoginTheme {

    }
}