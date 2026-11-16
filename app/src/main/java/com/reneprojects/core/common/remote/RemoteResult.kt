package com.reneprojects.core.common.remote

/**
 * Result of a remote data source call.
 *
 * Kept separate from [com.reneprojects.core.common.result.RenEcommerceResult] on purpose: this
 * type carries HTTP concerns (`304`, status code, `ETag`) that belong to the data layer only.
 */
internal sealed interface RemoteResult<out T> {
    object NotModified : RemoteResult<Nothing>
    data class Success<T>(val data: T, val eTag: String?) : RemoteResult<T>
    data class Error(val code: Int, val message: String) : RemoteResult<Nothing>
}
