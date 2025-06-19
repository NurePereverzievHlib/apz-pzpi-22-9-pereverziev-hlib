package com.example.orthovision.data.model

data class AppointmentTime(
    val id: Int,
    val doctor_id: Int,
    val clinic_id: Int,
    val available_time: String,
    val is_booked: Boolean,
    val created_at: String,
    val updated_at: String
)

