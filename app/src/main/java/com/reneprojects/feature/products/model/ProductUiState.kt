package com.reneprojects.feature.products.model

internal data class ProductsUiState(
    val productCarousels: List<ProductCarousel> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
)