package com.reneprojects.core.common.remote

import kotlinx.coroutines.CancellationException
import retrofit2.Response

private const val ETAG_HEADER = "ETag"
private const val TECHNICAL_ERROR_CODE = -1
private const val EMPTY_BODY_MESSAGE = "Empty response body"
private const val TECHNICAL_ERROR_MESSAGE = "Network or technical failure"

/**
 * Executes a Retrofit [call] and maps its HTTP concerns to a [RemoteResult].
 *
 * Centralizes `304`, `ETag` extraction, empty bodies, HTTP errors and thrown exceptions so each
 * feature data source only declares its endpoint and how to extract the body.
 *
 * @param T Raw response body type returned by the API.
 * @param R Data exposed to the repository.
 * @param call Retrofit call to execute.
 * @param extract Converts the response body into the exposed data.
 * @return The mapped [RemoteResult], or [RemoteResult.NotModified] for `304` responses.
 */
internal suspend fun <T : Any, R> safeRemoteCall(
    call: suspend () -> Response<T>,
    extract: (T) -> R,
): RemoteResult<R> {
    return try {
        val response = call()

        when {
            response.code() == 304 -> RemoteResult.NotModified

            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    RemoteResult.Success(
                        data = extract(body),
                        eTag = response.headers()[ETAG_HEADER]
                    )
                } else {
                    RemoteResult.Error(
                        code = response.code(),
                        message = EMPTY_BODY_MESSAGE
                    )
                }
            }

            else -> RemoteResult.Error(
                code = response.code(),
                message = response.message()
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RemoteResult.Error(
            code = TECHNICAL_ERROR_CODE,
            message = TECHNICAL_ERROR_MESSAGE
        )
    }
}
