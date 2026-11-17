package com.reneprojects.components.productcarousel.model


internal data class CardCarouselData(
    val header: CarouselHeader,
    val products: List<ItemCard>
) {
    /**
     * @property formattedOriginalPrice Pre-discount price; `null` hides the struck-through price.
     * @property discountPercentage Rounded discount for the badge; `null` hides the badge.
     * @property formattedRating Rating with one decimal; `null` hides the rating row.
     */
    data class ItemCard(
        val id: Int,
        val title: String,
        val formattedPrice: String,
        val formattedOriginalPrice: String?,
        val discountPercentage: Int?,
        val formattedRating: String?,
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