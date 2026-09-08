package com.reneprojects.core.feature.products.repository

import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.common.cachemanager.manager.CacheManager
import com.reneprojects.core.common.cachemanager.model.CacheStatus
import com.reneprojects.core.common.constants.CacheKeys
import com.reneprojects.core.common.constants.CachePolicy
import com.reneprojects.core.feature.products.local.dao.ProductDao
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.core.feature.products.mapper.ProductEntityMapper
import com.reneprojects.core.feature.products.mapper.ProductEntityMapperModule
import com.reneprojects.core.feature.products.remote.datasource.ProductsRemoteDataSource
import com.reneprojects.core.feature.products.remote.datasource.ProductsRemoteResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** region module */
@Module(
    includes = [
        ProductEntityMapperModule::class
    ]
)
@InstallIn(SingletonComponent::class)
internal interface ProductRepositoryModule {
    @Binds
    @Singleton
    fun bindProductRepository(
        implementation: ProductRepositoryImpl
    ): ProductRepository
}
/** endregion module */

/** region abstraction */
interface ProductRepository {
    fun observeProducts(): Flow<List<ProductEntity>>
    suspend fun loadProductData(forceRefresh: Boolean = false): RenEcommerceResult<Unit>
}
/** endregion abstraction */

/** region implementation */
internal class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductsRemoteDataSource,
    private val productDao: ProductDao,
    private val cacheManager: CacheManager,
    private val productEntityMapper: ProductEntityMapper

) : ProductRepository {
    override fun observeProducts(): Flow<List<ProductEntity>> {
        return productDao.observeProducts()
    }

    override suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit> {
        val cacheKey = CacheKeys.PRODUCTS
        val cacheStatus = cacheManager.getStatus(cacheKey)

        if (!forceRefresh && cacheStatus.isValid) {
            return RenEcommerceResult.Success(Unit)
        }

        return updateProducts(
            cacheKey = cacheKey,
            cacheStatus = cacheStatus
        )
    }

    /**
     * Synchronizes local products with the remote response using `ETag`\-based cache control.
     *
     * Behavior:
     * \- `NotModified`: refreshes cache validity without changing local data.
     * \- `Success`: maps and replaces local products, then updates cache metadata.
     * \- `Error`: returns a failure with the remote code and message.
     *
     * @param cacheKey Cache key for products.
     * @param cacheStatus Current cache status, including validity and `ETag`.
     * @return `RenEcommerceResult.Success(Unit)` when synchronization or cache renewal succeeds;
     * `RenEcommerceResult.Error` when the remote request fails.
     */
    private suspend fun updateProducts(
        cacheKey: String,
        cacheStatus: CacheStatus
    ): RenEcommerceResult<Unit> {
        return when (val remoteResult = remoteDataSource.fetchProducts(eTag = cacheStatus.eTag)) {
            is ProductsRemoteResult.NotModified -> {
                cacheManager.updateCache(
                    key = cacheKey,
                    expirationTimeMillis = CachePolicy.PRODUCTS_TTL,
                    eTag = cacheStatus.eTag
                )
                RenEcommerceResult.Success(Unit)
            }

            is ProductsRemoteResult.Success -> {
                val productEntities = remoteResult.products.map {
                    productEntityMapper.toProductEntity(it)
                }

                productDao.replaceProducts(
                    products = productEntities
                )

                cacheManager.updateCache(
                    key = cacheKey,
                    expirationTimeMillis = CachePolicy.PRODUCTS_TTL,
                    eTag = remoteResult.eTag
                )
                RenEcommerceResult.Success(Unit)
            }

            is ProductsRemoteResult.Error -> {
                RenEcommerceResult.Error(
                    exception = Exception("Error ${remoteResult.code}: ${remoteResult.message}")
                )
            }
        }
    }
}
/** endregion implementation */
