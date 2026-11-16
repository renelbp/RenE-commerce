package com.reneprojects.core.feature.categories.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Category returned by `GET /products/categories`.
 *
 * Fields are nullable with defaults on purpose: a missing or malformed entry must not break the
 * whole sync. The `url` field from the API is intentionally not modelled because it is unused.
 */
internal data class ProductCategoryDto(
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("name")
    val name: String? = null
)
