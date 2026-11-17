package com.reneprojects.feature.products.model

import com.reneprojects.components.productcard.model.ProductCardData

internal data class LandingUiState(
    val landingPageData: List<LandingPageData> = emptyList(),
    val filteredProducts: List<ProductCardData> = emptyList(),
    val isLoading: Boolean = true,
    val isFilterLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
)
