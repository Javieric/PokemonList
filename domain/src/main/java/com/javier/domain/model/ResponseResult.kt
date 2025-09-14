package com.javier.domain.model

sealed class ResponseResult<out T> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Error(val cause: ErrorCause, val throwable: Throwable? = null) : ResponseResult<Nothing>()
}

enum class ErrorCause {
    NETWORK,
    SERVER,
    NOT_FOUND,
    NO_INTERNET,
    UNKNOWN
}