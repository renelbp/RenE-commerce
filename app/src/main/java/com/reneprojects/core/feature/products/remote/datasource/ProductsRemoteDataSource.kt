package com.reneprojects.core.feature.products.remote.datasource

import com.reneprojects.core.common.remote.RemoteResult
import com.reneprojects.core.common.remote.safeRemoteCall
import com.reneprojects.core.feature.products.remote.dto.ProductDto
import com.reneprojects.core.network.api.RenEcommerceApiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

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
     * @return A [RemoteResult] wrapping the API response.
     */
    suspend fun fetchProducts(eTag: String?): RemoteResult<List<ProductDto>>
}
/** endregion abstraction */

/** region implementation */
internal class ProductsRemoteDataSourceImpl @Inject constructor(
    private val apiService: RenEcommerceApiService
) : ProductsRemoteDataSource {

    override suspend fun fetchProducts(eTag: String?): RemoteResult<List<ProductDto>> {
        return safeRemoteCall(
            call = { apiService.getProductsResponse(eTag = eTag) },
            extract = { it.products }
        )
    }
}
/** endregion implementation */
