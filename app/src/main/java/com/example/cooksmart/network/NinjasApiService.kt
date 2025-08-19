package com.example.cooksmart.network

import com.example.cooksmart.model.NinjasRecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NinjasApiService {

    @GET("recipe?query=kids")
    suspend fun getKidsRecipes(
        @Header("X-Api-Key") apiKey: String = "wu9+mB3bHelwVr1iBz4Qgw==9z4obDVH320tZgHK"
    ): Response<List<NinjasRecipeResponse>>


}
