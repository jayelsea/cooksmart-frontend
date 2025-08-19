package com.example.cooksmart.model

data class CocktailResponse(
    val drinks: List<Cocktail>
)

data class Cocktail(
    val idDrink: String,
    val strDrink: String,
    val strCategory: String?,
    val strInstructions: String?,
    val strDrinkThumb: String?
)

