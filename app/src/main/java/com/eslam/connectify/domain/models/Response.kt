package com.eslam.connectify.domain.models

sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Error<out T>(
        val message: String
    ): Response<T>()
}
