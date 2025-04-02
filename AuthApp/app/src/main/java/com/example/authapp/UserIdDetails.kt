package com.example.authapp


data class UserIdDetails(
    var name: String = "",
    var username: String = "",
    var password: String = "",
)

object UserDetailsId {
    val user = UserIdDetails()
}