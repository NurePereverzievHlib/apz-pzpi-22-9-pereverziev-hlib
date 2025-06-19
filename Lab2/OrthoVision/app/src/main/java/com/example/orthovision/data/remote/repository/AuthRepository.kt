package com.example.orthovision.data.remote.repository

import com.example.orthovision.data.model.LoginRequest
import com.example.orthovision.data.model.LoginResponse
import com.example.orthovision.data.model.RegisterRequest
import com.example.orthovision.data.model.RegisterResponse
import com.example.orthovision.data.remote.api.AuthApi

class AuthRepository(private val api: AuthApi) {
    suspend fun login(email: String, password: String): LoginResponse {
        return api.login(LoginRequest(email, password))
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.registerUser(request)
    }
}

