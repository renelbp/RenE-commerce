package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.reneprojects.components.loadingspinner.LoadingSpinner
import com.reneprojects.components.productcarousel.view.CarouselSection
import com.reneprojects.components.productfilter.view.ProductFilterSection
import com.reneprojects.feature.products.model.LandingPageData
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
import com.reneprojects.feature.products.model.LandingPageData.ProductFilterSectionData

private const val FILTER_SECTION_KEY = "product_filter"

@Composable
internal fun ProductsSection(
    landingPageData: List<LandingPageData>,
    modifier: Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    selectedCategory: String?,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
    onFilterSelected: (String?) -> Unit
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
            val filterSection = landingPageData
                .filterIsInstance<ProductFilterSectionData>()
                .firstOrNull()

            filterSection?.let{
                stickyHeader(key = FILTER_SECTION_KEY) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        ProductFilterSection(
                            filters = filterSection.filters,
                            selectedSlug = selectedCategory,
                            onFilterSelected = onFilterSelected
                        )
                    }
                }
            }

            itemsIndexed(
                items = landingPageData.filterIsInstance<ProductCarouselSectionData>(),
                key = { index, section -> section.composeKey(index) }
            ) { _, section ->
                CarouselSection(
                    section = section.cardCarouselData,
                    navigateToProductCategory = navigateToProductCategory,
                    navigateToProductDetails = navigateToProductDetails
                )
            }
        }
    }
}


private fun LandingPageData.composeKey(index: Int): String {
    return when (this) {
        is ProductCarouselSectionData -> "product_carousel-${cardCarouselData.header}-$index"
        is ProductFilterSectionData -> FILTER_SECTION_KEY
    }
}
