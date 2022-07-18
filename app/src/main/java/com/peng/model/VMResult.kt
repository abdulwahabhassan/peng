package com.peng.model

sealed class VMResult<out R> {

    data class Success<out T>(val data: T) : VMResult<T>()
    data class Error(val errorMessage: String) : VMResult<Nothing>()
    data class Loading(val message: String = "") : VMResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[errorMessage=$errorMessage]"
            is Loading -> "Loading[message=$message]"

        }
    }
}