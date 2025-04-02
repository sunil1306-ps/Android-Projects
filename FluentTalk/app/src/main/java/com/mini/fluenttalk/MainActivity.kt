package com.mini.fluenttalk

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.mini.fluenttalk.ui.screens.Navigation
import com.mini.fluenttalk.ui.theme.FluentTalkTheme
import com.mini.fluenttalk.viewModels.LoginViewModel
import com.mini.fluenttalk.viewModels.mainViewModel


class MainActivity : ComponentActivity() {

    private val viewModel = mainViewModel()
    private val loginvm = LoginViewModel()
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        loginvm.user = auth.currentUser
        viewModel.getContacts()
        setContent {
            FluentTalkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    SetStatusBarColor(color = Color.White)
                    Navigation(
                        "splashScreen",
                        navController,
                        { signOut() },
                        { signIn() },
                        { createNewUser() },
                        loginvm,
                        viewModel
                    )
                }
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        loginvm.user = auth.currentUser
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewUser() {

        try {
            auth.createUserWithEmailAndPassword(loginvm.email, loginvm.password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(loginvm.name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.e("cccc", "displayname set successfully")
                                } else {
                                    Log.e("dddd", "Failed to update user profile.", task.exception)
                                }
                            }

                        Toast.makeText(baseContext, "login success", Toast.LENGTH_SHORT).show()
                        Log.e("asdf", "login success")
                        loginvm.user = user
                        loginvm.createUser(loginvm.name, loginvm.email)
                        navController.navigate("logInScreen") {
                            popUpTo ("signUpScreen") {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(baseContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        Log.e("asdf", "login failed")
                    }
                }
        } catch (e: Exception) {
            print(e.message)
        }

    }

    private fun signIn() {

        auth.signInWithEmailAndPassword(loginvm.email, loginvm.password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    if (auth.currentUser != null) {
                        loginvm.user = auth.currentUser
                    }
                    viewModel.userList.clear()
                    viewModel.tempList.clear()
                    viewModel.getContacts()
                    navController.navigate("homeScreen") {
                        popUpTo("logInScreen") {
                            inclusive = true
                        }
                    }
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun signOut() {

        navController.navigate("logInScreen") {
            popUpTo("homeScreen") {
                inclusive = true
            }
        }

        auth.signOut()

    }


}


@Composable
fun SetStatusBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color)
    }
}
