package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productcarousel.model.CardCarouselData.CarouselHeader
import com.reneprojects.components.productcarousel.model.CardCarouselData.IconType
import com.reneprojects.components.productcarousel.model.CardCarouselData.ItemCard
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
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
}
/** endregion abstraction */

/** region implementation */
internal class ProductMapperImpl @Inject constructor() : ProductMapper {
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
}

/** endregion implementation */
