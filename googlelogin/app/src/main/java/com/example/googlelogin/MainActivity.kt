package com.example.googlelogin


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.googlelogin.datastate.DataState
import com.example.googlelogin.logins.EmailLogin
import com.example.googlelogin.screens.LogIn
import com.example.googlelogin.screens.Register
import com.example.googlelogin.screens.Screens
import com.example.googlelogin.screens.UserProfile
import com.example.googlelogin.ui.theme.GoogleLoginTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth
    val emailLogin = EmailLogin()

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContent {
            GoogleLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screens.Register.name
                    ) {
                        composable(Screens.Register.name) {
                            Register(
                                navController = navController,
                                {
                                    emailLogin.register(auth)
                                    setContent { SetData(emailLogin = EmailLogin()) }
                                },

                            )
                        }
                        composable(Screens.LogIn.name) {
                            LogIn(
                                {
                                    emailLogin.login(auth)
                                    setContent {
                                        SetData(emailLogin = EmailLogin())
                                    }
                                },
                                navController = navController
                            )
                        }
                    }

                }
            }
        }
    }


    @Composable
    fun SetData(emailLogin: EmailLogin, modifier: Modifier = Modifier) {

        when (val result = emailLogin.response.value) {
            is DataState.LogIn -> {

            }
            is DataState.Loading -> {
                Box(
                    modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            is DataState.Success -> {
                if (result.user != null) {
                    UserProfile(result.user.email)
                }
            }
            else -> {
                Toast.makeText(this, "Log In Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }


//    public override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        try {
//            if (currentUser != null) {
//                setContent {
//                    val navController = rememberNavController()
//                    GoogleLoginTheme {
//                        UserProfile(navController, currentUser.email)
//                    }
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            Log.d(TAG, "ui update failed ${e.message}")
//        }
//    }

}


