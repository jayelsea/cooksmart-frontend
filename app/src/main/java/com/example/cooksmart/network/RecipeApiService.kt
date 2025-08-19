package com.example.cooksmart.network

import com.example.cooksmart.model.Recipe
import com.example.cooksmart.model.LoginRequest
import com.example.cooksmart.model.LoginResponse
import com.example.cooksmart.model.RegisterRequest
import com.example.cooksmart.model.RegisterResponse
import com.example.cooksmart.model.TheMealDbResponse
import retrofit2.Response
import retrofit2.http.*

interface RecipeApiService {
    @GET("api/recipes")
    suspend fun getAllRecipes(): Response<List<Recipe>>

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): Response<Recipe>

    @POST("api/recipes")
    suspend fun createRecipe(@Body recipe: Recipe): Response<Recipe>

    @PUT("api/recipes/{id}")
    suspend fun updateRecipe(@Path("id") id: Long, @Body recipe: Recipe): Response<Recipe>

    @DELETE("api/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Long): Response<Unit>

    @GET("api/recipes/external")
    suspend fun getExternalRecipes(@Query("ingredients") ingredients: String): Response<List<Recipe>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // Recetas aleatorias desde TheMealDB
    @GET("https://www.themealdb.com/api/json/v1/1/random.php")
    suspend fun getRandomRecipe(): Response<TheMealDbResponse>
}
