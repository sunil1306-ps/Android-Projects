package com.pstudio.blip

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppStateManager : LifecycleObserver {

    private var userId: String? = null
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    private val _isAppInForeground = MutableStateFlow(true)
    val isAppInForeground: StateFlow<Boolean> = _isAppInForeground

    fun register(application: Application, uid: String) {
        userId = uid
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        setupOnDisconnect()
    }

    private fun setupOnDisconnect() {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        val onlineRef = dbRef.child(userId ?: return).child("online")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    onlineRef.onDisconnect().setValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        _isAppInForeground.value = true
        userId?.let { dbRef.child(it).child("online").setValue(true) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        _isAppInForeground.value = false
        userId?.let { dbRef.child(it).child("online").setValue(false) }
    }
}



