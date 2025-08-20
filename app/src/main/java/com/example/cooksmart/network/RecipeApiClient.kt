package com.example.cooksmart.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeApiClient {
    private const val BASE_URL = "http://192.168.1.64:8080/"

    val apiService: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }
}

