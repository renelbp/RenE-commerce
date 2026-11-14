package com.reneprojects.components.productfilter.model

internal data class ItemFilterData(
    val label: String,
    val iconType: IconType,
) {
    enum class IconType {
        FURNITURE,
        BEAUTY,
        FRAGRANCES,
        GROCERIES
    }
}
