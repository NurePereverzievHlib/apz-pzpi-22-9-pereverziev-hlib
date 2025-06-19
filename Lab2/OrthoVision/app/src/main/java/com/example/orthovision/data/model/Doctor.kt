package com.example.orthovision.data.model

import com.google.gson.annotations.SerializedName

data class Doctor(
    @SerializedName("ID") val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PasswordHash") val passwordHash: String,
    @SerializedName("Role") val role: String,
    @SerializedName("Specialization") val specialization: String,
    @SerializedName("AvatarURL") val avatarUrl: String?,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("Password") val password: String
)

