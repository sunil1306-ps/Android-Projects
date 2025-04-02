package com.sina.projectv.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.sina.projectv.data.UserData
import com.sina.projectv.navigation.Navigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserDataViewModel(): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var mobileno by mutableStateOf("")
        private set
    var isDataFetched by mutableStateOf(false)

    fun saveUserData(userName: String, email: String, mobileno: String) {
        val uid = auth.currentUser?.uid ?: return

        val user = UserData(name = userName, email = email, mobileno = mobileno)
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                Log.d("saveuser", "User data saved successfully")
            }
            .addOnFailureListener {
                Log.e("saveuser", "User data save failed: ${it.message}")
            }
    }

    fun getUserData() {
        val uid = auth.currentUser?.uid ?: return
        setLoading(true)

        db.collection("users").document(uid).get()
            .addOnSuccessListener { user ->
                clearUserData()
                username = user["name"]?.toString() ?: ""
                email = user["email"]?.toString() ?: ""
                mobileno = user["mobileno"]?.toString() ?: ""
                isDataFetched = true
            }
            .addOnFailureListener {
                Log.e("getuser", "User data fetch failed: ${it.message}")
            }
            .addOnCompleteListener {
                setLoading(false)
            }
    }

    private fun setLoading(isLoading: Boolean) {
        _loadingState.value = isLoading
    }

    fun clearUserData() {
        username = ""
        email = ""
        mobileno = ""
        isDataFetched = false
    }

}