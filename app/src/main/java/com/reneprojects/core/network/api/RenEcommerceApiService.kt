package com.reneprojects.core.network.api

import com.reneprojects.core.feature.categories.remote.dto.ProductCategoryDto
import com.reneprojects.core.feature.products.remote.dto.ProductsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Single Retrofit service for the whole API.
 *
 * The base URL is shared, so every new endpoint is added here as a new function instead of
 * creating a service per feature. Use the `If-None-Match` header to keep `ETag` caching.
 */
internal interface RenEcommerceApiService {
    @GET("products")
    suspend fun getProductsResponse(
        @Header("If-None-Match") eTag: String? = null
    ): Response<ProductsResponseDto>

    @GET("products/categories")
    suspend fun getProductCategoriesResponse(
        @Header("If-None-Match") eTag: String? = null
    ): Response<List<ProductCategoryDto>>
}
