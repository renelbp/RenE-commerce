package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productcarousel.model.CardCarouselData.CarouselHeader
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.ItemCard
import com.reneprojects.components.productfilter.model.ItemFilterData
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType as FilterIconType
import com.reneprojects.components.productcard.model.ProductCardData
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
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

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

    /**
     * Maps products to display-ready cards for the filtered product list.
     *
     * @param products Products to render, already filtered and ordered.
     * @return One [ProductCardData] per product, preserving the input order.
     */
    fun toProductCards(products: List<ProductEntity>): List<ProductCardData>
}
/** endregion abstraction */

/** region implementation */
internal class ProductMapperImpl @Inject constructor() : ProductMapper {
    private companion object Constants {
        private const val OTHERS_LABEL = "Others"
        private const val PERCENT_DIVISOR = 100.0
    }

    private fun toProductItemCard(entity: ProductEntity): ItemCard =
        with(entity) {
            val prices = toDisplayPrices()
            ItemCard(
                id = id,
                title = title,
                formattedPrice = prices.formattedPrice,
                formattedOriginalPrice = prices.formattedOriginalPrice,
                discountPercentage = prices.discountPercentage,
                formattedRating = rating.takeIf { it > 0 }?.toRatingString(),
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

    override fun toProductCards(products: List<ProductEntity>): List<ProductCardData> {
        return products.map { entity ->
            with(entity) {
                val prices = toDisplayPrices()

                ProductCardData(
                    id = id,
                    title = title,
                    formattedPrice = prices.formattedPrice,
                    formattedOriginalPrice = prices.formattedOriginalPrice,
                    discountPercentage = prices.discountPercentage,
                    formattedRating = rating.toRatingString(),
                    brand = brand?.trim()?.takeIf { it.isNotBlank() },
                    imageUrl = thumbnail,
                )
            }
        }
    }

    /**
     * Final price plus the original one when the product has a real discount.
     *
     * `price` is the pre-discount price; the badge uses the rounded discount and hides at 0.
     */
    private fun ProductEntity.toDisplayPrices(): DisplayPrices {
        val roundedDiscount = discountPercentage.roundToInt()
        val hasDiscount = roundedDiscount > 0
        val originalPrice = price.toPriceString()

        return DisplayPrices(
            formattedPrice = if (hasDiscount) {
                (price * (1 - discountPercentage / PERCENT_DIVISOR)).toPriceString()
            } else {
                originalPrice
            },
            formattedOriginalPrice = originalPrice.takeIf { hasDiscount },
            discountPercentage = roundedDiscount.takeIf { hasDiscount },
        )
    }

    private fun Double.toRatingString(): String = String.format(Locale.ROOT, "%.1f", this)

    private data class DisplayPrices(
        val formattedPrice: String,
        val formattedOriginalPrice: String?,
        val discountPercentage: Int?,
    )

    private fun String.toFilterIconType(): FilterIconType? = when (this) {
        CategorySlug.FURNITURE -> FilterIconType.FURNITURE
        CategorySlug.BEAUTY -> FilterIconType.BEAUTY
        CategorySlug.FRAGRANCES -> FilterIconType.FRAGRANCES
        CategorySlug.GROCERIES -> FilterIconType.GROCERIES
        else -> null
    }
}

/** endregion implementation */
