package com.pstudio.blip.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager.init
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.pstudio.blip.AppStateManager
import com.pstudio.blip.data.Message
import com.pstudio.blip.data.MessageStatus
import com.pstudio.blip.utilclasses.AESUtils
import com.pstudio.blip.utilclasses.AESUtils.decryptIfNeeded
import com.pstudio.blip.utilclasses.copyFileToCustomDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

sealed class SaveState {
    object Idle: SaveState()
    object Saving: SaveState()
    object Saved: SaveState()
}

class ChatViewModel: ViewModel() {

    private val rest = "os_v2_app_6i3kqawudbhqtjbq2si7dcejkw3qlliq4rhedwu4wbujhygxpmsvjrpestw32tkamicvxh2artkitqbqvdh76zr4iypzqk6tpakpbha"

    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    private val _editingMessage = mutableStateOf<Message?>(null)
    val editingMessage: State<Message?> = _editingMessage

    private val _uploadProgressMap = mutableStateMapOf<String, Float>()
    val uploadProgressMap: Map<String, Float> get() = _uploadProgressMap

    private val _downloadProgressMap = mutableStateMapOf<String, Float>()
    val downloadProgressMap: Map<String, Float> get() = _downloadProgressMap

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _chats = mutableStateMapOf<String, MutableList<Message>>()
    val chats: SnapshotStateMap<String, MutableList<Message>> = _chats

    private val _replyingToMessage = mutableStateOf<Message?>(null)
    val replyingToMessage: State<Message?> = _replyingToMessage

    private val _isOnline = MutableStateFlow<Boolean>(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val _isUserInBackground = MutableStateFlow<Boolean>(false)
    val isUserInBackground = _isUserInBackground

    private val _activeChatUserId = MutableStateFlow<String?>(null)
    val activeChatUserId: StateFlow<String?> = _activeChatUserId

    private val _isFriendTyping = mutableStateMapOf<String, Boolean>()
    val isFriendTyping: Map<String, Boolean> = _isFriendTyping

    private val _isLoadingMoreMessages = mutableStateMapOf<String, Boolean>()
    val isLoadingMoreMessages: SnapshotStateMap<String, Boolean> = _isLoadingMoreMessages


    init {
        fetchAllChatsForCurrentUser()
        viewModelScope.launch {
            AppStateManager.isAppInForeground.collect { isForeground ->
                _isUserInBackground.value = !isForeground
            }
        }
    }

    fun setTypingStatus(friendId: String, isTyping: Boolean) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = generateChatId(currentUserId, friendId)

        dbRef.child("chats")
            .child(chatId)
            .child("typingStatus")
            .child(currentUserId)
            .setValue(isTyping)
    }

