package com.example.orthovision.data.model

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)
