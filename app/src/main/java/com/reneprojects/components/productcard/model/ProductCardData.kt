package com.reneprojects.components.productcard.model

/**
 * Presentation model for a product card.
 *
 * Display-ready values only: formatting, discount calculation and brand cleanup happen in the
 * feature mapper.
 *
 * @property id Stable identity: list key and click payload.
 * @property title Already-formatted product name.
 * @property formattedPrice Final price already formatted for the current locale.
 * @property formattedOriginalPrice Pre-discount price; `null` when the product has no discount.
 * @property discountPercentage Rounded discount to show in the badge; `null` hides it.
 * @property formattedRating Rating with one decimal (for example, `4.5`).
 * @property brand Optional brand; `null` hides the brand line.
 * @property imageUrl Remote thumbnail.
 */
internal data class ProductCardData(
    val id: Int,
    val title: String,
    val formattedPrice: String,
    val formattedOriginalPrice: String?,
    val discountPercentage: Int?,
    val formattedRating: String,
    val brand: String?,
    val imageUrl: String,
)
