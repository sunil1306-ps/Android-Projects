package com.pstudio.blipadmin.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pstudio.blipadmin.data.FlaggedMessage

class AdminViewModel : ViewModel() {

    private val _flaggedMessages = mutableStateListOf<FlaggedMessage>()
    val flaggedMessages: List<FlaggedMessage> = _flaggedMessages

    private val dbRef = FirebaseDatabase.getInstance().getReference("admin")

    init {
        fetchFlaggedMessages()
    }

    fun fetchFlaggedMessages() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _flaggedMessages.clear()
                for (messageSnap in snapshot.children) {
                    val message = messageSnap.getValue(FlaggedMessage::class.java)
                    message?.let { _flaggedMessages.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminViewModel", "Failed to fetch flagged messages: ${error.message}")
            }
        })
    }

    fun removeMessage(messageId: String) {
        dbRef.child(messageId).removeValue()
        _flaggedMessages.removeAll { it.messageId == messageId }
    }

    fun acceptMessage(message: FlaggedMessage) {
        // Placeholder for approve action (e.g., move to safe content)
        removeMessage(message.messageId)
    }

    fun declineMessage(message: FlaggedMessage) {
        // Placeholder for decline action (e.g., notify user, archive, etc.)
        removeMessage(message.messageId)
    }
}
