package com.reneprojects.feature.products.model

import com.reneprojects.components.productcarousel.model.CardCarouselData
import com.reneprojects.components.productfilter.model.ItemFilterData

internal sealed interface LandingPageData {
    data class ProductCarouselSectionData(
        val cardCarouselData: CardCarouselData
    ) : LandingPageData

    data class ProductFilterSectionData(
        val filters: List<ItemFilterData>,
        val selectedIconType: ItemFilterData.IconType
    ) : LandingPageData

}