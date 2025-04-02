package com.example.googlelogin.logins

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.googlelogin.Details.User
import com.example.googlelogin.datastate.DataState
import com.google.firebase.auth.FirebaseAuth


class EmailLogin: ComponentActivity() {

    val response: MutableState<DataState> = mutableStateOf(DataState.Register)


    fun register(auth: FirebaseAuth) {
        response.value = DataState.Loading
        auth.createUserWithEmailAndPassword(User.Email!!, User.Password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(baseContext, "SignUp success", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    response.value = DataState.Success(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "SignUp failed", Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun login(auth: FirebaseAuth) {

        response.value = DataState.Loading
        auth.signInWithEmailAndPassword(User.Email!!, User.Password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    Toast.makeText(baseContext, "logIn success", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    response.value = DataState.Success(user)
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "logIn failed", Toast.LENGTH_SHORT).show()
                }
            }

    }

}