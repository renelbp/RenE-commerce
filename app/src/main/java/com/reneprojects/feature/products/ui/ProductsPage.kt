package com.reneprojects.feature.products.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reneprojects.feature.products.ui.viewmodel.ProductViewModel

@Composable
internal fun PageContent(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    when {
        uiState.productCarousels.isNotEmpty() -> {
            ProductsSection(
                modifier = modifier,
                productCarousels = uiState.productCarousels,
                navigateToProductCategory = {// TODO IMPLEMENT NAVIGATION WITH NavGraph
                    Toast.makeText(context, "Navigation To Category Page", Toast.LENGTH_SHORT)
                        .show()
                },
                navigateToProductDetails = {// TODO IMPLEMENT NAVIGATION WITH NavGraph
                    Toast.makeText(
                        context,
                        "Navigation To Product Details Page",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
        }
        // TODO: Handle loading and error states (isLoading, errorMessage)
    }
}
