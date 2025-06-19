package com.example.orthovision.data.model

data class AppointmentWithDiseases(
    val appointment: Appointment,
    val diseases: List<Disease>?
)

data class Appointment(
    val ID: Int,
    val AppointmentTimeID: Int,
    val PatientID: Int,
    val Status: String,
    val Reason: String,
    val CreatedAt: String
)

data class Disease(
    val ID: Int,
    val AppointmentID: Int,
    val DiseaseName: String,
    val Description: String,
    val DiagnosisDate: String,
    val Status: String
)
