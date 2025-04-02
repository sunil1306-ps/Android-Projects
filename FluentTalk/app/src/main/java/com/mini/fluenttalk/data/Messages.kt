package com.mini.fluenttalk.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList


data class Message(
    var isSenderMe: Boolean? = null,
    var text: String? = null
)

data class Contacts (
    var email: String? = null,
    var phoneNo: String? = null
)

data class Messages (
    var messagesList: SnapshotStateList<Message>? = mutableStateListOf()
)

data class Users (
    var name: String? = null,
    var email: String? = null
)
