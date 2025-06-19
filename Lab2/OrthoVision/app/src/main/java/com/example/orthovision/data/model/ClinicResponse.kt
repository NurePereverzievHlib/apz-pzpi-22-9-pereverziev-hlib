package com.example.orthovision.data.model

import com.google.gson.annotations.SerializedName

data class ClinicResponse(
    @SerializedName("Name") val name: String,
    @SerializedName("Address") val address: String,
    @SerializedName("Phone") val phone: String,
    @SerializedName("Location") val location: String,
    @SerializedName("ID") val id: String
)

fun ClinicResponse.toClinic(): Clinic {
    return Clinic(
        id = id,
        name = name,
        address = address,
        phone = phone,
        location = location
    )
}
