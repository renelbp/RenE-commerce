package com.reneprojects.feature.products.model

internal data class ProductsUiState(
    val products: List<ProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
)