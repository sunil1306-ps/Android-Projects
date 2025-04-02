package com.mini.rdatabase

import android.provider.MediaStore.Images
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(/*firebaseDatabase: FirebaseDatabase?*/): ViewModel() {

    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)

    var id = 0

    var inputMessage = mutableStateOf("")

    var messages: MutableList<Message> = mutableStateListOf(

//    Message(message = "Hello", isSenderMe = true) ,
//    Message(message = "Hai", isSenderMe = false),
//    Message(message = "How are you", isSenderMe = true),
//    Message(message = "Good, How about you.", isSenderMe = false),
//    Message(message = "Im great", isSenderMe = true),
//    Message(message = "What's plan tomo?", isSenderMe = true),
//    Message(message = "Nothing much.", isSenderMe = false),
//    Message(message = "Wanna hang out?", isSenderMe = true)

    )

    fun setData(msg: String, isSenderMe: Boolean){
        FirebaseDatabase.getInstance().getReference("data").child("messages")
            .child("$id").child("isSenderMe").setValue(isSenderMe)
        FirebaseDatabase.getInstance().getReference("data").child("messages")
            .child("$id").child("message").setValue("$msg")
    }

//    private fun randoml(): Char {
//        var letter: Char
//        val letters: List<Char> = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i')
//
//        letter = letters.random()
//        return letter
//    }

    init {
        getData()
    }

    fun getData() {

        response.value =DataState.Loading
        val tempList = mutableListOf<Message>()

        FirebaseDatabase.getInstance().getReference("data").child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnap in snapshot.children) {

                        val messageItem = dataSnap.getValue(Message::class.java)
                        messageItem?.message = dataSnap.child("message").getValue(String::class.java)
                        messageItem?.isSenderMe = dataSnap.child("isSenderMe").getValue(true) as Boolean?
                        if (messageItem != null) {
                            tempList.add(messageItem)
                        }
                    }
                    Log.e("ooo", "onDataChange: $tempList")
                    response.value = DataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ooo", "onCancelled: ${error.toException()}")
                    response.value = DataState.Failure(error.message)
                }

            })
    }

}

val viewModel = MainViewModel()