package com.example.googlelogin.datastate


import com.google.firebase.auth.FirebaseUser


sealed class DataState {
    class Success(val user: FirebaseUser?): DataState()
    object Register: DataState()
    object LogIn: DataState()
    object Loading: DataState()
    object Empty: DataState()
}
