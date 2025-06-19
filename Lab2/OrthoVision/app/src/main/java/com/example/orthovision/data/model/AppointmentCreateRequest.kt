package com.example.orthovision.data.model

import com.google.gson.annotations.SerializedName

data class AppointmentCreateRequest(
    @SerializedName("AppointmentTimeID")
    val appointmentTimeId: Int,
    @SerializedName("PatientID")
    val patientId: Int,
    @SerializedName("Reason")
    val reason: String
)