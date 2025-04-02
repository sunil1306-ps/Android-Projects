package com.example.authapp.ui.theme



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.authapp.UserIdDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AppViewModel: ViewModel() {
    var inputName by mutableStateOf("")
    var inputPassword by mutableStateOf("")
    private val _userIdDetails = MutableStateFlow(UserIdDetails())
    val userIdDetails: StateFlow<UserIdDetails> = _userIdDetails.asStateFlow()


    fun updateUsername(input: String) {
        inputName = input
    }
    fun updatePassword(input: String) {
        inputPassword = input
    }



}