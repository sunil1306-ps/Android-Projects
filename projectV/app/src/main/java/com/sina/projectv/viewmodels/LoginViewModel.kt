package com.sina.projectv.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sina.projectv.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoginViewModel() : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val auth = Firebase.auth
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    var isUserLoggedIn by mutableStateOf(firebaseAuth.currentUser != null)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val dataViewModel: UserDataViewModel = UserDataViewModel()

    private fun setLoading(isLoading: Boolean) {
        _loadingState.value = isLoading
    }

    fun signInWithGoogle(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                errorMessage = "Google sign-in failed: ${e.message}"
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        try {
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            isUserLoggedIn = true
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Firebase authentication failed: ${e.message}"
        }
    }

    fun createUser(name: String, email: String, mobileno: String, password: String) {
        if (!validateInputs(name, email, mobileno, password)) {
            errorMessage = "Invalid input!"
            return
        }

        viewModelScope.launch {
            setLoading(true)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dataViewModel.saveUserData(name, email, mobileno)
                        isUserLoggedIn = true
                    } else {
                        errorMessage = "Authentication failed: ${task.exception?.message}"
                    }
                }
            setLoading(false)
        }
    }

    private fun validateInputs(name: String, email: String, mobileno: String, password: String): Boolean {
        return name.isNotBlank() && email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) &&
                mobileno.matches(Regex("\\d{10}")) && password.length >= 6
    }

    fun signInWithEmailAndPassword(email: String, password: String) {

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataViewModel.getUserData()
                    isUserLoggedIn = true
                    errorMessage = null
                    Log.e("userl", "User signed in successfully")
                } else {
                    errorMessage = "Authentication failed: ${task.exception?.message}"
                    Log.e("userl", "Authentication failed: ${task.exception?.message}")
                }
            }
        setLoading(false)
    }

    fun userSignOut() {
        auth.signOut()
        isUserLoggedIn = false
        dataViewModel.clearUserData()
    }


    /*********** EMAIL AND PASSWORD SIGN IN METHODS ENDS ***********/


}