package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.BEAUTY
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FRAGRANCES
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FURNITURE
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.GROCERIES
import com.reneprojects.common.constants.CategorySlug
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.categories.repository.CategoriesRepository
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.domain.mapper.ProductMapper
import com.reneprojects.feature.products.model.LandingPageData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
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
     * Exposes a [Flow] of landing sections ready for the UI layer.
     *
     * The category filter section comes first when categories are available; the rest are product
     * carousels grouped by category.
     *
     * @return A stream of landing sections.
     */
    fun observeProductSections(): Flow<List<LandingPageData>>

    /**
     * Requests a data refresh for products and categories through the repositories.
     *
     * @param forceRefresh Whether to bypass local cache validation.
     * @return The result of the products load. A categories failure is tolerated and does not fail
     * the whole refresh.
     */
    suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit>
}
/** endregion abstraction */

/** region implementation */
internal class ProductsInteractorImpl @Inject constructor(
    private val productsRepository: ProductRepository,
    private val categoriesRepository: CategoriesRepository,
    private val mapper: ProductMapper
) : ProductsInteractor {

    override fun observeProductSections(): Flow<List<LandingPageData>> {
        return combine(
            productsRepository.observeProducts(),
            categoriesRepository.observeCategories()
        ) { products, categories ->
            buildList {
                val filterSection = mapper.toProductFilterSection(categories)
                if (filterSection != null) {
                    add(filterSection)
                }

                products.groupBy { it.category }.forEach { (category, productList) ->
                    add(
                        mapper.toProductSections(
                            category = category,
                            products = productList,
                            iconType = getIconSection(category = category)
                        )
                    )
                }
            }
        }.flowOn(Dispatchers.Default)
    }

    private fun getIconSection(category: String): IconType? {
        return when (category.trim().lowercase()) {
            CategorySlug.FRAGRANCES -> FRAGRANCES
            CategorySlug.FURNITURE -> FURNITURE
            CategorySlug.GROCERIES -> GROCERIES
            CategorySlug.BEAUTY -> BEAUTY
            else -> null
        }
    }

    override suspend fun loadProductData(forceRefresh: Boolean): RenEcommerceResult<Unit> =
        coroutineScope {
            val categoriesResult = async {
                categoriesRepository.loadCategoriesData(forceRefresh = forceRefresh)
            }

            val productsResult = productsRepository.loadProductData(forceRefresh = forceRefresh)

            categoriesResult.await()
            productsResult
        }
}
/** endregion implementation */
