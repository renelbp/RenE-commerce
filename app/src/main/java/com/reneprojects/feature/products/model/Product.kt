package com.reneprojects.feature.products.model

internal data class Product(
    val id: Int,
    val title: String,
    val formattedPrice: String,
    val imageUrl: String,
    val category: String,
)