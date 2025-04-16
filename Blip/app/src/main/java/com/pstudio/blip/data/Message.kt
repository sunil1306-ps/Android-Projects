package com.pstudio.blip.data

import android.net.Uri

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val senderUserName: String = "",
    val message: String = "",
    val fileName: String = "",
    val iv: String = "",
    val mediaIv: String = "",
    val localUri: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "text",
    val mimeType: String = "",
    val replyTo: Message? = null,
    val seen: Boolean = false
)