package com.reneprojects.components.productcarousel.model


internal data class CardCarouselData(
    val header: CarouselHeader,
    val products: List<ItemCard>
) {
    data class ItemCard(
        val id: Int,
        val title: String,
        val formattedPrice: String,
        val imageUrl: String,
        val category: String,
    )

    data class CarouselHeader(
        val iconType: IconType?,
        val label: String,
        val linkId: Int
    )

    enum class IconType {
        FURNITURE,
        BEAUTY,
        FRAGRANCES,
        GROCERIES
    }
}