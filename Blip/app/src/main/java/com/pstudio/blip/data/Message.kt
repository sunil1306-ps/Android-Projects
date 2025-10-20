package com.pstudio.blip.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.google.firebase.database.Exclude

enum class MessageStatus(val priority: Int) {
    SENDING(0),
    SENT(1),
    DELIVERED(2),
    SEEN(3),
    FAILED(-1); // Optional for error handling
}

@Stable
@Immutable
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
    val receiverLocalUri: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "text",
    val mimeType: String = "",
    val replyTo: Message? = null,
    val stealth: Boolean = false,
    val status: MessageStatus = MessageStatus.SENDING
)