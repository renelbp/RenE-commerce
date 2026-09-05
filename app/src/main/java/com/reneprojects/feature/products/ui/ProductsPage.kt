package com.reneprojects.feature.products.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reneprojects.feature.products.viewmodel.ProductViewModel

@Composable
internal fun PageContent(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.products.isNotEmpty() -> {
            ProductsSection(modifier = modifier, productList = uiState.products)
        }
    }

}
