package com.reneprojects.core.feature.products.repository

import com.reneprojects.core.common.cachemanager.manager.CacheManager
import com.reneprojects.core.common.cachemanager.model.CacheStatus
import com.reneprojects.core.common.constants.CacheKeys
import com.reneprojects.core.common.constants.CachePolicy
import com.reneprojects.core.feature.products.local.dao.ProductDao
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.core.feature.products.mapper.ProductEntityMapper
import com.reneprojects.core.feature.products.mapper.ProductEntityMapperModule
import com.reneprojects.core.feature.products.remote.api.ProductsApiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

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

interface ProductRepository {
    fun observeProducts(): Flow<List<ProductEntity>>
    suspend fun loadProductData(forceRefresh: Boolean = false)
}

internal class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductsApiService,
    private val productDao: ProductDao,
    private val cacheManager: CacheManager,
    private val productEntityMapper: ProductEntityMapper

) : ProductRepository {
    override fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeProducts()

    override suspend fun loadProductData(forceRefresh: Boolean) {
        val cacheKey = CacheKeys.PRODUCTS
        val cacheStatus: CacheStatus = cacheManager.getStatus(cacheKey = cacheKey)

        if (!forceRefresh && cacheStatus.isValid) return

        try {
            val response = apiService.getProductResponse(eTag = cacheStatus.eTag)
            when {
                // Not Modified
                response.code() == 304 -> {
                    cacheManager.updateCache(
                        key = cacheKey,
                        expirationTimeMillis = CachePolicy.PRODUCTS_TTL, eTag = cacheStatus.eTag
                    )
                }

                response.isSuccessful -> {
                    val productsResponseDto = response.body() ?: throw IllegalStateException(
                        "Empty Products Response"
                    )
                    val productsEntities = productsResponseDto.products.map {
                        productEntityMapper.toProductEntity(it)
                    }
                    productDao.replaceProducts(products = productsEntities)
                    cacheManager.updateCache(
                        key = cacheKey,
                        expirationTimeMillis = CachePolicy.PRODUCTS_TTL,
                        eTag = response.headers()["ETag"]
                    )
                }

                else -> {
                    throw HttpException(response)
                }
            }
        } catch (exception: IOException) {
            if (productDao.getProducts().isEmpty()) {
                throw exception
            }
        }
    }
}