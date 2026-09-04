package com.reneprojects.core.feature.products.mapper

import com.reneprojects.core.feature.products.local.entity.ProductEntity
import com.reneprojects.core.feature.products.remote.dto.ProductDto
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ProductEntityMapperModule {
    @Binds
    @Singleton
    fun bindProductEntityMapper(
        implementation: ProductEntityMapperImpl
    ): ProductEntityMapper
}

internal interface ProductEntityMapper {
    fun toProductEntity(dto: ProductDto): ProductEntity
}

internal class ProductEntityMapperImpl @Inject constructor() : ProductEntityMapper {
    override fun toProductEntity(dto: ProductDto): ProductEntity {
        return ProductEntity(
            id = dto.id ?: 0,
            title = dto.title.orEmpty(),
            description = dto.description.orEmpty(),
            category = dto.category.orEmpty(),
            price = dto.price ?: 0.0,
            discountPercentage = dto.discountPercentage ?: 0.0,
            rating = dto.rating ?: 0.0,
            stock = dto.stock ?: 0,
            brand = dto.brand,
            thumbnail = dto.thumbnail.orEmpty()
        )
    }

}