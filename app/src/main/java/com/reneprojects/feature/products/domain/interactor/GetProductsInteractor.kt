package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductMapper
import com.reneprojects.feature.products.ui.components.model.IconType
import com.reneprojects.feature.products.ui.components.model.ProductCarousel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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
    private companion object CategoryConstants {
        private const val FURNITURE_VALUE = "furniture"
        private const val BEAUTY_VALUE = "beauty"
        private const val FRAGRANCES_VALUE = "fragrances"
        private const val GROCERIES_VALUE = "groceries"
    }

    override fun observeProductSections(): Flow<List<ProductCarousel>> {
        return repository.observeProducts().map { entities ->
            entities.groupBy { it.category }.map { (category, products) ->
                val iconType: IconType? = getIconSection(category = category)
                mapper.toProductSections(
                    category = category, products = products,
                    iconType = iconType
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    private fun getIconSection(category: String): IconType? {
        return when (category.trim().lowercase()) {
            FRAGRANCES_VALUE -> IconType.FRAGRANCES
            FURNITURE_VALUE -> IconType.FURNITURE
            GROCERIES_VALUE -> IconType.GROCERIES
            BEAUTY_VALUE -> IconType.BEAUTY
            else -> null
        }
    }

    override suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit> {
        return repository.loadProductData(forceRefresh = forceRefresh)
    }
}
/** endregion implementation */
