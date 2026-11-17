package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reneprojects.components.loadingspinner.LoadingSpinner
import com.reneprojects.components.productcard.model.ProductCardData
import com.reneprojects.components.productcard.view.ProductCard
import com.reneprojects.components.productcarousel.view.CarouselSection
import com.reneprojects.components.productfilter.view.ProductFilterSection
import com.reneprojects.feature.products.model.LandingPageData
import com.reneprojects.feature.products.model.LandingPageData.ProductCarouselSectionData
import com.reneprojects.feature.products.model.LandingPageData.ProductFilterSectionData

private const val FILTER_SECTION_KEY = "product_filter"
private const val GRID_COLUMNS = 2
private const val SCREEN_HORIZONTAL_PADDING = 16
private const val CARD_SPACING = 8

@Composable
internal fun ProductsSection(
    landingPageData: List<LandingPageData>,
    filteredProducts: List<ProductCardData>,
    modifier: Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    selectedCategory: String?,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
    onFilterSelected: (String?) -> Unit
) {
    val state = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    val filterSection = landingPageData
        .filterIsInstance<ProductFilterSectionData>()
        .firstOrNull()

    // Without the chip row there is no way to clear the filter, so keep the carousels visible.
    val visibleFilterSection = filterSection.takeIf { selectedCategory != null }

    LaunchedEffect(selectedCategory) {
        if (visibleFilterSection != null) {
            gridState.scrollToItem(0)
        } else {
            listState.scrollToItem(0)
        }
    }

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
        if (visibleFilterSection != null) {
            FilteredProductsGrid(
                products = filteredProducts,
                filterSection = visibleFilterSection,
                selectedCategory = selectedCategory,
                onFilterSelected = onFilterSelected,
                gridState = gridState,
                navigateToProductDetails = navigateToProductDetails,
            )
        } else {
            LandingSectionsList(
                landingPageData = landingPageData,
                filterSection = filterSection,
                selectedCategory = selectedCategory,
                onFilterSelected = onFilterSelected,
                listState = listState,
                navigateToProductCategory = navigateToProductCategory,
                navigateToProductDetails = navigateToProductDetails,
            )
        }
    }
}

@Composable
private fun FilteredProductsGrid(
    products: List<ProductCardData>,
    filterSection: ProductFilterSectionData,
    selectedCategory: String?,
    onFilterSelected: (String?) -> Unit,
    gridState: LazyGridState,
    navigateToProductDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier.fillMaxWidth(),
        state = gridState,
        contentPadding = PaddingValues(
            start = SCREEN_HORIZONTAL_PADDING.dp,
            end = SCREEN_HORIZONTAL_PADDING.dp,
            bottom = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
        verticalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
    ) {
        stickyHeader(key = FILTER_SECTION_KEY) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                ProductFilterSection(
                    filters = filterSection.filters,
                    selectedSlug = selectedCategory,
                    onFilterSelected = onFilterSelected,
                    // The grid already applies the screen margins to the full-span header.
                    contentPadding = PaddingValues(0.dp),
                )
            }
        }

        items(
            items = products,
            key = { it.id },
        ) { product ->
            ProductCard(
                data = product,
                onClick = navigateToProductDetails,
            )
        }
    }
}

@Composable
private fun LandingSectionsList(
    landingPageData: List<LandingPageData>,
    filterSection: ProductFilterSectionData?,
    selectedCategory: String?,
    onFilterSelected: (String?) -> Unit,
    listState: LazyListState,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
) {
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        filterSection?.let {
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


private fun LandingPageData.composeKey(index: Int): String {
    return when (this) {
        is ProductCarouselSectionData -> "product_carousel-${cardCarouselData.header}-$index"
        is ProductFilterSectionData -> FILTER_SECTION_KEY
    }
}
