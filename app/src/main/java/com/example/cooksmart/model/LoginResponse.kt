package com.example.cooksmart.model

data class LoginResponse(
    val success: Boolean,
    val userId: Long?,
    val message: String?
)

