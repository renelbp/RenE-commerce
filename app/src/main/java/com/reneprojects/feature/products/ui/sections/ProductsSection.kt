package com.reneprojects.feature.products.ui.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val GRID_COLUMNS = 2
private const val SCREEN_HORIZONTAL_PADDING = 16
private const val CARD_SPACING = 8
private const val CONTENT_TOP_PADDING = 8
private const val CONTENT_FADE_DURATION_MS = 200
private const val MIN_SKELETON_DURATION_MS = 350L

private enum class ProductsContent {
    Carousels,
    Filtering,
    Filtered,
}

@Composable
internal fun ProductsSection(
    landingPageData: List<LandingPageData>,
    filteredProducts: List<ProductCardData>,
    isFilterLoading: Boolean,
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
    val isFiltered = selectedCategory != null && filterSection != null

    // Keyed on `isFiltered`: re-entering from the carousels restarts the minimum, while switching
    // categories keeps it. Saveable so a rotation does not replay the skeleton.
    var isMinimumSkeletonTimeElapsed by rememberSaveable(isFiltered) {
        mutableStateOf(!isFiltered)
    }

    LaunchedEffect(isFiltered) {
        if (isFiltered && !isMinimumSkeletonTimeElapsed) {
            delay(MIN_SKELETON_DURATION_MS.milliseconds)
            isMinimumSkeletonTimeElapsed = true
        }
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            gridState.scrollToItem(0)
        } else {
            listState.scrollToItem(0)
        }
    }

    val contentMode = when {
        !isFiltered -> ProductsContent.Carousels
        isFilterLoading || !isMinimumSkeletonTimeElapsed -> ProductsContent.Filtering
        else -> ProductsContent.Filtered
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
        Column(modifier = Modifier.fillMaxSize()) {
            filterSection?.let { section ->
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    ProductFilterSection(
                        filters = section.filters,
                        selectedSlug = selectedCategory,
                        onFilterSelected = onFilterSelected
                    )
                }
            }

            AnimatedContent(
                targetState = contentMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(CONTENT_FADE_DURATION_MS)
                    ) togetherWith fadeOut(
                        animationSpec = tween(CONTENT_FADE_DURATION_MS)
                    )
                },
                label = "products_content"
            ) { productsContent ->
                when (productsContent) {
                    ProductsContent.Carousels -> LandingSectionsList(
                        landingPageData = landingPageData,
                        listState = listState,
                        navigateToProductCategory = navigateToProductCategory,
                        navigateToProductDetails = navigateToProductDetails,
                    )

                    ProductsContent.Filtering -> LoadingFilteredProductsSkeleton()

                    ProductsContent.Filtered -> FilteredProductsGrid(
                        products = filteredProducts,
                        gridState = gridState,
                        navigateToProductDetails = navigateToProductDetails,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilteredProductsGrid(
    products: List<ProductCardData>,
    gridState: LazyGridState,
    navigateToProductDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier.fillMaxSize(),
        state = gridState,
        contentPadding = PaddingValues(
            start = SCREEN_HORIZONTAL_PADDING.dp,
            end = SCREEN_HORIZONTAL_PADDING.dp,
            top = CONTENT_TOP_PADDING.dp,
            bottom = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
        verticalArrangement = Arrangement.spacedBy(CARD_SPACING.dp),
    ) {
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
    listState: LazyListState,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit,
) {
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
        is ProductFilterSectionData -> "product_filter-$index"
    }
}
