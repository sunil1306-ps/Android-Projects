package com.example.emailauth

data class UserID(
    var Name: String? = null,
    var Password: String? = null
        )

object Credentials {
    var userDetails = UserID()
}
