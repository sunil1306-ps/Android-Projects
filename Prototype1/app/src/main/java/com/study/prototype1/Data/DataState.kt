package com.study.prototype1.Data

sealed class DataState {
    class Success(val data: MutableList<Image>): DataState()
    class Failure(val message: String): DataState()
    object Loading: DataState()
    object Empty: DataState()
}