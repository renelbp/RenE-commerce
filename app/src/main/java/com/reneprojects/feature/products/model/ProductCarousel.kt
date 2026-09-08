package com.reneprojects.feature.products.model

internal data class ProductCarousel(
    val header: CarouselHeader,
    val products: List<Product>
)

internal data class CarouselHeader(
    val label: String,
    val linkId: Int
)