package com.reneprojects.feature.products.model

internal data class LandingUiState(
    val landingPageData: List<LandingPageData> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
)