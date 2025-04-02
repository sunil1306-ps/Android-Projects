package com.mini.fluenttalk.viewModels


import com.google.firebase.database.FirebaseDatabase

fun addUsers(num: String) {
    for (i in 1..10) {
        val x = (1111111111..9999999999).random().toString()
        FirebaseDatabase.getInstance().getReference("users").child(num).child("convs").child(x).child("email").setValue("test@gmail.com")
        FirebaseDatabase.getInstance().getReference("users").child(num).child("convs").child(x).child("phoneNo").setValue(x)
    }
}