package com.reneprojects.core.feature.products.remote.datasource

import com.reneprojects.core.feature.products.remote.api.ProductsApiService
import com.reneprojects.core.feature.products.remote.dto.ProductDto
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal sealed interface ProductsRemoteResult {
    object NotModified : ProductsRemoteResult
    data class Success(val products: List<ProductDto>, val eTag: String?) : ProductsRemoteResult
    data class Error(val code: Int, val message: String) : ProductsRemoteResult
}

/** region module */
@Module
@InstallIn(SingletonComponent::class)
internal interface ProductsRemoteDataSourceModule {
    @Binds
    @Singleton
    fun bindProductsRemoteDataSource(
        implementation: ProductsRemoteDataSourceImpl
    ): ProductsRemoteDataSource
}
/** endregion module */

/** region abstraction */
internal interface ProductsRemoteDataSource {
    /**
     * Fetches products from the remote API using an optional ETag for caching.
     *
     * @param eTag The ETag of the last successful fetch.
     * @return A [ProductsRemoteResult] wrapping the API response.
     */
    suspend fun fetchProducts(eTag: String?): ProductsRemoteResult
}
/** endregion abstraction */

/** region implementation */
internal class ProductsRemoteDataSourceImpl @Inject constructor(
    private val apiService: ProductsApiService
) : ProductsRemoteDataSource {

    override suspend fun fetchProducts(eTag: String?): ProductsRemoteResult {
        return try {
            val response = apiService.getProductResponse(eTag = eTag)

            when {
                response.code() == 304 -> {
                    ProductsRemoteResult.NotModified
                }

                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        ProductsRemoteResult.Success(
                            products = body.products,
                            eTag = response.headers()["ETag"]
                        )
                    } else {
                        ProductsRemoteResult.Error(
                            code = response.code(),
                            message = "Empty response body"
                        )
                    }
                }

                else -> {
                    ProductsRemoteResult.Error(
                        code = response.code(),
                        message = response.message()
                    )
                }
            }
        } catch (e: Exception) {
            ProductsRemoteResult.Error(
                code = -1,
                message = e.message ?: "Unknown error"
            )
        }
    }
}
/** endregion implementation */
