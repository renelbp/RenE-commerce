package com.reneprojects.core.feature.products.remote.api

import com.reneprojects.core.feature.products.remote.dto.ProductsResponseDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ProductsApiService {
    @GET("products")
    suspend fun getProductResponse(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): ProductsResponseDto
}