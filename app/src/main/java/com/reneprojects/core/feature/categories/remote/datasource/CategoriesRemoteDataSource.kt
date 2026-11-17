package com.reneprojects.core.feature.categories.remote.datasource

import com.reneprojects.core.common.remote.RemoteResult
import com.reneprojects.core.common.remote.safeRemoteCall
import com.reneprojects.core.feature.categories.remote.dto.ProductCategoryDto
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
internal interface CategoriesRemoteDataSourceModule {
    @Binds
    @Singleton
    fun bindCategoriesRemoteDataSource(
        implementation: CategoriesRemoteDataSourceImpl
    ): CategoriesRemoteDataSource
}
/** endregion module */

/** region abstraction */
internal interface CategoriesRemoteDataSource {
    /**
     * Fetches product categories using an optional ETag for cache validation.
     *
     * @param eTag The ETag of the last successful fetch.
     * @return A [RemoteResult] wrapping the API response.
     */
    suspend fun fetchCategories(eTag: String?): RemoteResult<List<ProductCategoryDto>>
}
/** endregion abstraction */

/** region implementation */
internal class CategoriesRemoteDataSourceImpl @Inject constructor(
    private val apiService: RenEcommerceApiService
) : CategoriesRemoteDataSource {

    override suspend fun fetchCategories(eTag: String?): RemoteResult<List<ProductCategoryDto>> {
        return safeRemoteCall(
            call = { apiService.getProductCategoriesResponse(eTag = eTag) },
            extract = { it }
        )
    }
}
/** endregion implementation */
