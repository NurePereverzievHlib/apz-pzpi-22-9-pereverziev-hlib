package com.example.orthovision.data.remote.api

import com.example.orthovision.data.model.LoginRequest
import com.example.orthovision.data.model.LoginResponse
import com.example.orthovision.data.model.RegisterRequest
import com.example.orthovision.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): RegisterResponse
}
