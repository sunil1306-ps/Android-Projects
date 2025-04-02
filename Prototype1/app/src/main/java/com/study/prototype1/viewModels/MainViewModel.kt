package com.study.prototype1.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.study.prototype1.Data.Image
import com.study.prototype1.Data.DataState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(path: String): ViewModel() {

    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)

    init {
        fetchDataFromFirebase(path)
    }

    private fun fetchDataFromFirebase(path: String) {
        val tempList = mutableListOf<Image>()
        response.value = DataState.Loading
        FirebaseDatabase.getInstance().getReference("$path")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(DataSnap in snapshot.children) {
                        val imageItem  = DataSnap.getValue(Image::class.java)
                        if (imageItem != null)
                            tempList.add(imageItem)
                    }
                    response.value = DataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    response.value = DataState.Failure(error.message)
                }


            })
    }

}
