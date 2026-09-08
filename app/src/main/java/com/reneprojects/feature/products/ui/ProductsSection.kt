package com.reneprojects.feature.products.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.reneprojects.feature.products.model.ProductCarousel
import com.reneprojects.feature.products.ui.components.CarouselSection

@Composable
internal fun ProductsSection(
    productCarousels: List<ProductCarousel>,
    modifier: Modifier,
) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        items(items = productCarousels, key = { it.header.linkId }) { productCarousel ->
            CarouselSection(section = productCarousel)
        }
    }
}