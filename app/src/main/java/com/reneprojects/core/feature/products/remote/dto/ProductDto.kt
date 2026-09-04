package com.reneprojects.core.feature.products.remote.dto

import com.google.gson.annotations.SerializedName

internal data class ProductDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("discountPercentage")
    val discountPercentage: Double? = null,
    @SerializedName("rating")
    val rating: Double? = null,
    @SerializedName("stock")
    val stock: Int? = null,
    @SerializedName("brand")
    val brand: String? = null
)