package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.utils.extension.toPriceString
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.model.Product
import com.reneprojects.feature.products.model.ProductCarousel
import com.reneprojects.feature.products.model.CarouselHeader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface ProductUiMapperModule {
    @Binds
    fun bindProductUiMapper(impl: ProductMapperImpl): ProductMapper
}

internal interface ProductMapper {
    fun toProductUiModel(entity: ProductEntity): Product
    fun toProductSections(entities: List<ProductEntity>): List<ProductCarousel>
}

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

    override fun toProductSections(entities: List<ProductEntity>): List<ProductCarousel> {
        return entities.groupBy { it.category }
            .map { (category, products) ->
                ProductCarousel(
                    header = CarouselHeader(
                        label = category,
                        linkId = products.firstOrNull()?.id ?: 0
                    ),
                    products = products.map { toProductUiModel(it) }
                )
            }
    }
}