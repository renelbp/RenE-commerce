package com.reneprojects

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

internal interface RetrofitService {
    @GET("products")
    suspend fun getProductResponse(): ProductResponse
}

internal object RetrofitClient {
    private const val BASE_URL = "https://dummyjson.com/"
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService: RetrofitService = retrofitInstance.create(RetrofitService::class.java)
}