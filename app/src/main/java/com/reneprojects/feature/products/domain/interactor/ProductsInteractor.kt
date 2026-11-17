package com.reneprojects.feature.products.domain.interactor

import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.BEAUTY
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FRAGRANCES
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.FURNITURE
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType.GROCERIES
import com.reneprojects.common.constants.CategorySlug
import com.reneprojects.components.productcard.model.ProductCardData
import com.reneprojects.core.common.result.RenEcommerceResult
import com.reneprojects.core.feature.categories.repository.CategoriesRepository
import com.reneprojects.core.feature.products.local.entity.ProductEntity
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
import java.util.Locale
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
     * Exposes the flat product list for the selected category chip.
     *
     * Filtering happens in memory over the locally stored products; no extra API call is made.
     * [CategorySlug.OTHERS] returns every product outside the supported categories.
     *
     * @param selectedCategory Stream of the selected category slug, or `null` when no chip is
     * active.
     * @return A stream of display-ready cards ordered by product id; empty when there is no
     * selection.
     */
    fun observeProductListing(
        selectedCategory: Flow<String?>
    ): Flow<List<ProductCardData>>

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

    override fun observeProductListing(
        selectedCategory: Flow<String?>
    ): Flow<List<ProductCardData>> {
        return combine(
            productsRepository.observeProducts(),
            selectedCategory
        ) { products, category ->
            if (category == null) {
                emptyList()
            } else {
                mapper.toProductCards(
                    products.filterByCategory(category = category)
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    private fun List<ProductEntity>.filterByCategory(
        category: String
    ): List<ProductEntity> {
        val selectedCategory = category.trim().lowercase(Locale.ROOT)

        return filter { product ->
            val productCategory = product.category.trim().lowercase(Locale.ROOT)
            if (selectedCategory == CategorySlug.OTHERS) {
                productCategory !in SUPPORTED_CATEGORIES
            } else {
                productCategory == selectedCategory
            }
        }.sortedBy { it.id }
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

    private companion object Categories {
        val SUPPORTED_CATEGORIES = setOf(
            CategorySlug.FURNITURE,
            CategorySlug.BEAUTY,
            CategorySlug.FRAGRANCES,
            CategorySlug.GROCERIES,
        )
    }
}
/** endregion implementation */
