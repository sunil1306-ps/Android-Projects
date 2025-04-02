package com.mini.fluenttalk.viewModels


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mini.fluenttalk.data.Users


class LoginViewModel: ViewModel() {

    var user: FirebaseUser? = null
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isError by mutableStateOf(false)



    fun createUser(name: String, email: String) {
        val collectionMap = hashMapOf<String, String>(
            "name" to name,
            "email" to email
        )
        try {
            FirebaseDatabase.getInstance().getReference("users").child(name).setValue(collectionMap)
        } catch (e: Exception) {
            Log.e("abcd", "User Creation Failed")
        }
    }

    fun addUser(name: String) {

        FirebaseDatabase.getInstance().getReference("users").child(name).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user1 = snapshot.getValue(Users::class.java)
                    user1?.name = snapshot.child("name").getValue(String::class.java)
                    user1?.email = snapshot.child("email").getValue(String::class.java)

                    val collectionMap = hashMapOf<String, String>(
                        "name" to user1?.name!!,
                        "email" to user1.email!!
                    )
                    val collectionMap1 = hashMapOf<String, String>(
                        "name" to user?.displayName!!,
                        "email" to user?.email!!
                    )
                    Log.e("nmnmnm", collectionMap.toString())

                    try {
                        FirebaseDatabase.getInstance().getReference("users/${user?.displayName}/convs/${user1.name}").setValue(collectionMap)
                        FirebaseDatabase.getInstance().getReference("users/${user1.name}/convs/${user?.displayName}").setValue(collectionMap1)
                    } catch (e: Exception) {
                        Log.e("abcd", "User Creation Failed")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



}