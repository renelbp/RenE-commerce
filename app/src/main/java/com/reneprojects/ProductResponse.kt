package com.reneprojects

internal class ProductResponse(
    val products: List<Product>
)

internal data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val thumbnail: String
)
