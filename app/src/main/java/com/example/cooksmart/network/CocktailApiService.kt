package com.example.cooksmart.network

import com.example.cooksmart.model.CocktailResponse
import retrofit2.Response
import retrofit2.http.GET

interface CocktailApiService {
    // Obtener bebida aleatoria
    @GET("random.php")
    suspend fun getRandomCocktail(): Response<CocktailResponse>

    // Buscar bebidas por nombre
    @GET("search.php")
    suspend fun searchCocktailByName(@retrofit2.http.Query("s") name: String): Response<CocktailResponse>

    // Buscar bebidas por ingrediente
    @GET("filter.php")
    suspend fun searchCocktailByIngredient(@retrofit2.http.Query("i") ingredient: String): Response<CocktailResponse>

    // Puedes agregar más métodos según lo necesites, por ejemplo búsqueda por categoría, etc.
}
