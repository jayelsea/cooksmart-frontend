package com.example.cooksmart.data

import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes")
    suspend fun getRecipes(
        @Query("ingredient") ingredient: String?,
        @Query("country") country: String?
    ): List<Recipe>
}
