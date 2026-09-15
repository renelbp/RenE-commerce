package com.reneprojects.feature.products.ui.components.model

import com.reneprojects.feature.products.model.Product

internal data class ProductCarousel(
    val header: CarouselHeader,
    val products: List<Product>
)

internal data class CarouselHeader(
    val iconType: IconType?,
    val label: String,
    val linkId: Int
)

internal enum class IconType {
    FURNITURE,
    BEAUTY,
    FRAGRANCES,
    GROCERIES
}