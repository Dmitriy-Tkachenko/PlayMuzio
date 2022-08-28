package com.tk4dmitriy.playmuzio.utils

sealed class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(status = Status.SUCCESS, data = data)
    class Error<T>(message: String): Resource<T>(status = Status.ERROR, message = message)
    class Loading<T>: Resource<T>(status = Status.LOADING)
}


