package com.reneprojects.core.feature.products.remote.dto

import com.google.gson.annotations.SerializedName

internal data class ProductsResponseDto(
    @SerializedName("products")
    val products: List<ProductDto> = emptyList(),
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("skip")
    val skip: Int = 0,
    @SerializedName("limit")
    val limit: Int = 0
)