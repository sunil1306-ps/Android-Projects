package com.mini.rdatabase


sealed class DataState {
    class Success(val data: MutableList<Message>): DataState()
    class Failure(val message: String): DataState()
    object Loading: DataState()
    object Empty: DataState()
}