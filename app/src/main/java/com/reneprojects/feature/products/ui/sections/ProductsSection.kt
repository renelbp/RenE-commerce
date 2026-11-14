package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.reneprojects.components.loadingspinner.LoadingSpinner
import com.reneprojects.components.productcarousel.view.CarouselSection
import com.reneprojects.components.productfilter.model.ItemFilterData.IconType
import com.reneprojects.components.productfilter.view.ProductFilterSection
import com.reneprojects.feature.products.model.LandingPageData
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
import com.reneprojects.feature.products.model.LandingPageData.ProductFilterSectionData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsSection(
    landingPageData: List<LandingPageData>,
    modifier: Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
    onFilterSelected: (IconType) -> Unit
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            LoadingSpinner(state, isRefreshing)
        },
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(
                items = landingPageData,
                key = { index, section -> section.composeKey(index) }
            ) { _, section ->
                LandingSectionContent(
                    section = section,
                    navigateToProductCategory = navigateToProductCategory,
                    navigateToProductDetails = navigateToProductDetails,
                    onFilterSelected = onFilterSelected
                )
            }
        }
    }
}

@Composable
private fun LandingSectionContent(
    section: LandingPageData,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
    onFilterSelected: (IconType) -> Unit
) {
    when (section) {
        is ProductCarouselSectionData -> CarouselSection(
            section = section.cardCarouselData,
            navigateToProductCategory = navigateToProductCategory,
            navigateToProductDetails = navigateToProductDetails
        )

        is ProductFilterSectionData -> ProductFilterSection(
            filters = section.filters,
            selectedIconType = section.selectedIconType,
            onFilterSelected = onFilterSelected
        )
    }
}

private fun LandingPageData.composeKey(index: Int): String {
    return when (this) {
        is ProductCarouselSectionData -> "product_carousel-${cardCarouselData.header}-$index"
        is ProductFilterSectionData -> "product_filter-$selectedIconType-$index"
    }
}
