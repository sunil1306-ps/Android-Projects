package com.mini.fluenttalk.viewModels


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.mini.fluenttalk.data.Message
import com.mini.fluenttalk.data.Users


class mainViewModel(): ViewModel() {

    init {
        try {
            getContacts()
        } catch (e: Exception) {
            Log.e("mvff", "failed to fetch data or contacts")
        }
    }

    var tempList: MutableList<Message> = mutableStateListOf()
    var userList: MutableList<Users> = mutableStateListOf()
    var conversations: MutableList<TextMessage> = mutableStateListOf()
    var suggestionResult: MutableList<String> = mutableStateListOf()

    var selectedContact by mutableStateOf("")
    var inputMessage by mutableStateOf("")

    private val smartReply = SmartReply.getClient()

    private val user = Firebase.auth.currentUser




    fun smartReplyGenerator() {
        smartReply.suggestReplies(conversations)
            .addOnSuccessListener {result ->
                suggestionResult.clear()
                if (result.status == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    suggestionResult.add("No suggestions available")
                } else if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    for (suggestion in result.suggestions) {
                        suggestionResult.add(suggestion.text)
                    }
                } else {
                    suggestionResult.clear()
                }

            }

    }

    fun setData(message: Message, size: Int) {

        val dbInst = FirebaseDatabase.getInstance()

        val collectionMap = hashMapOf<String, Any>(
            "text" to message.text!!,
            "isSenderMe" to true
        )
        val collectionMap1 = hashMapOf<String, Any>(
            "text" to message.text!!,
            "isSenderMe" to false
        )

        dbInst.getReference("users/").child(selectedContact).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val count = size
                    dbInst.getReference("users/${user?.displayName}/convs/${selectedContact}/chat/msgs/").push().setValue(collectionMap)
                    dbInst.getReference("users/${selectedContact}/convs/${user?.displayName}/chat/msgs/").push().setValue(collectionMap1)

                } else {
                    Log.e("nomsg", "no user exist")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun getData() {
        FirebaseDatabase.getInstance().getReference("users/${user?.displayName}/convs/$selectedContact/chat/msgs/")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    tempList.clear()
                    for (datasnap in snapshot.children) {
                        val messageItem = datasnap.getValue(Message::class.java)

                        if (messageItem != null) {
                            messageItem.isSenderMe = datasnap.child("isSenderMe").getValue(Boolean::class.java)
                        }
                        Log.e("vvv", "$messageItem")
                        tempList.add(messageItem!!)
                    }
                    if (tempList.isNotEmpty()) {
                        conversations.add(TextMessage.createForRemoteUser(tempList[tempList.lastIndex].text!!, System.currentTimeMillis(), selectedContact))
                    }
                    if (tempList.isNotEmpty()) {
                        smartReplyGenerator()
                    }
                    Log.e("mmnnmm", suggestionResult.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    fun getContacts() {
        FirebaseDatabase.getInstance().getReference("users/${user?.displayName}/convs/")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (datasnap in snapshot.children) {
                        val contact = datasnap.getValue(Users::class.java)

                        if (contact != null) {
                            contact.name = datasnap.child("name").getValue(String::class.java)
                            contact.email = datasnap.child("email").getValue(String::class.java)
                            userList.add(contact)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


}