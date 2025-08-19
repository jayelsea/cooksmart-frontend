package com.example.cooksmart.model

data class RegisterResponse(
    val success: Boolean,
    val userId: Long?,
    val message: String?
)

