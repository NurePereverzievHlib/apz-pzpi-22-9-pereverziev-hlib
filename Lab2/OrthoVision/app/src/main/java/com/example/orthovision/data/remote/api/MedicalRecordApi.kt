package com.example.orthovision.data.remote.api

import com.example.orthovision.data.model.AppointmentWithDiseases
import retrofit2.http.GET
import retrofit2.http.Path

interface MedicalRecordApi {
    @GET("medical-record/{userID}")
    suspend fun getMedicalRecords(@Path("userID") userId: Int): MedicalRecordResponse
}

data class MedicalRecordResponse(
    val message: String,
    val data: List<AppointmentWithDiseases>?
)
