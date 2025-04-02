package com.example.authapp


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authapp.ui.theme.AppViewModel
import com.example.authapp.ui.theme.AuthAppTheme

@Composable
fun MainScree (
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(),
    registerClick: ()-> Unit,
    signInClick: () -> Unit,
) {
    val appUiState by appViewModel.userIdDetails.collectAsState()
    LogIn(
        name = appUiState.username,
        inputName = appViewModel.inputName,
        updateUsername = { appViewModel.updateUsername(it) },
        inputPassword = appViewModel.inputPassword,
        updatePassword = { appViewModel.updatePassword(it) },
        onRegisterClick = { registerClick },
        onSignInClick = { signInClick }
    )


}


@Composable
fun LogIn(
    name: String? = null,
    inputName: String,
    updateUsername: (String)->Unit,
    inputPassword: String,
    updatePassword: (String)->Unit,
    onSignInClick: ()->Unit,
    onRegisterClick: ()->Unit,
    modifier: Modifier = Modifier
) {



    Column(
        modifier
            .padding(25.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserDetails(name = name)
        Spacer(modifier = modifier.weight(0.5f))
        Box(
            modifier = modifier
                .border(2.dp, Color.Black, CircleShape)
                .size(150.dp),
        ){
            Image(painter = painterResource(
                id = R.drawable.baseline_fingerprint_24),
                modifier = modifier
                    .align(Alignment.Center)
                    .size(100.dp),
                contentDescription = null
            )
        }
        Spacer(modifier = modifier.height(16.dp))
        UserNameInput(
            label = "Username",
            value = inputName,
            onValueChange = updateUsername
        )
        Spacer(modifier = modifier.height(16.dp))
        PasswordInput(
            label = "Password",
            value = inputPassword,
            onValueChange = updatePassword
        )
        Spacer(modifier = modifier.height(16.dp))
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onSignInClick) {
                Text("Sign in")
            }
            Button(onClick = onRegisterClick) {
                Text("Register")
            }
        }
        Spacer(modifier = modifier.weight(1f))
    }
}

@Composable
fun UserDetails(name: String? = null, modifier: Modifier = Modifier) {
    Column(modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Name: $name",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UserNameInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun PasswordInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AuthAppTheme {
        //LogIn("", "")
    }
}