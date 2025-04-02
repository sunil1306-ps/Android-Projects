package com.example.googlelogin.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.googlelogin.datastate.DataState

class MainViewModel: ViewModel() {

    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)


}