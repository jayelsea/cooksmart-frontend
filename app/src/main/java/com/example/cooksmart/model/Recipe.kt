package com.example.cooksmart.model

import java.io.Serializable

// Ajusta los campos según la respuesta real de tu backend
 data class Recipe(
    val id: Long,
    val title: String,
    val description: String?,
    val ingredients: List<String>?,
    val instructions: String?,
    val imageUrl: String?
) : Serializable
