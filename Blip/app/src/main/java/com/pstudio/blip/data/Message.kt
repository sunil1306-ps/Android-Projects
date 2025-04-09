package com.pstudio.blip.data

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val senderUserName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "text",
    val replyTo: Message? = null,
    val seen: Boolean = false
)