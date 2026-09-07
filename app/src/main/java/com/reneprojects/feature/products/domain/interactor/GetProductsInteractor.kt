package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductUiMapper
import com.reneprojects.feature.products.model.Product
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ProductsInteractorModule {
    @Binds
    @Singleton
    fun bindProductsInteractor(
        implementation: ProductsInteractorImpl
    ): ProductsInteractor
}

internal interface ProductsInteractor {
    fun observeProducts(): Flow<List<Product>>

    suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit>
}

internal class ProductsInteractorImpl @Inject constructor(
    private val repository: ProductRepository,
    private val mapper: ProductUiMapper
) :
    ProductsInteractor {
    override fun observeProducts(): Flow<List<Product>> {
        return repository.observeProducts().map {
            it.map { entity ->
                mapper.toProductUiModel(entity = entity)
            }
        }
    }

    override suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit> {
        return repository.loadProductData(forceRefresh = forceRefresh)
    }

}