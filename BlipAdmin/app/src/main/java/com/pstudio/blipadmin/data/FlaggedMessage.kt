package com.pstudio.blipadmin.data

data class FlaggedMessage(
    val messageId: String = "",
    val message: String = "",
    val senderId: String = "",
    val senderUsername: String = "",
    val receiverId: String = "",
    val receiverUsername: String = "",
    val timestamp: Long = 0L
)