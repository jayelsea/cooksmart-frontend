package com.example.cooksmart.model

data class TheMealDbResponse(
    val meals: List<TheMealDbRecipe>?
)

data class TheMealDbRecipe(
    val idMeal: String?,
    val strMeal: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?
)

