package com.reneprojects.feature.products.mapper

import com.reneprojects.utils.extension.toPriceString
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.feature.products.model.ProductUiModel
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
    fun toProductUiModel(entity: ProductEntity): ProductUiModel
}

internal class ProductUiMapperIMPL @Inject constructor() : ProductUiMapper {
    override fun toProductUiModel(entity: ProductEntity): ProductUiModel =
        with(entity) {
            ProductUiModel(
                id = id,
                title = title,
                formattedPrice = price.toPriceString(),
                imageUrl = thumbnail
            )
        }
}