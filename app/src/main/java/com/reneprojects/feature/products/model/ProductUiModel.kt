package com.reneprojects.feature.products.model

internal data class ProductUiModel(
    val id: Int,
    val title: String,
    val formattedPrice: String,
    val imageUrl: String,
)