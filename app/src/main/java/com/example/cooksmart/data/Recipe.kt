package com.example.cooksmart.data

data class Recipe(
    val id: String,
    val name: String,
    val country: String,
    val ingredients: List<String>,
    val imageUrl: String?,
    val description: String
)
