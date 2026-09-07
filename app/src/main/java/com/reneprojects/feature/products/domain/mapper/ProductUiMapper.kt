package com.reneprojects.feature.products.domain.mapper

import com.reneprojects.utils.extension.toPriceString
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.model.Product
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
internal interface ProductUiMapperModule {
    @Binds
    fun bindProductUiMapper(impl: ProductUiMapperIMPL): ProductUiMapper
}

internal interface ProductUiMapper {
    fun toProductUiModel(entity: ProductEntity): Product
}

internal class ProductUiMapperIMPL @Inject constructor() : ProductUiMapper {
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
}