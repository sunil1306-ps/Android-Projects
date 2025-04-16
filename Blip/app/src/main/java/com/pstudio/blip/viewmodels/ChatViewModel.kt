package com.pstudio.blip.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pstudio.blip.data.Message
import com.pstudio.blip.utilclasses.AESUtils
import com.pstudio.blip.utilclasses.AESUtils.decryptIfNeeded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

sealed class ChatUiState {
    object Idle: ChatUiState()
    object Loading: ChatUiState()
    object Success: ChatUiState()
    data class Error(val message: String): ChatUiState()
}

class ChatViewModel: ViewModel() {

    private val rest = "os_v2_app_6i3kqawudbhqtjbq2si7dcejkw3qlliq4rhedwu4wbujhygxpmsvjrpestw32tkamicvxh2artkitqbqvdh76zr4iypzqk6tpakpbha"

    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState

    private val _editingMessage = mutableStateOf<Message?>(null)
    val editingMessage: State<Message?> = _editingMessage

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _chats = mutableStateMapOf<String, MutableList<Message>>()
    val chats: SnapshotStateMap<String, MutableList<Message>> = _chats

    private val _replyingToMessage = mutableStateOf<Message?>(null)
    val replyingToMessage: State<Message?> = _replyingToMessage

    private val _isOnline = MutableStateFlow<Boolean>(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val _activeChatUserId = MutableStateFlow<String?>(null)
    val activeChatUserId: StateFlow<String?> = _activeChatUserId

    init {
        fetchAllChatsForCurrentUser()
    }

    fun setActiveChatUserId(userId: String?) {
        _activeChatUserId.value = userId
    }

    fun startReplying(message: Message) {
        _replyingToMessage.value = message
    }

    fun cancelReplying() {
        _replyingToMessage.value = null
    }

    fun startEditingMessage(message: Message) {
        _editingMessage.value = message
        Log.d("replyto", "reply started")
    }

    fun cancelEditing() {
        _editingMessage.value = null
    }

    fun fetchAllChatsForCurrentUser() {

        val currentUserId = auth.currentUser?.uid

        if (currentUserId.isNullOrEmpty()) {
            _chatUiState.value = ChatUiState.Error("User not logged in")
            return
        }

        _chatUiState.value = ChatUiState.Loading

        dbRef.child("chats").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                _chats.clear()  // Clear old data
                for (chatSnap in snapshot.children) {

                    val chatId = chatSnap.key ?: continue
                    if (!chatId.contains(currentUserId)) continue

                    val messages = mutableListOf<Message>()
                    val messageSnapshot = chatSnap.child("messages")

                    for (messageSnap in messageSnapshot.children) {
                        val message = messageSnap.getValue(Message::class.java)
                        message?.let {
                            val msgWithId = it.copy(messageId = messageSnap.key ?: "")
                            val decryptedMessage = msgWithId.decryptIfNeeded()

                            messages.add(decryptedMessage)
                        }
                    }

                    val otherUserId = chatId.split("_").firstOrNull { it != currentUserId } ?: continue
                    _chats[otherUserId] = messages

                }
                _chatUiState.value = ChatUiState.Success
                Log.e("fetched chats", chats.size.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                _chatUiState.value = ChatUiState.Error(error.message)
            }

        })

    }

    private var onlineStatusListener: ValueEventListener? = null
    fun listenToUserOnlineStatus(userId: String) {
        val onlineRef = dbRef.child("users").child(userId).child("online")

        // Remove existing listener if already attached
        onlineStatusListener?.let { onlineRef.removeEventListener(it) }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isReceiverOnline = snapshot.getValue(Boolean::class.java) ?: false
                _isOnline.value = isReceiverOnline
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OnlineStatus", "Error: ${error.message}")
            }
        }

        onlineRef.addValueEventListener(listener)
        onlineStatusListener = listener
    }

    fun removeUserOnlineStatusListener(userId: String) {
        val onlineRef = dbRef.child("users").child(userId).child("online")
        onlineStatusListener?.let {
            onlineRef.removeEventListener(it)
            onlineStatusListener = null
        }
    }

    fun sendMessage(
        receiverId: String,
        senderUserName: String,
        messageText: String,
        messageType: String = "text",
        mediaIv: String = "",
        mimeType: String = "",
        fileName: String = "",
        localUri: String = ""
    ) {
        val senderId = auth.currentUser?.uid ?: return
        val replyTo = _replyingToMessage.value

        if (messageText.isBlank()) return

        val chatId = generateChatId(senderId, receiverId)
        val messageId = dbRef.push().key ?: UUID.randomUUID().toString()

        val (encryptedMessage, iv) = AESUtils.encrypt(messageText)

        val message = Message(
            messageId = messageId,
            message = encryptedMessage,
            iv = iv,
            mediaIv = mediaIv,
            fileName = fileName,
            localUri = localUri,
            senderId = senderId,
            senderUserName = senderUserName,
            receiverId = receiverId,
            timestamp = System.currentTimeMillis(),
            messageType = messageType,
            mimeType = mimeType,
            replyTo = replyTo
        )

        val decryptedMessage = message.decryptIfNeeded()

        val existingMessages = _chats[receiverId]?.toMutableList() ?: mutableListOf()
        existingMessages.add(decryptedMessage)
        _chats[receiverId] = existingMessages
//        _chatUiState.value = ChatUiState.Success

        dbRef.child("chats")
            .child(chatId)
            .child("messages")
            .child(messageId)
            .setValue(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message sent successfully")

                FirebaseDatabase.getInstance().getReference("users/$receiverId/playerId").get()
                    .addOnSuccessListener { snapshot ->
                        val playerId = snapshot.getValue(String::class.java)
                        if (playerId != null) {
                            dbRef.child("users").child(receiverId).child("online")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val isReceiverOnline = snapshot.getValue(Boolean::class.java) ?: false
                                        if (!isReceiverOnline) {
                                            sendNotificationToReceiver(playerId, messageText, senderUserName)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("Notify", "Failed to check receiver's online state: ${error.message}")
                                    }
                                })
                        }
                    }

                _chatUiState.value = ChatUiState.Success
            }
            .addOnFailureListener {
                Log.e("ChatViewModel", "Failed to send message", it)
                _chatUiState.value = ChatUiState.Error("Failed to send message")
            }

        cancelReplying()
    }


    fun markMessagesAsSeen(
        currentUserId: String,
        friendId: String
    ) {
        val chatId = generateChatId(currentUserId, friendId)
        val messagesRef = dbRef.child("chats").child(chatId).child("messages")

        messagesRef.orderByChild("receiverId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (messageSnap in snapshot.children) {
                        val message = messageSnap.getValue(Message::class.java)
                        if (message != null && !message.seen) {
                            // Only mark unseen messages as seen
                            messageSnap.ref.child("seen").setValue(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SeenStatus", "Failed to mark as seen: ${error.message}")
                }
            })
    }

    private fun generateChatId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "${user1Id}_$user2Id" else "${user2Id}_$user1Id"
    }

    private var messageListeners = mutableMapOf<String, ChildEventListener>()
    fun listenForMessages(
        friendId: String,
        currentUserId: String
    ) {
        val chatId = generateChatId(friendId, currentUserId)
        val messagesRef = dbRef.child("chats").child(chatId).child("messages")

        // Avoid duplicate listeners
        if (messageListeners.containsKey(chatId)) return

        val listener = messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                val decryptedMessage = message?.decryptIfNeeded()
                decryptedMessage?.let {
                    val existingMessages = chats[friendId]?.toMutableList() ?: mutableListOf()

                    // Avoid adding duplicates (based on message ID or timestamp)
                    val alreadyExists = existingMessages.any {
                        it.timestamp == decryptedMessage.timestamp ||
                        it.messageId == decryptedMessage.messageId
                    }

                    if (!alreadyExists) {
                        existingMessages.add(decryptedMessage)
                        chats[friendId] = existingMessages
                        _chatUiState.value = ChatUiState.Success
                    }
                    val isInChatWithFriend = activeChatUserId.value == friendId
                    if (decryptedMessage.receiverId == currentUserId && !decryptedMessage.seen && isInChatWithFriend) {
                        markMessagesAsSeen(currentUserId, friendId)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java)
                val decryptedMessage = updatedMessage?.decryptIfNeeded()
                decryptedMessage?.let { newMsg ->
                    val existingMessages = chats[friendId]?.toMutableList() ?: return

                    val index = existingMessages.indexOfFirst { it.messageId == newMsg.messageId }
                    if (index != -1) {
                        existingMessages[index] = newMsg
                        chats[friendId] = existingMessages
                        _chatUiState.value = ChatUiState.Success // Trigger UI update
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val deletedMessage = snapshot.getValue(Message::class.java)
                deletedMessage?.let { message ->

                    val existingMessages = chats[friendId]?.toMutableList() ?: return
                    val updatedMessages = existingMessages.filterNot { it.messageId == message.messageId }
                    chats[friendId] = updatedMessages.toMutableList()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                _chatUiState.value = ChatUiState.Error(error.message)
            }
        })

        messageListeners[chatId] = listener
    }

    fun deleteMessage(
        friendId: String,
        messageId: String
    ) {

        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = generateChatId(friendId, currentUserId)

        val msgRef = dbRef.child("chats").child(chatId).child("messages").child(messageId)

        msgRef.removeValue()
            .addOnSuccessListener {
                val updatedMessages = _chats[friendId]?.filterNot { it.messageId == messageId } ?: emptyList()
                chats[friendId] = updatedMessages.toMutableList()
            }

    }

    fun editMessage(
        friendId: String,
        newText: String
    ) {
        val messageToEdit = _editingMessage.value ?: return
        val chatId = generateChatId(messageToEdit.senderId, messageToEdit.receiverId)

        dbRef.child("chats").child(chatId).child("messages").child(messageToEdit.messageId)
            .child("message").setValue(newText)
            .addOnSuccessListener {
                // Update local state
                chats[friendId]?.let { list ->
                    val updatedList = list.map {
                        if (it.messageId == messageToEdit.messageId) it.copy(message = newText) else it
                    }
                    chats[friendId] = updatedList.toMutableList()
                }
                _editingMessage.value = null
            }
        cancelEditing()
    }

    private fun sendNotificationToReceiver(
        playerId: String,
        message: String,
        senderUsername: String
    ) {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("app_id", "f236a802-d418-4f09-a430-d491f1888955") // Your OneSignal App ID
            put("include_player_ids", JSONArray().put(playerId))
            put("headings", JSONObject().put("en", senderUsername))
            put("contents", JSONObject().put("en", message))
            put("data", JSONObject().apply {
                put("senderUsername", senderUsername)
                put("senderId", FirebaseAuth.getInstance().currentUser?.uid)
            })
        }

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url("https://onesignal.com/api/v1/notifications")
            .addHeader("Authorization", rest) // 🔐 Use your REST API Key
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OneSignal", "Notification failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("OneSignal", "Notification sent: ${response.body?.string()}")
            }
        })
    }



}