    fun observeTypingStatus(friendId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = generateChatId(currentUserId, friendId)

        dbRef.child("chats")
            .child(chatId)
            .child("typingStatus")
            .child(friendId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _isFriendTyping[friendId] = snapshot.getValue(Boolean::class.java) ?: false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TypingStatus", "Cancelled: ${error.message}")
                }
            })
    }

    fun setUploadProgress(messageId: String, progress: Float) {
        _uploadProgressMap[messageId] = progress
    }

    fun removeUploadProgress(messageId: String) {
        _uploadProgressMap.remove(messageId)
    }

    fun setDownloadProgress(messageId: String, progress: Float) {
        _downloadProgressMap[messageId] = progress
    }

    fun removeDownloadProgress(messageId: String) {
        _downloadProgressMap.remove(messageId)
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

        dbRef.child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {

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
    fun listenToUserOnlineStatus(userId: String, onResult: (success: Boolean) -> Unit) {
        val onlineRef = dbRef.child("users").child(userId).child("online")

        // Remove existing listener if already attached
        onlineStatusListener?.let { onlineRef.removeEventListener(it) }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isReceiverOnline = snapshot.getValue(Boolean::class.java) ?: false
                _isOnline.value = isReceiverOnline
                onResult(isReceiverOnline)
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

    fun setReceiverUri(senderId: String, receiverId: String, messageId: String, uri: String) {

        val chatId = generateChatId(senderId, receiverId)
        val currentUserId = auth.currentUser?.uid ?: return
        val uriId = if (currentUserId == senderId) "localUri" else "receiverLocalUri"

        dbRef.child("chats")
            .child(chatId)
            .child("messages")
            .child(messageId)
            .child(uriId)
            .setValue(uri)
            .addOnSuccessListener {
                Log.d("FirebaseUpdate", "receiverLocalUri successfully updated.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUpdate", "Failed to update receiverLocalUri", e)
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
        localUri: String = "",
        receiverLocalUri: String = "",
        isStealth: Boolean = false
    ): String? {
        val senderId = auth.currentUser?.uid ?: return null
        val replyTo = _replyingToMessage.value

        if (messageText.isBlank() && messageType == "text") return null

        val chatId = generateChatId(senderId, receiverId)
        val messageId = dbRef.push().key ?: UUID.randomUUID().toString()

        val (encryptedMessage, iv) = AESUtils.encrypt(messageText.ifBlank { " " })

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
            replyTo = replyTo,
            receiverLocalUri = receiverLocalUri,
            stealth = isStealth,
            status = MessageStatus.SENDING
        )

        val decryptedMessage = message.decryptIfNeeded()

        val existingMessages = _chats[receiverId]?.toMutableList() ?: mutableListOf()
        existingMessages.add(decryptedMessage)
        _chats[receiverId] = existingMessages

        dbRef.child("chats")
            .child(chatId)
            .child("messages")
            .child(messageId)
            .setValue(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message sent successfully")

                if (messageType == "text") {
                    updateMessageStatusIfProgressing(chatId, messageId, MessageStatus.SENT)
                }

                FirebaseDatabase.getInstance().getReference("users/$receiverId/playerId").get()
                    .addOnSuccessListener { snapshot ->
                        val playerId = snapshot.getValue(String::class.java)
                        if (playerId != null) {
                            if (!isOnline.value) {
                                sendNotificationToReceiver(playerId, messageText, senderUserName)
                            }
                        }
                    }

                _chatUiState.value = ChatUiState.Success
            }
            .addOnFailureListener {
                Log.e("ChatViewModel", "Failed to send message", it)
                updateMessageStatusIfProgressing(chatId, messageId, MessageStatus.FAILED)
                _chatUiState.value = ChatUiState.Error("Failed to send message")
            }

        cancelReplying()

        return messageId
    }

    fun saveFileToExternalStorage(
        context: Context,
        uri: Uri,
        mediaType: String,
        flag: String
    ) {
        _saveState.value = SaveState.Saving
        val result = copyFileToCustomDirectory(context, uri, mediaType, flag)

        result?.let { file ->
            val absPath = file.file.absolutePath
            if (absPath.isNotEmpty()) {
                _saveState.value = SaveState.Saved
            }
        }

    }

    fun updateMediaUrlForMessage(
        receiverId: String,
        messageId: String,
        newUrl: String,
        iv: String,
    ) {

        val senderId = auth.currentUser?.uid ?: return
        val chatId = generateChatId(senderId, receiverId)
        if (newUrl.isBlank()) updateMessageStatusIfProgressing(chatId, messageId, MessageStatus.FAILED)
        val messageRef = dbRef.child("chats").child(chatId).child("messages").child(messageId)

        messageRef.get().addOnSuccessListener { snapshot ->
            val existingMessage = snapshot.getValue(Message::class.java)

            if (existingMessage != null) {
                val updatedMessage = existingMessage.copy(message = newUrl, iv = iv)

                messageRef.setValue(updatedMessage)
                    .addOnSuccessListener {
                        Log.d("ChatViewModel", "Media URL updated and message overwritten for: $messageId")
                        updateMessageStatusIfProgressing(chatId, messageId, MessageStatus.SENT)
                    }
                    .addOnFailureListener {
                        Log.e("ChatViewModel", "Failed to overwrite message", it)
                    }
            } else {
                Log.e("ChatViewModel", "Message not found for ID: $messageId")
            }
        }.addOnFailureListener {
            Log.e("ChatViewModel", "Failed to retrieve message for update", it)
        }
    }

    private fun generateChatId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "${user1Id}_$user2Id" else "${user2Id}_$user1Id"
    }

    fun markMessagesAsSeen(friendId: String, currentUserId: String) {
        val chatId = generateChatId(friendId, currentUserId)

        // Check: online, app foreground, and current open chat
        //if (!isOnline.value) return
        if (isUserInBackground.value) return
        if (activeChatUserId.value != friendId) return

        val messages = _chats[friendId]?.toMutableList() ?: return

        for (message in messages) {
            if (
                message.senderId == friendId &&
                message.status.priority < MessageStatus.SEEN.priority
            ) {
                // Update message status in DB
                updateMessageStatusIfProgressing(chatId, message.messageId, MessageStatus.SEEN)

            }
        }
    }

    private var messageListeners = mutableMapOf<String, ChildEventListener>()
    private var statusListeners = mutableMapOf<String, ChildEventListener>()

    fun listenForMessages(friendId: String, currentUserId: String) {
        val chatId = generateChatId(friendId, currentUserId)
        val messagesRef = dbRef.child("chats").child(chatId).child("messages")

        // Prevent duplicate listeners
        if (messageListeners.containsKey(chatId)) return

        val messageListener = messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                val decryptedMessage = message?.decryptIfNeeded()
                if (decryptedMessage != null &&
                    decryptedMessage.messageType != "text" &&
                    decryptedMessage.message.isBlank() &&
                    decryptedMessage.senderId != currentUserId
                ) return

                decryptedMessage?.let {

                    if (decryptedMessage.senderId != currentUserId) {
                        updateMessageStatusIfProgressing(chatId, decryptedMessage.messageId, MessageStatus.DELIVERED)
                    }

                    val existingMessages = chats[friendId]?.toMutableList() ?: mutableListOf()
                    val alreadyExists = existingMessages.any {
                        it.timestamp == decryptedMessage.timestamp || it.messageId == decryptedMessage.messageId
                    }

                    if (!alreadyExists) {
                        existingMessages.add(decryptedMessage)
                        chats[friendId] = existingMessages
                        _chatUiState.value = ChatUiState.Success
                    }

                    markMessagesAsSeen(friendId, currentUserId)

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java)
                val decryptedMessage = updatedMessage?.decryptIfNeeded()

                if (
                    decryptedMessage != null &&
                    decryptedMessage.messageType != "text" &&
                    decryptedMessage.message.isBlank() &&
                    decryptedMessage.senderId != currentUserId
                ) return

                decryptedMessage?.let { newMsg ->

                    val existingMessages = chats[friendId]?.toMutableList() ?: return
                    val index = existingMessages.indexOfFirst { it.messageId == newMsg.messageId }

                    if (index != -1) {
                        val existingMessage = existingMessages[index]
                        if (existingMessage.messageType != "text" &&
                            decryptedMessage.message.isNotBlank()
                        ) {
                            updateMessageStatusIfProgressing(chatId, decryptedMessage.messageId, MessageStatus.DELIVERED)
                        }

                        existingMessages[index] = newMsg
                        chats[friendId] = existingMessages
                        _chatUiState.value = ChatUiState.Success
                    } else {
                        existingMessages.add(newMsg)
                        chats[friendId] = existingMessages
                        _chatUiState.value = ChatUiState.Success
                    }

                    markMessagesAsSeen(friendId, currentUserId)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val deletedMessage = snapshot.getValue(Message::class.java) ?: return
                val existingMessages = chats[friendId]?.toMutableList() ?: return
                chats[friendId] = existingMessages.filterNot { it.messageId == deletedMessage.messageId }.toMutableList()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                _chatUiState.value = ChatUiState.Error(error.message)
            }
        })

        messageListeners[chatId] = messageListener

        val statusListener = messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val messageId = snapshot.key ?: return
                val newStatus = snapshot.child("status").getValue(String::class.java)
                val status = try {
                    MessageStatus.valueOf(newStatus ?: "")
                } catch (e: Exception) {
                    return
                }
                val existingMessages = chats[friendId]?.toMutableList() ?: return
                val index = existingMessages.indexOfFirst { it.messageId == messageId }

                if (index != -1) {
                    val updatedMessage = existingMessages[index].copy(status = status)

                    existingMessages[index] = updatedMessage

                    _chats[friendId] = existingMessages
                }

            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        statusListeners[chatId] = statusListener
    }

    fun removeMessageListeners(friendId: String, currentUserId: String) {
        val chatId = generateChatId(friendId, currentUserId)

        messageListeners[chatId]?.let { listener ->
            dbRef.child("chats").child(chatId).child("messages").removeEventListener(listener)
            messageListeners.remove(chatId)
        }

    }

    fun updateMessageStatusIfProgressing(chatId: String, messageId: String, newStatus: MessageStatus) {
        val statusRef = dbRef.child("chats/$chatId/messages/$messageId/status")
        statusRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentStatusStr = currentData.getValue(String::class.java)
                val currentStatus = try {
                    MessageStatus.valueOf(currentStatusStr ?: "")
                } catch (e: Exception) {
                    null
                }

                // If no status yet or new status is ahead
                if (currentStatus == null || newStatus.priority > currentStatus.priority) {
                    currentData.value = newStatus.name
                }

                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    Log.e("StatusUpdate", "Transaction failed", error.toException())
                } else {
                    Log.d("StatusUpdate", "Status update committed: $committed")
                }
            }
        })
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








