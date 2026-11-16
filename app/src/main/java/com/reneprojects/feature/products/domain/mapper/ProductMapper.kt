package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productcarousel.model.CardCarouselData.CarouselHeader
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.ItemCard
import com.reneprojects.components.productfilter.model.ItemFilterData
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType as FilterIconType
import com.reneprojects.common.constants.CategorySlug
import com.reneprojects.core.feature.categories.local.entity.CategoryEntity
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
import com.reneprojects.feature.products.model.LandingPageData.ProductFilterSectionData
import com.reneprojects.utils.extension.toPriceString
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

/** region module */
@Module
@InstallIn(SingletonComponent::class)
internal interface ProductUiMapperModule {
    @Binds
    fun bindProductUiMapper(impl: ProductMapperImpl): ProductMapper
}
/** endregion module */

/** region abstraction */
internal interface ProductMapper {
    /**
     * Groups a list of [ProductEntity] into a list of [CardCarouselData] grouped by category.
     *
     * @param products The list of products from the repository.
     * @return A list of sections for display in the UI.
     */
    fun toProductSections(
        category: String,
        products: List<ProductEntity>,
        iconType: IconType?
    ): ProductCarouselSectionData

    /**
     * Builds the category filter section with the supported categories plus the "Others" chip.
     *
     * @param categories Categories stored locally, coming from the categories endpoint.
     * @return The filter section, or `null` when no supported category is available.
     */
    fun toProductFilterSection(categories: List<CategoryEntity>): ProductFilterSectionData?
}
/** endregion abstraction */

/** region implementation */
internal class ProductMapperImpl @Inject constructor() : ProductMapper {
    private companion object Labels {
        private const val OTHERS_LABEL = "Others"
    }

    private fun toProductItemCard(entity: ProductEntity): ItemCard =
        with(entity) {
            ItemCard(
                id = id,
                title = title,
                formattedPrice = price.toPriceString(),
                imageUrl = thumbnail,
                category = category
            )
        }

    override fun toProductSections(
        category: String,
        products: List<ProductEntity>,
        iconType: IconType?
    ): ProductCarouselSectionData {
        return ProductCarouselSectionData(
            cardCarouselData = CardCarouselData(
                header = CarouselHeader(
                    iconType = iconType,
                    label = category,
                    linkId = products.firstOrNull()?.id ?: 0
                ),
                products = products.map { toProductItemCard(it) }
            )
        )
    }

    override fun toProductFilterSection(
        categories: List<CategoryEntity>
    ): ProductFilterSectionData? {
        val filters = categories.mapNotNull { category ->
            val iconType = category.slug.toFilterIconType() ?: return@mapNotNull null
            ItemFilterData(
                slug = category.slug,
                label = category.name,
                iconType = iconType
            )
        }.sortedBy { it.iconType?.ordinal ?: Int.MAX_VALUE }

        if (filters.isEmpty()) return null

        return ProductFilterSectionData(
            filters = filters + ItemFilterData(
                slug = CategorySlug.OTHERS,
                label = OTHERS_LABEL,
                iconType = null
            )
        )
    }

    private fun String.toFilterIconType(): FilterIconType? = when (this) {
        CategorySlug.FURNITURE -> FilterIconType.FURNITURE
        CategorySlug.BEAUTY -> FilterIconType.BEAUTY
        CategorySlug.FRAGRANCES -> FilterIconType.FRAGRANCES
        CategorySlug.GROCERIES -> FilterIconType.GROCERIES
        else -> null
    }
}

/** endregion implementation */
