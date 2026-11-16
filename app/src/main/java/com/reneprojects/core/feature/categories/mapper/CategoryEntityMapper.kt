package com.reneprojects.core.feature.categories.mapper

import com.reneprojects.core.feature.categories.local.entity.CategoryEntity
import com.reneprojects.core.feature.categories.remote.dto.ProductCategoryDto
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** region module */
@Module
@InstallIn(SingletonComponent::class)
internal interface CategoryEntityMapperModule {
    @Binds
    @Singleton
    fun bindCategoryEntityMapper(
        implementation: CategoryEntityMapperImpl
    ): CategoryEntityMapper
}
/** endregion module */

/** region abstraction */
internal interface CategoryEntityMapper {
    /**
     * Maps a [ProductCategoryDto] to a [CategoryEntity].
     *
     * @param dto The data transfer object from the API.
     * @return A database-ready entity, or `null` when the entry has no usable slug.
     */
    fun toCategoryEntity(dto: ProductCategoryDto): CategoryEntity?
}
/** endregion abstraction */

/** region implementation */
internal class CategoryEntityMapperImpl @Inject constructor() : CategoryEntityMapper {
    override fun toCategoryEntity(dto: ProductCategoryDto): CategoryEntity? {
        val slug = dto.slug?.trim()?.lowercase(Locale.ROOT)
        if (slug.isNullOrBlank()) return null

        return CategoryEntity(
            slug = slug,
            name = dto.name?.trim()?.takeIf { it.isNotBlank() } ?: slug
        )
    }
}
/** endregion implementation */
