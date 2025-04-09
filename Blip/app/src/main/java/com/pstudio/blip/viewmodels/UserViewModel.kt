package com.pstudio.blip.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pstudio.blip.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class FetchState {
    object Idle : FetchState()
    object Loading : FetchState()
    object Success : FetchState()
    data class Error(val message: String) : FetchState()

}

class UserViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private val database = FirebaseDatabase.getInstance().getReference()
    private var friendsListener: ChildEventListener? = null
    private var currentListenerUserId: String? = null

    private val _friendsList = MutableStateFlow<List<User>>(emptyList())
    val friendsList: StateFlow<List<User>> = _friendsList

    private val _fetchState = MutableStateFlow<FetchState>(FetchState.Idle)
    val fetchState: StateFlow<FetchState> = _fetchState

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults

    init {
        setUserPresence()
    }

    private fun setUserPresence() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userStatusRef = dbRef.child(userId).child("online")
            val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        userStatusRef.onDisconnect().setValue(false) // when app closes/disconnects
                        userStatusRef.setValue(true) // online
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Presence", "Error: ${error.message}")
                }
            })
        }
    }


    // Add user from database by username
    fun addFriendByUsername(
        currentUserId: String,
        currentUsername: String,
        searchUsername: String,
        onResult: (Boolean, String) -> Unit
    ) {
        database.child("users").orderByChild("username").equalTo(searchUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val friendEntry = snapshot.children.first()
                        val friendId = friendEntry.key ?: return
                        val friendData = friendEntry.getValue(User::class.java)

                        if (friendId == currentUserId) {
                            onResult(false, "You can't add yourself!")
                            return
                        }

                        // Add friend to both users
                        val updates = mutableMapOf<String, Any?>()
                        updates["users/$currentUserId/friends/$friendId"] = true
                        updates["users/$friendId/friends/$currentUserId"] = true

                        database.updateChildren(updates).addOnSuccessListener {
                            createEmptyChatBetweenUsers(currentUserId, friendId)
                            onResult(true, "Friend added successfully!")
                        }.addOnFailureListener {
                            onResult(false, "Failed to add friend: ${it.message}")
                        }
                    } else {
                        onResult(false, "User not found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false, "Error: ${error.message}")
                }
            })

    }


    // Fetch friends list
    fun fetchFriendsList(userId: String) {

        _fetchState.value = FetchState.Loading

        dbRef.child(userId).child("friends").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friends = mutableListOf<User>()
                val friendUids = snapshot.children.map { it.key }

                if (friendUids.isEmpty()) {
                    _fetchState.value = FetchState.Success
                    return
                }

                var loadedCount = 0
                for (friendUid in friendUids) {
                    dbRef.child(friendUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val user = userSnapshot.getValue(User::class.java)
                            user?.let {
                                val userWithUid = it.copy(uid = userSnapshot.key ?: "")
                                friends.add(userWithUid)
                            }

                            loadedCount++
                            if (loadedCount == friendUids.size) {
                                _friendsList.value = friends
                                _fetchState.value = FetchState.Success
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _fetchState.value = FetchState.Error("Failed to load user: ${error.message}")
                        }
                    })
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserViewModel", "Error fetching friends: ${error.message}")
                _friendsList.value = emptyList()  // Prevent crashes
                _fetchState.value = FetchState.Error(error.message)
            }
        })
    }

    fun listenForFriends(currentUserId: String) {

        if (currentListenerUserId == currentUserId) return
        val friendsRef = dbRef.child(currentUserId).child("friends")

        currentListenerUserId?.let {
            dbRef.child(it).child("friends").removeEventListener(friendsListener!!)
        }

        val listener = friendsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val friendId = snapshot.key ?: return

                val exists = _friendsList.value.any { it.uid == friendId }
                if (exists) return

                dbRef.child(friendId).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val friend = snapshot.getValue(User::class.java)
                        friend?.let { it ->
                            val updatedList = _friendsList.value.toMutableList()
                            updatedList.add(it.copy(uid = friendId))
                            _friendsList.value = updatedList
                            _fetchState.value = FetchState.Success
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _fetchState.value = FetchState.Error(error.message)
                    }

                })
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val deletedFriendId = snapshot.key

                val existing = _friendsList.value.toMutableList()
                existing.removeAll { it.uid == deletedFriendId }
                _friendsList.value = existing

            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })

        friendsRef.addChildEventListener(listener)
        friendsListener = listener
        currentListenerUserId = currentUserId
    }

    fun clearFriendListener(currentUserId: String) {
        friendsListener?.let {
            dbRef.child(currentUserId).child("friends").removeEventListener(it)
            friendsListener = null
            currentListenerUserId = null
        }
        _friendsList.value = emptyList()
    }

    fun deleteFriend(currentUserId: String, friendId: String) {
        val updates = mapOf(
            "$currentUserId/friends/$friendId" to null,
            "$friendId/friends/$currentUserId" to null
        )

        dbRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("DeleteFriend", "Friend removed from both users")
            }
            .addOnFailureListener {
                Log.e("DeleteFriend", "Failed to delete friend: ${it.message}")
            }
    }

    fun searchUserByUsername(username: String) {
        dbRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()

                    if (!snapshot.exists()) {
                        // No matching user found, show empty results
                        _searchResults.value = emptyList()
                        return
                    }

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { users.add(it) }
                    }
                    _searchResults.value = users
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserViewModel", "Error searching user: ${error.message}")
                    _searchResults.value = emptyList()  // Prevent crashes
                }
            })
    }

    private fun generateChatId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "${user1Id}_$user2Id" else "${user2Id}_$user1Id"
    }

    private fun createEmptyChatBetweenUsers(user1Id: String, user2Id: String) {
        val chatId = generateChatId(user1Id, user2Id)
        val messagesRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)

        messagesRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // Add placeholder so Firebase creates the node
                messagesRef.setValue(mapOf("placeholder" to true))
            }
        }
    }

    fun clearState() {
        _friendsList.value = emptyList()
        _searchResults.value = emptyList()
    }


}