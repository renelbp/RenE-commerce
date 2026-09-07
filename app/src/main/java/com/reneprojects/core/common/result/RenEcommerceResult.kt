package com.reneprojects.core.common.result

sealed interface RenEcommerceResult<out T> {

    data class Success<T>(
        val data: T
    ) : RenEcommerceResult<T>

    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error"
    ) : RenEcommerceResult<Nothing>
}