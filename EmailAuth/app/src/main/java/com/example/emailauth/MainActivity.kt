package com.example.emailauth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emailauth.ui.theme.EmailAuthTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            EmailAuthTheme {
                LogIn(
                    onSignInClick = { signInUser() },
                    onRegisterClick = { createNewUser() })
            }
        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }


    private fun createNewUser() {
        auth.createUserWithEmailAndPassword(Credentials.userDetails.Name!!, Credentials.userDetails.Password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signInUser() {
        auth.signInWithEmailAndPassword(Credentials.userDetails.Name!!, Credentials.userDetails.Password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            setContent {
                LogIn(
                    name = user.email,
                    onSignInClick = { signInUser() },
                    onRegisterClick = { createNewUser() }
                )
            }
        }else {
            setContent{
                LogIn(
                    name = null,
                    onSignInClick = { signInUser() },
                    onRegisterClick = { createNewUser() }
                )
            }
        }
    }

}




@Composable
fun LogIn(
    onSignInClick: ()->Unit,
    onRegisterClick: ()->Unit,
    modifier: Modifier = Modifier,
    name: String? = null,
) {
    var inputName by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
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
            onValueChange = {
                inputName = it
                Credentials.userDetails.Name = it
            }
        )
        Spacer(modifier = modifier.height(16.dp))
        PasswordInput(
            label = "Password",
            value = inputPassword,
            onValueChange = {
                inputPassword = it
                Credentials.userDetails.Password = it
            }
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
fun UserDetails(modifier: Modifier = Modifier, name: String? = null) {
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
        modifier = modifier.fillMaxWidth(),
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
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EmailAuthTheme {
        LogIn(
            onSignInClick = {  },
            onRegisterClick = {  }
        )
    }
}