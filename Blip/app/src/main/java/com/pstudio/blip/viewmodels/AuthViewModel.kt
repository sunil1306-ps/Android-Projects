package com.pstudio.blip.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState>  = _authState

    private val database = FirebaseDatabase.getInstance().reference.child("users")

    init {
        checkLoggedInUser()  // Automatically fetch user details on startup
    }

    private fun checkLoggedInUser() {
        _authState.value = AuthState.Loading
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.child(userId).child("username").get()
                .addOnSuccessListener { snapshot ->
                    val username = snapshot.value as? String ?: "Unknown"
                    _authState.value = AuthState.Success(userId, username)
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error("Failed to fetch user data")
                }
        } else {
            _authState.value = AuthState.Error("No user logged in")
        }
    }

    // Signup function
    fun signUp(email: String, password: String, username: String) {

        val playerId = OneSignal.User.pushSubscription.id

        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _authState.value = AuthState.Error("All fields are required!")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(username).build())
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                _authState.value = AuthState.Success(user.uid, user.displayName ?: "Unknown")
                            } else {
                                _authState.value = AuthState.Error(it.exception?.message ?: "Profile update failed")
                            }
                        }
                    user?.let {
                        val userId = it.uid
                        val userMap = mapOf(
                            "username" to username,
                            "email" to email,
                            "friends" to emptyMap<String, Boolean>(),
                            "playerId" to playerId
                        )

                        database.child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Success(user.uid, username)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error(e.message ?: "Failed to store user data")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Signup failed")
                }
            }

    }

    // Login function
    fun login(email: String, password: String) {

        val playerId = OneSignal.User.pushSubscription.id
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty!")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        database.child(user.uid).child("playerId").setValue(playerId)
                        _authState.value = AuthState.Success(user.uid, user.displayName ?: "Unknown")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    // Logout function
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val userId: String, val username: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

}