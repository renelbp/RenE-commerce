package com.reneprojects.feature.products.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.reneprojects.components.loadingspinner.LoadingSpinner
import com.reneprojects.components.productcarousel.model.ProductCarousel
import com.reneprojects.components.productcarousel.view.CarouselSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsSection(
    productCarousels: List<ProductCarousel>,
    modifier: Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navigateToProductCategory: (Int) -> Unit,
    navigateToProductDetails: (Int) -> Unit
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
            items(
                items = productCarousels,
                key = { it.header.label + it.header.linkId }) { productCarousel ->
                CarouselSection(
                    section = productCarousel,
                    navigateToProductCategory = navigateToProductCategory,
                    navigateToProductDetails = navigateToProductDetails
                )
            }
        }
    }
}
