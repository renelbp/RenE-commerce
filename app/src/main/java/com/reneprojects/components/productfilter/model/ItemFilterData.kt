package com.reneprojects.components.productfilter.model

/**
 * Chip shown in the category filter row.
 *
 * @property slug Category identity coming from the API; also the stable key used by Compose.
 * @property label Text displayed in the chip.
 * @property iconType Optional icon. `null` renders a text-only chip (for example, "Others").
 */
internal data class ItemFilterData(
    val slug: String,
    val label: String,
    val iconType: IconType?,
) {
    enum class IconType {
        FURNITURE,
        BEAUTY,
        FRAGRANCES,
        GROCERIES
    }
}
