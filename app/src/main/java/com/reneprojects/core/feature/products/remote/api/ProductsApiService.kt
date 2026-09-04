package com.reneprojects.core.feature.products.remote.api

import com.reneprojects.core.feature.products.remote.dto.ProductsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

internal interface ProductsApiService {
    @GET("products")
    suspend fun getProductResponse(
        @Header("If-None-Match") eTag: String? = null
    ): Response<ProductsResponseDto>
}