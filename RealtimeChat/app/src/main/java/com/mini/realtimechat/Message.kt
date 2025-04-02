package com.mini.realtimechat

import androidx.compose.runtime.mutableStateListOf

data class Message(
    var msg: String,
    var isSenderMe: Boolean
)

var messages: MutableList<Message> = mutableStateListOf(

    Message(msg = "Hello", isSenderMe = true) ,
    Message(msg = "Hai", isSenderMe = false),
    Message(msg = "How are you", isSenderMe = true),
    Message(msg = "Good, How about you.", isSenderMe = false),
    Message(msg = "Im great", isSenderMe = true),
    Message(msg = "What's plan tomo?", isSenderMe = true),
    Message(msg = "Nothing much.", isSenderMe = false),
    Message(msg = "Wanna hang out?", isSenderMe = true)
    
)