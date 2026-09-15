package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.ui.components.model.CarouselHeader
import com.reneprojects.feature.products.ui.components.model.IconType
import com.reneprojects.feature.products.model.Product
import com.reneprojects.feature.products.ui.components.model.ProductCarousel
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
     * Maps a database [ProductEntity] to a UI-friendly [Product] model.
     *
     * @param entity The source database entity.
     * @return A mapped UI model.
     */
    fun toProductUiModel(entity: ProductEntity): Product

    /**
     * Groups a list of [ProductEntity] into a list of [ProductCarousel] grouped by category.
     *
     * @param products The list of products from the repository.
     * @return A list of sections for display in the UI.
     */
    fun toProductSections(category: String, products: List<ProductEntity>, iconType: IconType?): ProductCarousel
}
/** endregion abstraction */

/** region implementation */
internal class ProductMapperImpl @Inject constructor() : ProductMapper {
    override fun toProductUiModel(entity: ProductEntity): Product =
        with(entity) {
            Product(
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
    ): ProductCarousel {
        return ProductCarousel(
            header = CarouselHeader(
                iconType = iconType,
                label = category,
                linkId = products.firstOrNull()?.id ?: 0
            ),
            products = products.map { toProductUiModel(it) }
        )
    }
}

/** endregion implementation */
