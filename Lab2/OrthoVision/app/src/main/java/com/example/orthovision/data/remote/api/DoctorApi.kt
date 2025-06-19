package com.example.orthovision.data.remote.api

import com.example.orthovision.data.model.Doctor
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.orthovision.data.model.DoctorsResponse

interface DoctorApi {
    @GET("clinics/{clinicId}/doctors")
    suspend fun getDoctorsByClinic(@Path("clinicId") clinicId: String): DoctorsResponse

    @GET("doctors/{id}")
    suspend fun getDoctorById(@Path("id") id: String): Doctor
}

