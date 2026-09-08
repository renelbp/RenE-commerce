package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductMapper
import com.reneprojects.feature.products.model.ProductCarousel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** region module */
@Module
@InstallIn(SingletonComponent::class)
internal interface ProductsInteractorModule {
    @Binds
    @Singleton
    fun bindProductsInteractor(
        implementation: ProductsInteractorImpl
    ): ProductsInteractor
}
/** endregion module */

/** region abstraction */
internal interface ProductsInteractor {
    /**
     * Exposes a [Flow] of product carousels ready for the UI layer.
     *
     * @return A stream of product sections grouped by category.
     */
    fun observeProductSections(): Flow<List<ProductCarousel>>

    /**
     * Requests a data refresh through the repository.
     *
     * @param forceRefresh Whether to bypass local cache validation.
     * @return The result of the load operation.
     */
    suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit>
}
/** endregion abstraction */

/** region implementation */
internal class ProductsInteractorImpl @Inject constructor(
    private val repository: ProductRepository,
    private val mapper: ProductMapper
) : ProductsInteractor {

    override fun observeProductSections(): Flow<List<ProductCarousel>> {
        return repository.observeProducts().map { entities ->
            mapper.toProductSections(entities = entities)
        }
    }

    override suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit> {
        return repository.loadProductData(forceRefresh = forceRefresh)
    }
}
/** endregion implementation */
